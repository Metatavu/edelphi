package fi.metatavu.edelphi.binaries.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.resources.ImageDAO;
import fi.metatavu.edelphi.domainmodel.resources.GoogleImage;
import fi.metatavu.edelphi.domainmodel.resources.Image;
import fi.metatavu.edelphi.domainmodel.resources.LinkedImage;
import fi.metatavu.edelphi.domainmodel.resources.LocalImage;
import fi.metatavu.edelphi.drive.DriveImageCache;
import fi.metatavu.edelphi.drive.DriveImageCache.ImageEntry;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;

public class ViewImageBinaryRequestController extends BinaryController {

  private static Logger logger = Logger.getLogger(ViewImageBinaryRequestController.class.getName());
  private static final String GOOGLE_DOCS_FAILURE = "exception.1012.googleDocsFailure";

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    ImageDAO imageDAO = new ImageDAO();

    Long imageId = binaryRequestContext.getLong("imageId");

    Image image = imageDAO.findById(imageId);

    if (image != null) {
      if (image instanceof LinkedImage) {
        handleLinkedImage(binaryRequestContext, (LinkedImage) image);
      } else if (image instanceof LocalImage) {
        handleLocalImage(binaryRequestContext, (LocalImage) image);
      } else if (image instanceof GoogleImage) {
        handleGoogleImage(binaryRequestContext, (GoogleImage) image);
      }
    } else {
      throw new PageNotFoundException(binaryRequestContext.getRequest().getLocale());
    }
  }

  private void handleLocalImage(BinaryRequestContext binaryRequestContext, LocalImage image) {
    binaryRequestContext.setResponseContent(image.getData(), image.getContentType());
  }

  private void handleLinkedImage(BinaryRequestContext binaryRequestContext, LinkedImage image) {
    try {
      URL imageUrl = new URL(image.getUrl());
      URLConnection uc = imageUrl.openConnection();
      binaryRequestContext.getResponse().setContentType(uc.getContentType());
      
      try (InputStream in = uc.getInputStream()) {
        ServletOutputStream out = binaryRequestContext.getResponse().getOutputStream();

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
      }
      
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "Failed to process linked image", ex);
      throw new PageNotFoundException(binaryRequestContext.getRequest().getLocale());
    }
  }

  private void handleGoogleImage(BinaryRequestContext binaryRequestContext, GoogleImage googleImage) {
    ImageEntry cachedEntry = DriveImageCache.get(googleImage.getResourceId());
    if (cachedEntry != null) {
      binaryRequestContext.setResponseContent(cachedEntry.getData(), cachedEntry.getContentType());
    } else {
      Drive drive = GoogleDriveUtils.getAdminService();
      
      File file;
      try {
        file = GoogleDriveUtils.getFile(drive, googleImage.getResourceId());
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to download file for Google Drive", e);
        throw new PageNotFoundException(binaryRequestContext.getRequest().getLocale());
      }
        
      try {
        DownloadResponse response = GoogleDriveUtils.downloadFile(drive, file);
        if (response == null) {
          Messages messages = Messages.getInstance();
          Locale locale = binaryRequestContext.getRequest().getLocale();
          Logging.logError(String.format("Failed to export google image %d", googleImage.getId()));
          throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, GOOGLE_DOCS_FAILURE));
        } else {
          binaryRequestContext.setResponseContent(response.getData(), response.getMimeType());
        }

      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to download image", e);
      }
    }
  }
  
}
