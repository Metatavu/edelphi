package fi.metatavu.edelphi.pages;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.LocalDocumentDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.resources.Document;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.GoogleDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.metatavu.edelphi.utils.MaterialBean;

public abstract class AbstractDelphiMaterialPageController extends DelfoiPageController {

  private static final String STYLESHEET_ATTRIBUTE = "styleSheet";
  private static final String NAME_ATTRIBUTE = "name";
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String PAGE_COUNT_ATTRIBUTE = "pageCount";
  private static final String PAGE_ATTRIBUTE = "page";
  private static final String CONTENT_ATTRIBUTE = "content";
  private static final String TITLE_ATTRIBUTE = "title";

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @SuppressWarnings ("squid:S3776")
  protected Document resolveDocument(Long resourceId, List<MaterialBean> folderMaterials, Map<Long, List<MaterialBean>> materialTrees) {
    ResourceDAO resourceDAO = new ResourceDAO();
    
    Long currentId = resourceId;
    
    if ((currentId == null) && (!folderMaterials.isEmpty())) {
      currentId = folderMaterials.get(0).getId();
    }
    
    Document document = null;
    
    while (currentId != null) {
      Resource resource = resourceDAO.findById(currentId);
      
      if (resource instanceof Document) {
        // when resource is document we found the document and can break
        return (Document) resource;
      } else {
        if (resource instanceof Folder) {
          // for folders we need to find the first document under it
          List<MaterialBean> list = materialTrees.get(currentId);
          if ((list != null) && (!list.isEmpty())) {
            currentId = list.get(0).getId();
          } else {
            return null;
          }
        } else {
          return null;
        }
      }
    }

    return document;
  }

  protected void appendDocument(PageRequestContext pageRequestContext, Document document, Long documentId, Integer pageNumber) {
    if (document == null) {
      return;
    }
    
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO(); 
    if (document instanceof LocalDocument) {
      Integer page = pageNumber != null ? pageNumber : 0;
      LocalDocument localDocument = (LocalDocument) document;
      LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, page);
      Long pageCount = localDocumentDAO.countByDocument(localDocument);
      pageRequestContext.getRequest().setAttribute(TITLE_ATTRIBUTE, localDocumentPage.getTitle());
      pageRequestContext.getRequest().setAttribute(CONTENT_ATTRIBUTE, localDocumentPage.getContent());
      pageRequestContext.getRequest().setAttribute(PAGE_ATTRIBUTE, page);
      pageRequestContext.getRequest().setAttribute(PAGE_COUNT_ATTRIBUTE, pageCount);
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
    
    pageRequestContext.getRequest().setAttribute(TYPE_ATTRIBUTE, document.getType());
    pageRequestContext.getRequest().setAttribute(NAME_ATTRIBUTE, document.getName());
  }

  private void handleGoogleSpreadsheet(PageRequestContext pageRequestContext, Drive drive, File file) throws IOException {
    DownloadResponse response = GoogleDriveUtils.exportSpreadsheet(drive, file);
    if (response != null) {
      pageRequestContext.getRequest().setAttribute(TITLE_ATTRIBUTE, file.getName());
      pageRequestContext.getRequest().setAttribute(CONTENT_ATTRIBUTE, IOUtils.toString(response.getData(), "UTF-8"));
    } else {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale()); 
    }
  }

  private void handleGoogleDocument(PageRequestContext pageRequestContext, Drive drive, File file) throws IOException {
    DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "text/html");
    String content = GoogleDriveUtils.extractGoogleDocumentContent(response.getData());
    String styleSheet = GoogleDriveUtils.extractGoogleDocumentStyleSheet(response.getData());
    
    if (content != null && styleSheet != null) {
      pageRequestContext.getRequest().setAttribute(TITLE_ATTRIBUTE, file.getName());
      pageRequestContext.getRequest().setAttribute(CONTENT_ATTRIBUTE, content);
      pageRequestContext.getRequest().setAttribute(STYLESHEET_ATTRIBUTE, styleSheet);
    } else {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
  }
  
}