  package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class MultiSelectQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO;
  
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    QueryPage queryPage = exportContext.getQueryPage();
    
    boolean commentable = isPageCommentable(queryPage);
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
    
    if (commentable) {
      Locale locale = exportContext.getLocale();
      int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + reportMessages.getText(locale, "reports.spreadsheet.comment")); 
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.setCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
      }
    }
  }
  
  private String getFieldName() {
    return "multiselect";
  }
   
}
