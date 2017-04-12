package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageThesisMultipleScale2D;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ThesisMultipleScale2DQueryReportPage extends AbstractThesisScale2DQueryReportPage {
  
  private static final Logger logger = Logger.getLogger(ThesisMultipleScale2DQueryReportPage.class.getName());
  
  private static final String THESES_OPTION = "multiple2dscales.theses";
  private static final String CHART_INDEX_PARAM = "chartIndex";

  public ThesisMultipleScale2DQueryReportPage() {
    super(QueryPageType.THESIS_MULTIPLE_2D_SCALES);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    List<String> theses = QueryPageUtils.getListSetting(queryPage, THESES_OPTION);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    appendQueryPageTheses(requestContext, queryPage, theses);
    appendQueryPageComments(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_multiple_scale_2d.jsp", null);
  }
  
  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPageThesisMultipleScale2D reportPage = new QueryReportPageThesisMultipleScale2D(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/multiple_scale2d.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    reportPage.setTheses(QueryPageUtils.getListSetting(queryPage, THESES_OPTION));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    return reportPage;
  }

  private void appendQueryPageTheses(RequestContext requestContext, QueryPage queryPage, List<String> theses) {
    @SuppressWarnings("unchecked")
    Map<Long, List<String>> thesesMap = (Map<Long, List<String>>) requestContext.getRequest().getAttribute("multiple2dscalesTheses");
    if (thesesMap == null) {
      thesesMap = new HashMap<>();
      requestContext.getRequest().setAttribute("multiple2dscalesTheses", thesesMap);
    }
    
    thesesMap.put(queryPage.getId(), theses);
  }

  private void appendQueryPageComments(RequestContext requestContext, QueryPage queryPage) {
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);

    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    if (answers == null) {
      answers = new HashMap<>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }

    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }

  @Override
  @SuppressWarnings ("squid:S2629")
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    // Determine whether 2D is rendered as bubble chart or as an X/Y axis bar chart   

    String axis = chartContext.getParameter(RENDER_2D_AXIS_PARAM);
    Integer thesisIndex = chartContext.getInteger(CHART_INDEX_PARAM);
    if (thesisIndex == null) {
      logger.log(Level.SEVERE, String.format("Missing required param %s", CHART_INDEX_PARAM));
      return null;
    }
    
    Render2dAxis render2dAxis = RENDER_2D_AXIS_X_OPTION.equals(axis) ? Render2dAxis.X : RENDER_2D_AXIS_Y_OPTION.equals(axis) ? Render2dAxis.Y : Render2dAxis.BOTH;
    List<String> theses = QueryPageUtils.getListSetting(queryPage, THESES_OPTION);
    String thesis = theses.get(thesisIndex);
    
    if (render2dAxis == Render2dAxis.BOTH) {
      // Render an ordinary 2D bubble chart
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      String labelX = QueryPageUtils.getSetting(queryPage, "multiple2dscales.labelx");
      String labelY = QueryPageUtils.getSetting(queryPage, "multiple2dscales.labely");
      return createBubbleChart(chartContext, queryPage, thesis, labelX, labelY, fieldNameX, fieldNameY);
    } else {
      // Render a bar chart of X or Y axis
      String fieldName = render2dAxis == Render2dAxis.X ? getFieldName(thesisIndex, "x") : getFieldName(thesisIndex, "y");
      String axisLabel = QueryPageUtils.getSetting(queryPage, render2dAxis == Render2dAxis.X ? "multiple2dscales.labelx" : "multiple2dscales.labely");
      return createBarChart(chartContext, queryPage, String.format("%s - %s", thesis, axisLabel), render2dAxis, fieldName);
    }
  }
  
  private String getFieldName(int index, String axis) {
    return String.format("multiple2dscales.%d.%s", index, axis);
  }
  
}
