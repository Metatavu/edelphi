package fi.metatavu.edelphi.pages.panel.admin.report.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;

public abstract class QueryReportPageController {

  private final QueryPageType queryPageType;

  public QueryReportPageController(QueryPageType queryPageType) {
    this.queryPageType = queryPageType;
  }

  @Deprecated
  public abstract QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage);
  
  public abstract QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage);
  
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    return null;
  }
  
  public QueryPageType getQueryPageType() {
    return queryPageType;
  }

  /**
   * Appends query page comment to request.
   * 
   * @param requestContext request contract
   * @param queryPage query page
   * @param processor comment processor 
   */
  protected void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage, ReportPageCommentProcessor processor) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    processor.processComments();
    List<QueryQuestionComment> rootComments = processor.getRootComments();
    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }

  /**
   * Returns comment answers map from the request context. If map is not yet defined, new is created. 
   * 
   * @param requestContext request contract
   * @return answer map
   */
  protected Map<Long, Map<String, String>> getRequestAnswerMap(RequestContext requestContext) {
    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    if (answers == null) {
      answers = new HashMap<>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    
    return answers;
  }
  
}
