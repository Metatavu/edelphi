package fi.metatavu.edelphi.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

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

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.auth.AuthenticationProviderFactory;
import fi.metatavu.edelphi.auth.GoogleAuthenticationStrategy;
import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.dao.base.DelfoiAuthDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiAuth;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public class GoogleDriveUtils {

  private static GoogleCredential adminCredentials = null;
  private static final String CHARSET = "UTF-8";
  private static final String TEXT_HTML = "text/html";
  private static final Logger logger = Logger.getLogger(GoogleDriveUtils.class.getName());
	private static final String[] REQUIRED_SCOPES = new String[] { "https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/drive.file" };
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final String FILE_FIELDS = "id,kind,mimeType,name,parents,createdTime,modifiedTime,imageMediaMetadata,videoMediaMetadata,trashed,webViewLink";

	// Service

	public static Drive getAuthenticatedService(RequestContext requestContext) {
		switch (resolveRequiredAuthLevel(requestContext)) {
			case FULL:
				handleAuthLevelFull(requestContext);
			break;
			case GRANT:
				handleAuthLevelGrant(requestContext);
			break;
			case REFRESH:
			  // TODO: Refresh token
			break;
			case NONE:
				AuthSource googleAuthSource = GoogleDriveUtils.getGoogleAuthSource(RequestUtils.getDelfoi(requestContext));
				GoogleAuthenticationStrategy googleAuthenticationProvider = (GoogleAuthenticationStrategy) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(googleAuthSource);
				GoogleCredential credential = googleAuthenticationProvider.getCredential(requestContext, REQUIRED_SCOPES);
				return new Drive.Builder(TRANSPORT, JSON_FACTORY, credential).build();
			default:
		}

		return null;
	}

	public static Drive getAdminService() {
		return new Drive.Builder(TRANSPORT, JSON_FACTORY, getAdminCredential()).build();
	}
	
	public static String getAdminAccountId() {
	  return getAdminCredential().getServiceAccountId();
	}
	
	private static synchronized GoogleCredential getAdminCredential() {
	  String keyFile = System.getProperty("edelphi.googleServiceAccount.key");
	  if (keyFile == null) {
	    logger.severe("Google service account keyfile is not configured");
	    return null;
	  }
	  
	  try {
  		if (adminCredentials != null) {
  			return refreshCredentials(adminCredentials);
  		}
  
  		adminCredentials = GoogleCredential
  	    .fromStream(new FileInputStream(keyFile))
  	    .createScoped(Arrays.asList(REQUIRED_SCOPES));
  			
  		return adminCredentials;
	  } catch (IOException e) {
	    logger.log(Level.SEVERE, "Failed to create admin service credentials", e);
	    return null;
	  }
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
	
	public static AuthSource getGoogleAuthSource(Delfoi delfoi) {
    // TODO needs more finesse; at this point we simply return the first Google auth in Delfoi and assume one exists in the first place
		// TODO Support for panel Google auth
		
    DelfoiAuthDAO delfoiAuthDAO = new DelfoiAuthDAO();
    List<DelfoiAuth> delfoiAuths = delfoiAuthDAO.listByDelfoi(delfoi);
    for (DelfoiAuth delfoiAuth : delfoiAuths) {
      if ("Google".equals(delfoiAuth.getAuthSource().getStrategy())) {
        return delfoiAuth.getAuthSource();
      }
    }
    return null;
  }

	public static RequiredAuthLevel resolveRequiredAuthLevel(RequestContext requestContext) {
    RequiredAuthLevel requiredAuthLevel = RequiredAuthLevel.NONE;
    
    if (!AuthUtils.isAuthenticatedBy(requestContext, "Google")) {
      // User is not authenticated by Google OAuth so we need to do full authentication 
      requiredAuthLevel = RequiredAuthLevel.FULL;
    } else {
      OAuthAccessToken accessToken = AuthUtils.getOAuthAccessToken(requestContext, "Google", REQUIRED_SCOPES);
      if (accessToken == null) {
        // User is authenticated with Google and has a valid token but has not granted usage of documentlist api so we need him/her to do that before proceeding
        requiredAuthLevel = RequiredAuthLevel.GRANT;
      } else if (AuthUtils.isOAuthTokenExpired(accessToken)) {
        // User's access token has expired so we need to request new one
        requiredAuthLevel = RequiredAuthLevel.REFRESH;
      }
    }
    
    return requiredAuthLevel;
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
	
  private static void handleAuthLevelFull(RequestContext requestContext) {
    try {
      AuthSource authSource = GoogleDriveUtils.getGoogleAuthSource(RequestUtils.getDelfoi(requestContext));
      StringBuilder redirectUrlBuilder = new StringBuilder(RequestUtils.getBaseUrl(requestContext.getRequest()))
      	.append("/dologin.page?authSource=")
    	  .append(authSource.getId());
    	  
    	for (String extraScope : REQUIRED_SCOPES) {
    		redirectUrlBuilder.append("&extraScope=");
    	  redirectUrlBuilder.append(URLEncoder.encode(extraScope, CHARSET));
    	}
    	  
      AuthUtils.storeRedirectUrl(requestContext, RequestUtils.getCurrentUrl(requestContext.getRequest(), true));
      requestContext.setRedirectURL(redirectUrlBuilder.toString());
    } catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
  
  private static void handleAuthLevelGrant(RequestContext requestContext) {
    try {
      AuthSource authSource = GoogleDriveUtils.getGoogleAuthSource(RequestUtils.getDelfoi(requestContext));
      
      StringBuilder redirectUrlBuilder = new StringBuilder(RequestUtils.getBaseUrl(requestContext.getRequest()))
    	  .append("/dologin.page?authSource=")
  	    .append(authSource.getId());
  	  
    	for (String extraScope : REQUIRED_SCOPES) {
    		redirectUrlBuilder.append("&scope=");
    	  redirectUrlBuilder.append(URLEncoder.encode(extraScope, CHARSET));
    	}
      
      AuthUtils.storeRedirectUrl(requestContext, RequestUtils.getCurrentUrl(requestContext.getRequest(), true));
      requestContext.setRedirectURL(redirectUrlBuilder.toString());
    } catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
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
  
  private enum RequiredAuthLevel {
    NONE,
    REFRESH,
    GRANT,
    FULL
  }

}
