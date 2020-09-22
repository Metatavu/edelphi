package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

/**
 * Spreadsheet exporter for multiple scale 1d query pages 
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Multiple1DScalesQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {
  
  private static final String THESES_OPTION = "multiple1dscales.theses";

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
  
    List<String> theses = queryPageController.getListSetting(queryPage, THESES_OPTION);
    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldName = getFieldName(thesisIndex);      
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      int columnIndex = exportContext.addColumn(getColumnLabel(queryPage.getTitle(), queryField.getCaption()));
      
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
        exportContext.setCellValue(queryReply, columnIndex, answer != null ? answer.getOption().getText() : null);
      }
    }

    exportComments(exportContext);
  }

  /**
   * Returns field name for given index
   * 
   * @param index index
   * @return field name
   */
  private String getFieldName(int index) {
    return String.format("multiple1dscales.%d", index);
  }
  
  /**
   * Returns column label for given thesis and label
   * 
   * @param thesis thesis
   * @param label label
   * @return field label
   */
  private String getColumnLabel(String thesis, String label) {
    return String.format("%s/%s", thesis, label);
  }
  
}
