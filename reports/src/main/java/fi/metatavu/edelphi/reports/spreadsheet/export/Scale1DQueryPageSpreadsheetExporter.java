package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.Scale1DReportPageCommentProcessor;

@ApplicationScoped
public class Scale1DQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());

    int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption());
    
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
      exportContext.setCellValue(queryReply, columnIndex, answer != null ? answer.getOption().getText() : null);
    }

    exportComments(exportContext);
  }
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new Scale1DReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
  private String getFieldName() {
    return "scale1d";
  }
  
}
