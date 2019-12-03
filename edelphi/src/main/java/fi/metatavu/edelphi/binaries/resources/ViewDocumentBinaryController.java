package fi.metatavu.edelphi.binaries.resources;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.resources.DocumentDAO;
import fi.metatavu.edelphi.domainmodel.resources.Document;
import fi.metatavu.edelphi.domainmodel.resources.GoogleDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;

public class ViewDocumentBinaryController extends BinaryController {

  private static Logger logger = Logger.getLogger(ViewDocumentBinaryController.class.getName());

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    DocumentDAO documentDAO = new DocumentDAO();

    Long documentId = binaryRequestContext.getLong("documentId");

    Document document = documentDAO.findById(documentId);
    if (document instanceof LocalDocument) {
      String baseUrl = RequestUtils.getBaseUrl(binaryRequestContext.getRequest());
      binaryRequestContext.setRedirectURL(baseUrl + "/resources/viewdocumentpage.binary?documentId=" + documentId + "&pageNumber=0");
    } else if (document instanceof GoogleDocument) {
      try {
        handleGoogleDocument((GoogleDocument) document, binaryRequestContext);
      }
      catch (Exception e) {
        Messages messages = Messages.getInstance();
        Locale locale = binaryRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
      }
    }
  }

  private void handleGoogleDocument(GoogleDocument googleDocument, BinaryRequestContext binaryRequestContext) throws IOException, GeneralSecurityException {
  	Drive drive = GoogleDriveUtils.getAdminService();
  	if (drive != null) {
    	try {
    	  byte[] outputData = null;
    		String outputMime = null;
    		String outputFileName = null;
    		String redirectUrl = null;
    		
  			File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
  			String mimeType = file.getMimeType();
  			
        try {
    			if ("application/vnd.google-apps.presentation".equals(mimeType)) {
  			    DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "application/pdf");
            outputData = response.getData();
            outputMime = response.getMimeType();
            outputFileName = ResourceUtils.getUrlName(file.getName()) + ".pdf";
    			} else {
  				  DownloadResponse response = GoogleDriveUtils.downloadFile(drive, file);
  					outputData = response.getData();
  					outputMime = response.getMimeType();
  					outputFileName = ResourceUtils.getUrlName(file.getName());
    			}
        } catch (GoogleJsonResponseException e) {
          logger.info("Google export or download failed, falling back to redirect.");
          
          if (StringUtils.isNotBlank(file.getWebContentLink())) {
            redirectUrl = file.getWebContentLink();
          } else {
            throw e;
          }
        }

  			if (redirectUrl != null) {
          binaryRequestContext.setRedirectURL(redirectUrl);
  			} else if (outputData != null && outputMime != null) {
    			binaryRequestContext.setResponseContent(outputData, outputMime);
    			if (StringUtils.isNotBlank(outputFileName)) {
    				binaryRequestContext.setFileName(outputFileName);
    			}
  			} 
  			
  		} catch (IOException e) {
        Messages messages = Messages.getInstance();
        Locale locale = binaryRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
  		}
  	}
  }
}
