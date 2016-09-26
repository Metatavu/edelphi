package fi.metatavu.edelphi.binaries.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.resources.ImageDAO;
import fi.metatavu.edelphi.domainmodel.resources.GoogleImage;
import fi.metatavu.edelphi.domainmodel.resources.Image;
import fi.metatavu.edelphi.domainmodel.resources.LinkedImage;
import fi.metatavu.edelphi.domainmodel.resources.LocalImage;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;

public class ViewImageBinaryRequestController extends BinaryController {

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
    } else
      throw new RuntimeException("image not found");
  }

  private void handleLocalImage(BinaryRequestContext binaryRequestContext, LocalImage image) {
    binaryRequestContext.setResponseContent(image.getData(), image.getContentType());
  }

  private void handleLinkedImage(BinaryRequestContext binaryRequestContext, LinkedImage image) {
    try {
      URL imageUrl = new URL(image.getUrl());
      URLConnection uc = imageUrl.openConnection();
      binaryRequestContext.getResponse().setContentType(uc.getContentType());
      InputStream in = uc.getInputStream();
      try {
        ServletOutputStream out = binaryRequestContext.getResponse().getOutputStream();

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
      }
      finally {
        if (in != null) {
          try {
            in.close();
          }
          catch (IOException ioe) {
            Logging.logException(ioe);
          }
        }
      }
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private void handleGoogleImage(BinaryRequestContext binaryRequestContext, GoogleImage googleImage) {
  	try {
    	Drive drive = GoogleDriveUtils.getAdminService();
			File file = GoogleDriveUtils.getFile(drive, googleImage.getResourceId());
			DownloadResponse pngFile = GoogleDriveUtils.exportFile(drive, file, "image/png");
			if (pngFile != null) {
			  
			} else {
			  DownloadResponse response = GoogleDriveUtils.downloadFile(drive, file);
			  if (response == null) {
		      Messages messages = Messages.getInstance();
		      Locale locale = binaryRequestContext.getRequest().getLocale();
			    Logging.logError(String.format("Failed to export google image %d", googleImage.getId()));
		      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, GOOGLE_DOCS_FAILURE));
			  } else {
			    binaryRequestContext.setResponseContent(response.getData(), response.getMimeType());
			  }
			}
		} catch (IOException | GeneralSecurityException e) {
      Messages messages = Messages.getInstance();
      Locale locale = binaryRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, GOOGLE_DOCS_FAILURE), e);
		}
  }
}