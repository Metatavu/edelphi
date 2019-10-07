package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.Scale2DReportPageCommentProcessor;

@ApplicationScoped
public class Scale2DQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  private static final String FIELD_Y = "y";
  private static final String FIELD_X = "x";
  
  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;


  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    boolean commentable = isPageCommentable(queryPage);
    
    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);
    
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    Locale locale = exportContext.getLocale();

    int columnIndexX = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldX.getCaption());
    int columnIndexY = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldY.getCaption());
    int commentColumnIndex = commentable ? exportContext.addColumn(queryPage.getTitle() + "/" + reportMessages.getText(locale, "reports.spreadsheet.comment")) : -1; 
    
    for (QueryReply queryReply : queryReplies) {
      String answerX = queryQuestionOptionAnswerDAO.findTextByQueryReplyAndQueryField(queryReply, queryFieldX);
      String answerY = queryQuestionOptionAnswerDAO.findTextByQueryReplyAndQueryField(queryReply, queryFieldY);
      
      exportContext.setCellValue(queryReply, columnIndexX, answerX);
      exportContext.setCellValue(queryReply, columnIndexY, answerY);

      if (commentable) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        exportContext.setCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
      }
    }
  }

  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new Scale2DReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }
  
}
