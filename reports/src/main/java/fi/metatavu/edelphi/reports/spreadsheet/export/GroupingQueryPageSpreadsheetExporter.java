package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class GroupingQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;
  
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;
  
  @Inject
  private QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO;
  
  @Inject
  private QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    QueryPage queryPage = exportContext.getQueryPage();
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryOptionFieldOptionGroup> fieldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(queryField);
    
    boolean commentable = queryPageController.getBooleanSetting(queryPage, "thesis.commentable");
    Locale locale = exportContext.getLocale();
    
    for (QueryOptionFieldOptionGroup fieldGroup : fieldGroups) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + fieldGroup.getName());

      for (QueryReply queryReply : queryReplies) {
        List<QueryQuestionOptionGroupOptionAnswer> answers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, queryField, fieldGroup);
        if (answers.size() > 0) {
          List<String> cellValues = new ArrayList<String>();
          
          for (QueryQuestionOptionGroupOptionAnswer answer : answers) {
            cellValues.add(answer.getOption().getText());
          }
          
          StringBuilder cellValueBuilder = new StringBuilder();
          for (int i = 0, l = cellValues.size(); i < l; i++) {
            cellValueBuilder.append(cellValues.get(i));
            if (i < (l - 1))
              cellValueBuilder.append(',');
          }
  
          exportContext.setCellValue(queryReply, columnIndex, cellValueBuilder.toString());
        }
      }
    }    

    if (commentable) {
      int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + reportMessages.getText(locale, "reports.spreadsheet.comment"));
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.setCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
      }
    }
  }

  private String getFieldName() {
    return "grouping";
  }
  
}
