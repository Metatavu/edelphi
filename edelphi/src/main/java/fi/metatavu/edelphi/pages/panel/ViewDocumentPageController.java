package fi.metatavu.edelphi.pages.panel;

import java.io.IOException;
import java.util.Locale;
import org.apache.commons.io.IOUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.DocumentDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Document;
import fi.metatavu.edelphi.domainmodel.resources.GoogleDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ViewDocumentPageController extends PanelPageController {

	public ViewDocumentPageController() {
		super();
		setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
	}
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

	@Override
	public void processPageRequest(PageRequestContext pageRequestContext) {
		// TODO: If query is hidden only users with manage material rights should be able to enter
		DocumentDAO documentDAO = new DocumentDAO();
		LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
		LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

		Panel panel = RequestUtils.getPanel(pageRequestContext);
		if (panel == null) {
			throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
		}

		Long documentId = pageRequestContext.getLong("documentId");
		Document document = documentDAO.findById(documentId);
		if (document == null) {
			throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
		}
		
		Integer page = pageRequestContext.getInteger("page");
		if (page == null) {
			page = 0;
		}

		if (document instanceof LocalDocument) {
			LocalDocument localDocument = localDocumentDAO.findById(documentId);
			LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, page);
			Long pageCount = localDocumentDAO.countByDocument(localDocument);
			pageRequestContext.getRequest().setAttribute("title", localDocumentPage.getTitle());
			pageRequestContext.getRequest().setAttribute("content", localDocumentPage.getContent());
			pageRequestContext.getRequest().setAttribute("page", page);
			pageRequestContext.getRequest().setAttribute("pageCount", pageCount);
		} else if (document instanceof GoogleDocument) {
			try {
				GoogleDocument googleDocument = (GoogleDocument) document;

				Drive drive = GoogleDriveUtils.getAdminService();
				File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
				if ("application/vnd.google-apps.document".equals(file.getMimeType())) {
			    handleGoogleDocument(pageRequestContext, drive, file);
				} else if ("application/vnd.google-apps.spreadsheet".equals(file.getMimeType())) {
			    handleGoogleSpreadsheet(pageRequestContext, drive, file);
				} else {
					pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath() + "/resources/viewdocument.binary?documentId=" + documentId);
				}
			} catch (Exception e) {
				Messages messages = Messages.getInstance();
				Locale locale = pageRequestContext.getRequest().getLocale();
				throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
			}
		}

		pageRequestContext.getRequest().setAttribute("panel", panel);
		pageRequestContext.getRequest().setAttribute("type", document.getType());
		pageRequestContext.getRequest().setAttribute("name", document.getName());
		pageRequestContext.getRequest().setAttribute("fullPath", document.getFullPath());

		pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewdocument.jsp");
	}

  private void handleGoogleSpreadsheet(PageRequestContext pageRequestContext, Drive drive, File file) throws IOException {
    DownloadResponse response = GoogleDriveUtils.exportSpreadsheet(drive, file);
    if (response != null) {
      pageRequestContext.getRequest().setAttribute("content", IOUtils.toString(response.getData(), "UTF-8"));
    } else {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale()); 
    }
  }

  private void handleGoogleDocument(PageRequestContext pageRequestContext, Drive drive, File file) throws IOException {
    DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "text/html");
    String content = GoogleDriveUtils.extractGoogleDocumentContent(response.getData());
    String styleSheet = GoogleDriveUtils.extractGoogleDocumentStyleSheet(response.getData());
    
    if (content != null && styleSheet != null) {
    	pageRequestContext.getRequest().setAttribute("content", content);
    	pageRequestContext.getRequest().setAttribute("styleSheet", styleSheet);
    } else {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
  }

}
