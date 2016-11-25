package fi.metatavu.edelphi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.bertoncelj.wildflysingletonservice.Start;
import com.bertoncelj.wildflysingletonservice.Stop;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.resources.GoogleDocumentDAO;
import fi.metatavu.edelphi.dao.resources.GoogleImageDAO;
import fi.metatavu.edelphi.domainmodel.resources.GoogleDocument;
import fi.metatavu.edelphi.domainmodel.resources.GoogleImage;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

@Singleton
public class EdelfoiGoogleDriveScheduler {

  private static final int TIMER_INTERVAL = 1000 * 60 * 6;

  @PersistenceContext
  private EntityManager entityManager;
  
  @Resource
  private TimerService timerService;
  
  private boolean stopped;
  
  @Start
  public void start() {
    stopped = false;
    startTimer(TIMER_INTERVAL);
  }
  
  @Stop
  public void stop() {
    stopped = true;
  }
  
  private void startTimer(int duration) {
    stopped = false;
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerService.createSingleActionTimer(duration, timerConfig);
  }
  
  @Timeout
  public void timeout(Timer timer) {
    if (!stopped) {
      GenericDAO.setEntityManager(entityManager);
      try {
        refreshGoogleDriveFiles();
        startTimer(TIMER_INTERVAL);
      } finally {
        GenericDAO.setEntityManager(null);
      }
    }
  }

  private void refreshGoogleDriveFiles() {
    GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
    
    try {
    	 Drive drive = GoogleDriveUtils.getAdminService();
    	
    	 List<GoogleDocument> googleDocuments = googleDocumentDAO.listByArchivedOrderByLastSynchronizedAsc(Boolean.FALSE, 0, 5);
    	 for (GoogleDocument googleDocument : googleDocuments) {
    		 refreshDocument(drive, googleDocument);
    	 }

    	 GoogleImageDAO googleImageDAO = new GoogleImageDAO();
    	 List<GoogleImage> googleImages = googleImageDAO.listByArchivedOrderByLastSynchronizedAsc(Boolean.FALSE, 0, 5);
    	 for (GoogleImage googleImage : googleImages) {
    		 refreshImage(drive, googleImage);
    	 }
    } catch (GeneralSecurityException | IOException e1) {
    	Logging.logException("Failed to obtain Google drive service", e1);
		}
  }

  private void refreshImage(Drive drive, GoogleImage googleImage) {
    GoogleImageDAO googleImageDAO = new GoogleImageDAO();

    try {
    	 File file = GoogleDriveUtils.getFile(drive, googleImage.getResourceId());
    	 if (file.getTrashed()) {
    		 // User has trashed the file from Google Drive, so archiving it.
    		 googleImageDAO.archive(googleImage);
    	 } else {
    		 if (!googleImage.getName().equals(file.getName())) {
    			 String urlName = ResourceUtils.getUniqueUrlName(file.getName(), googleImage.getParentFolder());
    			 googleImageDAO.updateName(googleImage, file.getName(), urlName);
    		 }
    		 
    		 googleImageDAO.updateLastModified(googleImage, new Date(file.getModifiedTime().getValue()));
    		 googleImageDAO.updateLastSynchronized(googleImage, new Date(System.currentTimeMillis()));
    	 }
     } catch (GoogleJsonResponseException e) {
    	 if (e.getStatusCode() == 404) {
    		 // User has removed the file from Google Drive or permission to it, so archiving it.
    		 googleImageDAO.archive(googleImage);
    	 } else {
    		 Logging.logException("GoogleImage synchronization failed", e);
    	 }
     } catch (Exception e) {
    	 Logging.logException("GoogleImage synchronization failed", e);
     }
  }

  private void refreshDocument(Drive drive, GoogleDocument googleDocument) {
    GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
    
    try {
    	 File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
    	 if (file.getTrashed()) {
    		 // User has trashed the file from Google Drive, so archiving it.
    		 googleDocumentDAO.archive(googleDocument);
    	 } else {
    		 if (!googleDocument.getName().equals(file.getName())) {
    			 String urlName = ResourceUtils.getUniqueUrlName(file.getName(), googleDocument.getParentFolder());
    			 googleDocumentDAO.updateName(googleDocument, file.getName(), urlName);
    		 }
    		 
    		 googleDocumentDAO.updateLastModified(googleDocument, new Date(file.getModifiedTime().getValue()));
    		 googleDocumentDAO.updateLastSynchronized(googleDocument, new Date(System.currentTimeMillis()));
    	 }
     } catch (GoogleJsonResponseException e) {
    	 if (e.getStatusCode() == 404) {
    		 // User has removed the file from Google Drive or permission to it, so archiving it.
    		 googleDocumentDAO.archive(googleDocument);
    	 } else {
    		 Logging.logException("GoogleDocument synchronization failed", e);
    	 }
     } catch (Exception e) {
    	 Logging.logException("GoogleDocument synchronization failed", e);
     }
  }
  
}
