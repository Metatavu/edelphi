  package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class MultiSelectQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO;
  
  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    QueryPage queryPage = exportContext.getQueryPage();

    List<String> options = queryPageController.getListSetting(queryPage, "multiselect.options");

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryReply> queryReplies = exportContext.getQueryReplies();

    int value = 0;
    for (String option : options) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + option);

      for (QueryReply queryReply : queryReplies) {
        QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
        if (answer != null) {
          Set<QueryOptionFieldOption> selectedOptions = answer.getOptions();
  
          for (QueryOptionFieldOption selectedOption : selectedOptions) {
            if (selectedOption.getValue().equals(String.valueOf(value))) {
              exportContext.setCellValue(queryReply, columnIndex, "1");
              break;
            }
          }
        }
      }
      
      value++;
    }
  }
  
  private String getFieldName() {
    return "multiselect";
  }
   
}
