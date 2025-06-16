package fi.metatavu.edelphi.drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

/**
 * Producer for Google Drive credentials
 * 
 * @author Antti Leppä
 */
public class GoogleCredentialsProducer {

  private static final String[] REQUIRED_SCOPES = new String[] { "https://www.googleapis.com/auth/drive.file" };
  
  @Inject
  private Logger logger;

  /**
   * Produces Google admin credentials
   * 
   * @return credentials admin credentials
   */
  @Dependent
  @AdminDrive
  @Produces
  public GoogleCredential produceAdminCredentials() {
    String keyFile = System.getProperty("edelphi.googleServiceAccount.key");
    if (!isExistingFile(keyFile)) {
      keyFile = System.getenv("GOOGLE_SERVICE_ACCOUNT_KEY");
    }

    if (!isExistingFile(keyFile)) {
      keyFile = "/opt/google-service-account.json";
    }

    if (!isExistingFile(keyFile)) {
      logger.error("Google service account keyfile is not configured");
    }

    try {
      return GoogleCredential.fromStream(new FileInputStream(keyFile)).createScoped(Arrays.asList(REQUIRED_SCOPES));
    } catch (IOException e) {
      logger.error("Error occured while loading google credentials");
    }
    
    return null;
  }

  /**
   * Returns whether file exists or not
   * 
   * @param file file path
   * @return whether file exists or not
   */
  private boolean isExistingFile(String path) {
    if (path == null) {
      return false;
    }
    
    File file = new File(path);
    return file.exists();
  }
  
}
