package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageComment;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.Scale2DReportPageCommentProcessor;

public class ThesisScale2DQueryReportPage extends AbstractThesisScale2DQueryReportPage {

  public ThesisScale2DQueryReportPage() {
    super(QueryPageType.THESIS_SCALE_2D);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_scale_2d.jsp", null);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/scale2d.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    
    // Add answers to comments and sort by them
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("x"));
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("y"));
    final Map<Long,String> answerMap = new HashMap<>();

    List<QueryReportPageComment> comments = reportPage.getComments();
    for (QueryReportPageComment comment : comments) {
      QueryReply queryReply = queryReplyDAO.findById(comment.getReplyId());
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX); 
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);
      answerMap.put(comment.getReplyId(), (xAnswer == null ? "-" : xAnswer.getOption().getValue()) + (yAnswer == null ? "-" : yAnswer.getOption().getValue()));
      if (xAnswer != null || yAnswer != null) {
        if (xAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(xAnswer.getOption().getOptionField().getCaption()));
          comment.setAnswer(caption, xAnswer.getOption().getText());
        }
        if (yAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(yAnswer.getOption().getOptionField().getCaption()));
          comment.setAnswer(caption, yAnswer.getOption().getText());
        }
      }
    }
    
    Collections.sort(comments, new ReportCommentComparator(answerMap));
    
    return reportPage;
  }

  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    // Determine whether 2D is rendered as bubble chart or as an X/Y axis bar chart   

    String axis = chartContext.getParameter(RENDER_2D_AXIS_PARAM);
    Render2dAxis render2dAxis = RENDER_2D_AXIS_X_OPTION.equals(axis) ? Render2dAxis.X : RENDER_2D_AXIS_Y_OPTION.equals(axis) ? Render2dAxis.Y : Render2dAxis.BOTH;
    String thesis = QueryPageUtils.getSetting(queryPage, "thesis.text");
    String pageTitle = queryPage.getTitle();
    String chartTitle = StringUtils.isNotBlank(thesis) ? thesis : pageTitle;

    if (render2dAxis == Render2dAxis.BOTH) {
      // Render an ordinary 2D bubble chart
      String fieldNameX = getFieldName("x");
      String fieldNameY = getFieldName("y");
      String labelX = QueryPageUtils.getSetting(queryPage, "scale2d.label.x");
      String labelY = QueryPageUtils.getSetting(queryPage, "scale2d.label.y");
      return createBubbleChart(chartContext, queryPage, chartTitle, labelX, labelY, fieldNameX, fieldNameY);
    } else {
      // Render a bar chart of X or Y axis
      String fieldName = render2dAxis == Render2dAxis.X ? getFieldName("x") : getFieldName("y");
      return createBarChart(chartContext, queryPage, chartTitle, render2dAxis, fieldName);
    }
  }

  /**
   * Appends query page comment to request.
   * 
   * @param requestContext request contract
   * @param queryPage query page
   */
  private void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage) {
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    Map<Long, Map<String, String>> answers = getRequestAnswerMap(requestContext);
    ReportPageCommentProcessor sorter = new Scale2DReportPageCommentProcessor(panelStamp, queryPage, answers);
    appendQueryPageComments(requestContext, queryPage, sorter);
  }

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }
 
}
