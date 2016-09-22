package fi.metatavu.edelphi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.tidy.Tidy;

import com.google.api.client.auth.oauth2.Credential;
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

  private static final String TEXT_HTML = "text/html";
  private static final Logger logger = Logger.getLogger(GoogleDriveUtils.class.getName());
	private static final String[] REQUIRED_SCOPES = new String[] { "https://www.googleapis.com/auth/drive", "https://www.googleapis.com/auth/drive.file" };
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();

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
		}

		return null;
	}

	public static Drive getAdminService() throws GeneralSecurityException, IOException {
		return new Drive.Builder(TRANSPORT, JSON_FACTORY, getAdminCredential()).build();
	}
	
	private static synchronized Credential getAdminCredential() throws GeneralSecurityException, IOException {
		String keyStorePassword = System.getProperty("edelphi.googleServiceAccount.keyStorePassword");
		String keyPassword = System.getProperty("edelphi.googleServiceAccount.keyPassword");
		String keyStoreAlias = System.getProperty("edelphi.googleServiceAccount.keyStoreAlias");
		String keyStoreFile = System.getProperty("edelphi.googleServiceAccount.keyStoreFile");
		String googleDriveAccountId = SystemUtils.getSettingValue("googleDrive.accountId");
  	String googleDriveAccountUser = SystemUtils.getSettingValue("googleDrive.accountUser");
		PrivateKey key = getPrivateKey(keyStoreFile, keyStorePassword, keyStoreAlias, keyPassword);
		
		if (ADMIN_CREDENTIAL != null) {
			long tokenExpiresIn = ADMIN_CREDENTIAL.getExpirationTimeMilliseconds() != null ? ADMIN_CREDENTIAL.getExpirationTimeMilliseconds() - System.currentTimeMillis() : -1;
			if (tokenExpiresIn <= 0) {
  			if (ADMIN_CREDENTIAL.refreshToken()) {
  			  return ADMIN_CREDENTIAL;
  			}
			} else {
				return ADMIN_CREDENTIAL;
			}
		}

		ADMIN_CREDENTIAL = new GoogleCredential.Builder()
	    .setTransport(TRANSPORT)
      .setJsonFactory(JSON_FACTORY)
      .setServiceAccountId(googleDriveAccountId)
      .setServiceAccountScopes(Arrays.asList(REQUIRED_SCOPES))
      .setServiceAccountPrivateKey(key)
      .setServiceAccountUser(googleDriveAccountUser)
      .build();
			
		return ADMIN_CREDENTIAL;
  }
	
	private static PrivateKey getPrivateKey(String keyStoreFile, String keyStorePassword, String keyAlias, String keyPassword) throws KeyStoreException {
	  KeyStore keyStore = KeyStore.getInstance("PKCS12");
	  
	  try (FileInputStream keyInputStream = new FileInputStream(keyStoreFile)) {
	    keyStore.load(keyInputStream, keyStorePassword.toCharArray());
	    return (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
	  } catch (FileNotFoundException e) {
	    logger.log(Level.SEVERE, String.format("Could not find private key %s", keyStoreFile), e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, String.format("Could not read private key %s", keyStoreFile), e);
    } catch (NoSuchAlgorithmException e) {
      logger.log(Level.SEVERE, String.format("Could not read keystore %s", keyStoreFile), e);
    } catch (CertificateException | UnrecoverableKeyException e) {
      logger.log(Level.SEVERE, String.format("Could not read certificate %s from keystore %s", keyAlias, keyStoreFile), e);
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
		return drive.files().get(fileId).execute();
	}
	
	public static File insertFile(Drive drive, String title, String description, String parentId, String mimeType, byte[] content, int retryCount) throws IOException {
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
    
    return file;
  }
  
  public static File insertFolder(Drive drive, String title, String description, String parentId, int retryCount) throws IOException {
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
  
  public static String getFileUrl(Drive drive, File file) {
    if (file == null) {
      logger.log(Level.SEVERE, "Could not get file url for null file");
      return null;
    }
    
    return getFileUrl(drive, file.getId());
  }

  public static String getFileUrl(Drive drive, String fileId) {
    try {
      return drive.files().get(fileId).buildHttpRequestUrl().build();
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
	  try (InputStream inputStream = drive.files().export(fileId, TEXT_HTML).executeAsInputStream()) {
      return new DownloadResponse(TEXT_HTML, IOUtils.toByteArray(inputStream));
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
	      return new DownloadResponse(TEXT_HTML, IOUtils.toByteArray(inputStream));
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
  
	public static String extractGoogleDocumentContent(byte[] rawData) throws TransformerException, IOException {
    ByteArrayOutputStream contentOutputStream = new ByteArrayOutputStream();
    
    Tidy tidy = new Tidy();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(rawData);
    org.w3c.dom.Document htmlDocument = tidy.parseDOM(inputStream, null);
    Element body = (Element) XPathAPI.selectSingleNode(htmlDocument.getDocumentElement(), "body");
    
    for (int i = 0, l = body.getChildNodes().getLength(); i < l; i++) {
      Node node = body.getChildNodes().item(i);
      tidy.pprint(node, contentOutputStream);
    }

    inputStream.close();
    contentOutputStream.flush();
    contentOutputStream.close();
    
    return new String(contentOutputStream.toByteArray(), "UTF-8");
  }
  
  public static String extractGoogleDocumentStyleSheet(byte[] rawData) throws TransformerException, IOException {
    StringBuilder styleBuilder = new StringBuilder();

    Tidy tidy = new Tidy();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(rawData);
    org.w3c.dom.Document htmlDocument = tidy.parseDOM(inputStream, null);
    
    NodeList styleNodes = XPathAPI.selectNodeList(htmlDocument.getDocumentElement(), "head/style");
    
    for (int i = 0, l = styleNodes.getLength(); i < l; i++) {
      Element styleElement = (Element) styleNodes.item(i);
      for (int j = 0, jl = styleElement.getChildNodes().getLength(); j < jl; j++) {
        Node child = styleElement.getChildNodes().item(j);
        styleBuilder.append(child.getNodeValue());
      }
    }

    inputStream.close();
    
    String rawCss = new String(styleBuilder.toString().getBytes("UTF-8"), "UTF-8");
      
    CSSStyleSheet styleSheet = CSSUtils.parseStylesheet(rawCss);
    
    for (int i = 0, l = styleSheet.getCssRules().getLength(); i < l; i++) {
      CSSRule cssRule = styleSheet.getCssRules().item(i);
      if (cssRule instanceof CSSStyleRule) {
        CSSStyleRule styleRule = (CSSStyleRule) cssRule;
        
        if ("body".equals(styleRule.getSelectorText())) {
          styleRule.setSelectorText(".documentContentContainer");
        } else {
          styleRule.setSelectorText(".documentContentContainer " + styleRule.getSelectorText());
        }
      }
    }    
    
    return CSSUtils.getStylesheetAsString(styleSheet);
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
    	  redirectUrlBuilder.append(URLEncoder.encode(extraScope, "UTF-8"));
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
    	  redirectUrlBuilder.append(URLEncoder.encode(extraScope, "UTF-8"));
    	}
      
      AuthUtils.storeRedirectUrl(requestContext, RequestUtils.getCurrentUrl(requestContext.getRequest(), true));
      requestContext.setRedirectURL(redirectUrlBuilder.toString());
    } catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }

  private static GoogleCredential ADMIN_CREDENTIAL = null;
	
  public static class DownloadResponse {
  	
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
  	
  	private String mimeType;
  	private byte[] data;
  }
  
  private enum RequiredAuthLevel {
    NONE,
    REFRESH,
    GRANT,
    FULL
  }

}
