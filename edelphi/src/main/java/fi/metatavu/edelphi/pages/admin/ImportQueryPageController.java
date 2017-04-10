package fi.metatavu.edelphi.pages.admin;

import org.apache.commons.fileupload.FileItem;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.tools.DataImporter;

public class ImportQueryPageController extends PageController {

  public ImportQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String method = pageRequestContext.getRequest().getMethod();
    if ("POST".equals(method)) {

      try {
      
        pageRequestContext.getResponse().setContentType("text/plain");

        Long queryId = pageRequestContext.getLong("queryId");
        FileItem emailMappingFile = pageRequestContext.getFile("emailMappingFile");
        FileItem fieldMappingFile = pageRequestContext.getFile("fieldMappingFile");
        FileItem commentMappingFile = pageRequestContext.getFile("commentMappingFile");
        FileItem queryDataFile = pageRequestContext.getFile("queryDataFile");
        FileItem matrixMappingFile = pageRequestContext.getFile("matrixMappingFile");
        String datePattern = pageRequestContext.getString("datePattern");
        String createdParameter = pageRequestContext.getString("createdParameter");
        String modifiedParameter = pageRequestContext.getString("modifiedParameter");

        DataImporter dataImporter = new DataImporter();
        dataImporter.setQueryId(queryId);
        dataImporter.setQueryDataFile(queryDataFile);
        dataImporter.setDelimiter(';');
        dataImporter.setDatePattern(datePattern);
        dataImporter.setCreatedParameter(createdParameter);
        dataImporter.setModifiedParameter(modifiedParameter);
        dataImporter.setEmailMappingFile(emailMappingFile);
        dataImporter.setFieldMappingFile(fieldMappingFile);
        dataImporter.setCommentMappingFile(commentMappingFile);
        dataImporter.setMatrixMappingFile(matrixMappingFile);

        dataImporter.doImport();

      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      pageRequestContext.setIncludeJSP("/jsp/pages/admin/importquery.jsp");
    }
  }

}
