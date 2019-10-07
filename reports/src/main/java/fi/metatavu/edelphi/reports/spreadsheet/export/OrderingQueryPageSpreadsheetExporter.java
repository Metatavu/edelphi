package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;

@ApplicationScoped
public class OrderingQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;
  
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    boolean commentable = isPageCommentable(queryPage);

    Locale locale = exportContext.getLocale();
    
    int columnIndex = exportContext.addColumn(queryPage.getTitle());
    int commentColumnIndex = commentable ? exportContext.addColumn(queryPage.getTitle() + "/" + reportMessages.getText(locale, "reports.spreadsheet.comment")) : -1; 
    
    for (QueryReply queryReply : queryReplies) {
      List<QueryQuestionNumericAnswer> answers = queryQuestionNumericAnswerDAO.listByQueryReplyAndQueryPageOrderByData(queryReply, queryPage);
      StringBuilder cellValueBuilder = new StringBuilder();
      for (int i = 0, l = answers.size(); i < l; i++) {
        QueryNumericField queryNumericField =  (QueryNumericField) answers.get(i).getQueryField();
        
        cellValueBuilder.append(queryNumericField.getCaption());
        if (i < (l - 1))
          cellValueBuilder.append(',');
      }

      exportContext.setCellValue(queryReply, columnIndex, cellValueBuilder.toString());

      if (commentable) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.setCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
      }
    }
  }
  
}
