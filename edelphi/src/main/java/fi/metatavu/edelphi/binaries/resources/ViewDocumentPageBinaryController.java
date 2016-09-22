package fi.metatavu.edelphi.binaries.resources;

import java.io.UnsupportedEncodingException;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.resources.DocumentDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.domainmodel.resources.Document;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;

public class ViewDocumentPageBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    DocumentDAO documentDAO = new DocumentDAO();
    
    Long documentId = binaryRequestContext.getLong("documentId");
    Integer pageNumber = binaryRequestContext.getInteger("pageNumber");

    Document document = documentDAO.findById(documentId);
    if (document instanceof LocalDocument) {
      // TODO: test with instrumented domain model
      handleLocalDocument((LocalDocument) document, pageNumber, binaryRequestContext);
    }
  }
  
  private void handleLocalDocument(LocalDocument localDocument, Integer pageNumber, BinaryRequestContext binaryRequestContext) {
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();
    LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, pageNumber);

    try {
      binaryRequestContext.setResponseContent(localDocumentPage.getContent().getBytes("UTF-8"), "text/html");
    }
    catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
}
