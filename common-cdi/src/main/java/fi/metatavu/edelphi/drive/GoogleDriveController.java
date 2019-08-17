package fi.metatavu.edelphi.drive;

import java.io.IOException;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

/**
 * Google Drive Controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class GoogleDriveController {
  
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final String FILE_FIELDS = "id,kind,mimeType,name,parents,createdTime,modifiedTime,imageMediaMetadata,videoMediaMetadata,trashed,webViewLink";
  
  @Inject
  private Logger logger;
  
  /**
   * Returns initialized drive client
   * 
   * @param credential Google credentials
   * @return initialized drive client
   */
  public Drive getDrive(GoogleCredential credential) {
    return new Drive.Builder(TRANSPORT, JSON_FACTORY, credential)
      .setApplicationName("eDelphi")
      .build();
  }
  
  /**
   * Inserts a file into Google Drive
   * 
   * @param drive drive instance
   * @param title file title
   * @param description file description
   * @param parentId parent folder id
   * @param mimeType mime type
   * @param content contents
   * @param retryCount maximum retry count
   * @return file id
   * @throws IOException thrown when upload fails
   */
  public String insertFile(Drive drive, String title, String description, String parentId, String mimeType, byte[] content, int retryCount) throws IOException {
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
  
  /**
   * Returns a file from a Google Drive
   * 
   * @param drive drive instance
   * @param fileId file id
   * @return a file
   * @throws IOException thrown when request fails
   */
  public File getFile(Drive drive, String fileId) throws IOException {
    return drive.files().get(fileId).setFields(FILE_FIELDS).execute();
  }
  
  /**
   * Insert permission file permission to an user
   * 
   * @param drive drive instance
   * @param fileId file id
   * @param userEmail target user email
   * @param permission permission name
   * @return created permission
   * @throws IOException thrown when request fails
   */
  public Permission insertUserPermission(Drive drive, String fileId, String userEmail, String permission) throws IOException {
    Permission permissionObject = new Permission();
    permissionObject.setEmailAddress(userEmail);
    permissionObject.setType("user");
    permissionObject.setRole(permission);
    return drive.permissions().create(fileId, permissionObject)
      .setSendNotificationEmail(false)
      .execute();
  }
  
  /**
   * Deletes a permission
   * 
   * @param drive drive instance
   * @param fileId file id
   * @param permission permission
   * @throws IOException
   */
  public void deletePermission(Drive drive, String fileId, Permission permission) throws IOException {
    drive.permissions().delete(fileId, permission.getId()).execute();
  }

  /**
   * Returns web view link for the file
   * 
   * @param file file 
   * @return web view link for the file
   */
  public String getWebViewLink(File file) {
    if (file == null) {
      logger.error("Could not get file url for null file");
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
  public String getWebViewLink(Drive drive, String fileId) {
    try {
      return getWebViewLink(getFile(drive, fileId));
    } catch (IOException e) {
      logger.error(String.format( "Failed to build Google Drive URL for file %s",  fileId), e);
    }
    
    return null;
  }
  
  /**
   * Tries to upload a file into Google Drive
   * 
   * @param drive drive instance
   * @param title file title
   * @param description file description
   * @param parentId parent folder id
   * @param mimeType mime type
   * @param retryCount maximum retry count
   * @return file id
   * @throws IOException thrown when upload fails
   */
  private File tryInsertFile(Drive drive, String title, String description, String parentId, String mimeType, byte[] content) throws IOException {
    File body = new File();
    body.setName(title);
    body.setDescription(description);
    body.setMimeType(mimeType);

    if (parentId != null && parentId.length() > 0) {
      body.setParents(Arrays.asList(parentId));
    }
    
    if (content != null) {
      ByteArrayContent fileContent = new ByteArrayContent(mimeType, content);
      return drive.files().create(body, fileContent)
        .execute();
      
    } else {
      return drive.files().create(body).execute();
    }
  }
    
}
