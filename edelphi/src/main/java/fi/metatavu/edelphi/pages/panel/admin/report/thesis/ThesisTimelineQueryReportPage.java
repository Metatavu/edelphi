package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.query.thesis.TimelineThesisQueryPageHandler;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.TimelineReportPageCommentProcessor;

public class ThesisTimelineQueryReportPage extends QueryReportPageController {

  public ThesisTimelineQueryReportPage() {
    super(QueryPageType.THESIS_TIMELINE);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
    QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value1");
    List<Double> data = ReportUtils.getNumberFieldData(queryField, queryReplies);
    Double min = QueryPageUtils.getDoubleSetting(queryPage, "timeline.min");
    Double max = QueryPageUtils.getDoubleSetting(queryPage, "timeline.max");
    Double step = QueryPageUtils.getDoubleSetting(queryPage, "timeline.step");
    Map<Double, String> dataNames = new HashMap<Double, String>();
    for (double d = min; d <= max; d += step) {
      dataNames.put(d, step % 1 == 0 ? new Long(Math.round(d)).toString() : new Double(d).toString());
    }
    QueryFieldDataStatistics statistics = ReportUtils.getStatistics(data, dataNames);
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_timeline.jsp", statistics);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/timeline.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    return reportPage;
  }

  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    // Data access objects
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    // Axis labels
    Double min = QueryPageUtils.getDoubleSetting(queryPage, "timeline.min");
    Double max = QueryPageUtils.getDoubleSetting(queryPage, "timeline.max");
    Double step = QueryPageUtils.getDoubleSetting(queryPage, "timeline.step");
    int type = QueryPageUtils.getIntegerSetting(queryPage, "timeline.type");

    List<String> captions = new ArrayList<String>();
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    for (double d = min; d <= max; d += step) {
      captions.add(step % 1 == 0 ? new Long(Math.round(d)).toString() : new Double(d).toString());
    }
    
    if (type == TimelineThesisQueryPageHandler.TIMELINE_TYPE_2VALUE) {
      Double[][] values = new Double[captions.size()][captions.size()];
      QueryField xField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value1");
      List<Double> xValues = ReportUtils.getNumberFieldData(xField, queryReplies);
      QueryField yField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value2");
      List<Double> yValues = ReportUtils.getNumberFieldData(yField, queryReplies);
      for (int i = 0; i < xValues.size(); i++) {
        if (xValues.get(i) == null || yValues.get(i) == null) {
          continue;
        }
        int x = (int) ((xValues.get(i) - min) / step);
        int y = (int) ((yValues.get(i) - min) / step);
        values[x][y] = new Double(values[x][y] != null ? values[x][y] + 1 : 1);
      }
      return ChartModelProvider.createBubbleChart(queryPage.getTitle(), null, captions, null, captions, 0, 0, values);
    }
    else {
      QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value1");
      List<Double> data = ReportUtils.getNumberFieldData(queryField, queryReplies);
      String chartTitle = QueryPageUtils.getSetting(queryPage, "timeline.value1Label"); 
      Map<Double, String> dataNames = new HashMap<Double, String>();
      List<Double> occurences = new ArrayList<Double>();
      Map<Double, Long> classifiedData = ReportUtils.getClassifiedNumberFieldData(data);
      
      for (double d = min; d <= max; d += step) {
        String caption = step % 1 == 0 ? new Long(Math.round(d)).toString() : new Double(d).toString();
        dataNames.put(d, caption);
        occurences.add(classifiedData.get(d) == null ? 0d : classifiedData.get(d));
      }
      
      
      QueryFieldDataStatistics statistics = ReportUtils.getStatistics(data, dataNames);
      Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
      Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
      Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;
      return ChartModelProvider.createBarChart(chartTitle, null, captions, occurences, avg, q1, q3);
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
    ReportPageCommentProcessor sorter = new TimelineReportPageCommentProcessor(panelStamp, queryPage, answers);
    appendQueryPageComments(requestContext, queryPage, sorter);
  }

}
