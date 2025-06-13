package fi.metatavu.edelphi.utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.services.drive.DriveScopes;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import fi.metatavu.edelphi.auth.AuthenticationProviderFactory;
import fi.metatavu.edelphi.auth.KeycloakAuthenticationStrategy;
import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public class GoogleDriveUtils {

  private static GoogleCredential adminCredentials = null;
  private static final String CHARSET = "UTF-8";
  private static final String TEXT_HTML = "text/html";
  private static final Logger logger = Logger.getLogger(GoogleDriveUtils.class.getName());
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final String FILE_FIELDS = "id,kind,mimeType,name,parents,createdTime,modifiedTime,imageMediaMetadata,videoMediaMetadata,trashed,webViewLink";
  private static final String FAKE_ID = "FAKE";
  
	// Service

	public static Drive getAuthenticatedService(RequestContext requestContext) {
	  AuthSource keycloakAuthSource = AuthUtils.getAuthSource("Keycloak");
	  if (keycloakAuthSource == null) {
	    logger.log(Level.SEVERE, "Could not obtain authenticated Drive because keycloak auth source is not configured");
	    return null;
	  }
    
    OAuthAccessToken brokerToken = getBrokerToken(requestContext, keycloakAuthSource);
    if (brokerToken != null) {
      GoogleCredential credential = getCredential(brokerToken);
      try {
        Drive drive = new Drive.Builder(TRANSPORT, JSON_FACTORY, credential).build();
        listFiles(drive, 1);
        return drive;
      } catch (IOException e) {
        logger.log(Level.WARNING, "Failed to get Google Access Token", e);
      }      
    }
    
    requestGoogleLogin(requestContext, keycloakAuthSource);
    return null;
  }

	/**
	 * Returns Google broker token from Keycloak
	 * 
	 * @param requestContext request context
	 * @param keycloakAuthSource Keycloak auth source
	 * @return Google broker token or null if not found
	 */
  private static OAuthAccessToken getBrokerToken(RequestContext requestContext, AuthSource keycloakAuthSource) {
    KeycloakAuthenticationStrategy keycloakAuthenticationProvider = (KeycloakAuthenticationStrategy) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(keycloakAuthSource);
    return keycloakAuthenticationProvider.getBrokerToken(requestContext, "google");
  }

  private static void requestGoogleLogin(RequestContext requestContext, AuthSource keycloakAuthSource) {
    String currentUrl = RequestUtils.getCurrentUrl(requestContext.getRequest(), true);
    try {
      StringBuilder loginUrlBuilder = new StringBuilder(RequestUtils.getBaseUrl(requestContext.getRequest()))
        .append("/dologin.page?authSource=")
        .append(keycloakAuthSource.getId())
        .append("&hint=google")
        .append(String.format("&redirectUrl=%s", URLEncoder.encode(currentUrl, "UTF-8")));
      requestContext.setRedirectURL(String.format("/logout.page?redirectUrl=%s", URLEncoder.encode(loginUrlBuilder.toString(), "UTF-8")));
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "Failed to encode logout redirect url", e);
    }
  }

	public static Drive getAdminService() {
		return new Drive.Builder(TRANSPORT, JSON_FACTORY, getAdminCredential()).build();
	}
	
	public static String getAdminAccountId() {
	  return getAdminCredential().getServiceAccountId();
	}
	
  private static GoogleCredential getCredential(OAuthAccessToken brokerToken) {
    Details webDetails = new Details();
    webDetails.setClientId(FAKE_ID);
    webDetails.setClientSecret(FAKE_ID);
    
    GoogleClientSecrets secrets = new GoogleClientSecrets();
    secrets.setWeb(webDetails);

    GoogleCredential credential = new GoogleCredential.Builder()
      .setClientSecrets(secrets)
      .setTransport(TRANSPORT)
      .setJsonFactory(JSON_FACTORY)
      .build();
    
    if (brokerToken != null && brokerToken.getAccessToken() != null) {
      credential.setAccessToken(brokerToken.getAccessToken());
    }

    if (brokerToken != null && brokerToken.getRefreshToken() != null) {
      credential.setRefreshToken(brokerToken.getRefreshToken());
    }
    
    return credential;
  }

  private static GoogleCredential getAdminCredential() {
    try {
  		if (adminCredentials != null) {
  			return refreshCredentials(adminCredentials);
  		}
  		
  		String keyFile = System.getProperty("edelphi.googleServiceAccount.key");
      if (!isExistingFile(keyFile)) {
        keyFile = System.getenv("GOOGLE_SERVICE_ACCOUNT_KEY");
      }

      if (!isExistingFile(keyFile)) {
        keyFile = "/opt/google-service-account.json";
      }

      if (!isExistingFile(keyFile)) {
        logger.log(Level.SEVERE, "Google service account keyfile is not configured");
      }
  
  		adminCredentials = GoogleCredential
  	    .fromStream(new FileInputStream(keyFile))
  	    .createScoped(Arrays.asList(DriveScopes.DRIVE_READONLY));
  			
  		return adminCredentials;
	  } catch (IOException e) {
	    logger.log(Level.SEVERE, "Failed to create admin service credentials", e);
	    return null;
	  }
  }

  /**
   * Returns whether file exists or not
   * 
   * @param path file path
   * @return whether file exists or not
   */
  private static boolean isExistingFile(String path) {
    if (path == null) {
      return false;
    }
    
    java.io.File file = new java.io.File(path);
    return file.exists();
  }

  public static GoogleCredential refreshCredentials(GoogleCredential credential) {
    long tokenExpiresIn = credential.getExpirationTimeMilliseconds() != null ? credential.getExpirationTimeMilliseconds() - System.currentTimeMillis() : -1;
    
    if (tokenExpiresIn <= 0) {
    	try {
        if (credential.refreshToken()) {
          return credential;
        }
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to refresh admin service credentials", e);
      }
    } else {
    	return credential;
    }
    
    return null;
  }

	// Files
	
	public static File getFile(Drive drive, String fileId) throws IOException {
		return drive.files().get(fileId).setFields(FILE_FIELDS).execute();
	}
	
	public static String insertFile(Drive drive, String title, String description, String parentId, String mimeType, byte[] content, int retryCount) throws IOException {
    File file = null;
    int retries = 0;
    while (file == null) {
    	try {
      	file = tryInsertFile(drive, title, description, parentId, mimeType, content);
      } catch (IOException e) {
      	if (retries >= retryCount) {
      		throw e;
      	} else {
      		retries++;
      	}
      }
    }
    
    return file.getId();
  }
  
  public static String insertFolder(Drive drive, String title, String description, String parentId, int retryCount) throws IOException {
  	return insertFile(drive, title, description, parentId, "application/vnd.google-apps.folder", null, retryCount);
  }
  
  public static FileList listFiles(Drive drive, Integer maxResults) throws IOException {
  	return drive.files()
  	    .list()
  	    .setPageSize(maxResults)
  	    .execute();
  }
  
  public static FileList listFiles(Drive drive, String q) throws IOException {
  	return drive.files().list().setQ(q).execute();
  }

	public static void deleteFile(Drive drive, File file) throws IOException {
		drive.files().delete(file.getId()).execute();
	}
	
	// Permissions

  public static PermissionList listPermissions(Drive drive, String fileId) throws IOException {
  	return drive.permissions().list(fileId).execute();
  }

  public static Permission insertPermission(Drive drive, String fileId, Permission permission) throws IOException {
		return drive.permissions().create(fileId, permission).execute();
	}
  
  public static Permission insertUserPermission(Drive drive, String fileId, String userEmail, String permission) throws IOException {
  	Permission permissionObject = new Permission();
  	permissionObject.setEmailAddress(userEmail);
  	permissionObject.setType("user");
  	permissionObject.setRole(permission);
    return GoogleDriveUtils.insertPermission(drive, fileId, permissionObject);
  }
  
  
  public static void deletePermission(Drive drive, String fileId, Permission permission) throws IOException {
  	drive.permissions().delete(fileId, permission.getId()).execute();
  }
  
	public static Permission publishFileWithLink(Drive drive, File file) throws IOException {
  	Permission permission = new Permission();
  	
  	permission.setType("anyone");
  	permission.setRole("reader");
  	
  	return insertPermission(drive, file.getId(), permission);
  }
	
	/**
	 * Exports file in requested format
	 * 
	 * @param drive drive client
	 * @param file file to be exported
	 * @param format target format
	 * @return exported data
   * @throws IOException thrown when exporting fails unexpectedly
	 */
	public static DownloadResponse exportFile(Drive drive, File file, String format) throws IOException {
	  return exportFile(drive, file.getId(), format);
	}

  /**
   * Exports file in requested format
   * 
   * @param drive drive client
   * @param fileId id of file to be exported
   * @param format target format
   * @return exported data
   * @throws IOException thrown when exporting fails unexpectedly
   */
  public static DownloadResponse exportFile(Drive drive, String fileId, String format) throws IOException {
    try (InputStream inputStream = drive.files().export(fileId, format).executeAsInputStream()) {
      return new DownloadResponse(format, IOUtils.toByteArray(inputStream));
    }
  }
  
  /**
   * Returns web view link for the file
   * 
   * @param file file 
   * @return web view link for the file
   */
  public static String getWebViewLink(File file) {
    if (file == null) {
      logger.log(Level.SEVERE, "Could not get file url for null file");
      return null;
    }
    
    return file.getWebViewLink();
  }

  /**
   * Returns web view link for the  file
   * 
   * @param drive drive client
   * @param fileId file id 
   * @return web view link for the  file
   */
  public static String getWebViewLink(Drive drive, String fileId) {
    try {
      return getWebViewLink(getFile(drive, fileId));
    } catch (IOException e) {
      logger.log(Level.SEVERE, String.format( "Failed to build Google Drive URL for file %s",  fileId), e);
    }
    
    return null;
  }
  
  /**
   * Exports a spreadsheet in HTML format
   * 
   * @param drive drive client
   * @param fileId file id for file to be exported
   * @return Spreadsheet in HTML format
   * @throws IOException thrown when exporting fails unexpectedly
   */
	public static DownloadResponse exportSpreadsheet(Drive drive, String fileId) throws IOException {
	  try (InputStream inputStream = drive.files().export(fileId, "text/csv").executeAsInputStream()) {
	    CSVRenderer csvRenderer = new CSVRenderer(inputStream, "google-spreadsheet", true);
	    return new DownloadResponse(TEXT_HTML, csvRenderer.renderHtmlTable().getBytes("UTF-8"));
    }
	}
  
  /**
   * Exports a spreadsheet in HTML format
   * 
   * @param drive drive client
   * @param file file to be exported
   * @return Spreadsheet in HTML format
   * @throws IOException thrown when exporting fails unexpectedly
   */
  public static DownloadResponse exportSpreadsheet(Drive drive, File file) throws IOException {
    return exportSpreadsheet(drive, file.getId());
  }
	
	/**
	 * Downloads file from Google Drive
	 * 
	 * @param drive drive client
	 * @param fileId file id for file to be downloaded
	 * @return file content
	 * @throws IOException an IOException
	 */
	public static DownloadResponse downloadFile(Drive drive, String fileId) throws IOException {
	  Get request = drive.files().get(fileId).setAlt("media");
	  String contentType = request.executeUsingHead().getContentType();
	  if (StringUtils.isNotBlank(contentType)) {
  	  try (InputStream inputStream = request.executeAsInputStream()) {
	      return new DownloadResponse(contentType, IOUtils.toByteArray(inputStream));
      }
	  }
	  
	  return null;
	}

  /**
   * Downloads file from Google Drive
   * 
   * @param drive drive client
   * @param file file to be downloaded
   * @return file content
   * @throws IOException an IOException
   */
  public static DownloadResponse downloadFile(Drive drive, File file) throws IOException {
    if (file == null) {
      return null;
    }
    
    return downloadFile(drive, file.getId());
  }
  
	public static String extractGoogleDocumentContent(byte[] rawData) {
    try {
      return Jsoup.parse(IOUtils.toString(rawData, CHARSET)).select("body").html();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not read html", e);
    }
    
    return null;
  }
  
  public static String extractGoogleDocumentStyleSheet(byte[] rawData) {
    String rawCss = extractRawCss(rawData);
      
    try {
      CSSStyleSheet styleSheet = CSSUtils.parseStylesheet(rawCss);
      
      for (int i = 0, l = styleSheet.getCssRules().getLength(); i < l; i++) {
        CSSRule cssRule = styleSheet.getCssRules().item(i);
        if (cssRule instanceof CSSStyleRule) {
          prefixDocumentSelector(cssRule);
        }
      }    
      
      return CSSUtils.getStylesheetAsString(styleSheet);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to parse CSS", e);
    }
    
    return null;
  }
  
  public static String getIconLink(File file) {
    if (StringUtils.isNotBlank(file.getIconLink())) {
      return file.getIconLink();
    }
    
    if (StringUtils.startsWith(file.getMimeType(), "image/")) {
      return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_image_x16.png";
    }
    
    switch (file.getMimeType()) {
      case "application/vnd.google-apps.document":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_document_x16.png";
      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
      case "application/vnd.google-apps.spreadsheet":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_spreadsheet_x16.png";
      case "application/vnd.google-apps.folder":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_folder_x16.png";
      case "application/vnd.google-apps.presentation":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_presentation_x16.png";
      case "application/vnd.google-apps.map":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_1_map_x16.png";
      case "application/pdf":
        return "https://ssl.gstatic.com/docs/doclist/images/mediatype/icon_3_pdf_x16.png";
      default:
    }
    
    return "https://ssl.gstatic.com/docs/doclist/images/icon_8_document_list.png";
  }

  private static void prefixDocumentSelector(CSSRule cssRule) {
    CSSStyleRule styleRule = (CSSStyleRule) cssRule;
    if ("body".equals(styleRule.getSelectorText())) {
      styleRule.setSelectorText(".documentContentContainer");
    } else {
      styleRule.setSelectorText(".documentContentContainer " + styleRule.getSelectorText());
    }
  }
  
  private static String extractRawCss(byte[] rawData) {
    try {
      return Jsoup.parse(IOUtils.toString(rawData, CHARSET)).select("head style").html();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not read html", e);
    }
    
    return null;
  }

	private static File tryInsertFile(Drive drive, String title, String description, String parentId, String mimeType, byte[] content) throws IOException {
		// File's metadata
  	
    File body = new File();
    body.setName(title);
    body.setDescription(description);
    body.setMimeType(mimeType);

    if (parentId != null && parentId.length() > 0) {
      body.setParents(Arrays.asList(parentId));
    }
    
    if (content != null) {
    // File's content.
      ByteArrayContent fileContent = new ByteArrayContent(mimeType, content);
      Create create = drive.files().create(body, fileContent);
      create.getMediaHttpUploader().setDirectUploadEnabled(true);
      return create.execute();
    } else {
      return drive.files().create(body).execute();
    }
	}

  
  public static class DownloadResponse {
    
    private String mimeType;
    private byte[] data;
  	
  	public DownloadResponse(String mimeType, byte[] data) {
			this.data = data;
			this.mimeType = mimeType;
		}
  	
  	public byte[] getData() {
			return data;
		}
  	
  	public String getMimeType() {
			return mimeType;
		}
  }

}
