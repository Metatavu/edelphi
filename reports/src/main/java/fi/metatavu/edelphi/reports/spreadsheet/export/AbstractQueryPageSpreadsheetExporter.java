package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.spreadsheet.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;

public abstract class AbstractQueryPageSpreadsheetExporter implements QueryPageSpreadsheetExporter {
  
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new GenericReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
  /**
   * Lists page's root comments
   * 
   * @return page's root comments
   */
  protected List<QueryQuestionComment> listRootComments(PanelStamp panelStamp, QueryPage queryPage) {
    return queryQuestionCommentDAO.listRootCommentsByQueryPageAndStampOrderByCreated(queryPage, panelStamp); 
  }
  
}
