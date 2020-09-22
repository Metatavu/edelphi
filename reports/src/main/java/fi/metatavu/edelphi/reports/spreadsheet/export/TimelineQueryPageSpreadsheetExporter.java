package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.TimelineReportPageCommentProcessor;

@ApplicationScoped
public class TimelineQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  private static final String TIMELINE_VALUE1 = "timeline.value1";
  private static final String TIMELINE_VALUE2 = "timeline.value2";
  
  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;
  
  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    QueryPage queryPage = exportContext.getQueryPage();

    QueryNumericField queryField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
    QueryNumericField queryField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
    
    int columnIndex1 = exportContext.addColumn(queryPage.getTitle() + "/" + queryField1.getCaption());
    int columnIndex2 = -1;
    
    if (queryField2 != null) {
      columnIndex2 = exportContext.addColumn(queryPage.getTitle() + "/" + queryField2.getCaption());
    }
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionNumericAnswer answer1 = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField1);
      if ((answer1 != null) && (answer1.getData() != null)) {
        exportContext.setCellValue(queryReply, columnIndex1, Math.round(answer1.getData()));
      }

      QueryQuestionNumericAnswer answer2 = queryField2 == null ? null : queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField2);
      if ((answer2 != null) && (answer2.getData() != null)) {
        exportContext.setCellValue(queryReply, columnIndex2, Math.round(answer2.getData()));
      }
    }
  }

  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new TimelineReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
}
