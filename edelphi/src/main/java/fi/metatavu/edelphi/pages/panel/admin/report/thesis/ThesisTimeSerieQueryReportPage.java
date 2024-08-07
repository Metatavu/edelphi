package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartDataSeries;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.MathUtils;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.utils.comments.TimeSerieReportPageCommentProcessor;

public class ThesisTimeSerieQueryReportPage extends QueryReportPageController {

  public ThesisTimeSerieQueryReportPage() {
    super(QueryPageType.THESIS_TIME_SERIE);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    // TODO: Any statistics for web page?

    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/time_serie.jsp", null);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/timeseries.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    return reportPage;
  }
  
  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    
    Locale locale = LocaleUtils.toLocale(chartContext.getReportContext().getLocale());
    
    Double minX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.minX");
    Double maxX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxX");
    Double minY = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.minY");
    Double maxY = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxY");
    Double stepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.stepX");
    Double userStepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.userStepX");
    if (userStepX == null) {
      userStepX = stepX;
    }
    double stepGCD = MathUtils.getGCD(stepX, userStepX);
    
    int valueCount = new Double(Math.ceil((maxX - minX) / stepGCD)).intValue() + 1;
    
    List<String> categoryCaptions = new ArrayList<String>(valueCount);
    List<Double> preliminaryValues = new ArrayList<Double>(valueCount);
    List<Double> averageValues = new ArrayList<Double>(valueCount);
    List<Double> q1Values = new ArrayList<Double>(valueCount);
    List<Double> q3Values = new ArrayList<Double>(valueCount);
    List<Double> minValues = new ArrayList<Double>(valueCount);
    List<Double> maxValues = new ArrayList<Double>(valueCount);
    
    for (int i = 0; i < valueCount; i++) {
      categoryCaptions.add(null);
      preliminaryValues.add(null);
      averageValues.add(null);
      q1Values.add(null);
      q3Values.add(null);
      minValues.add(null);
      maxValues.add(null);
    }
    
    NavigableMap<String, String> predefinedValuesStringMap = QueryPageUtils.getMapSetting(queryPage, "time_serie.predefinedValues");
    int predefinedCount = 0;
    Double lastPredefinedValue = null;
    Double lastPredefinedX = null;
    NavigableMap<Double, Double> predefinedValuesMap = new TreeMap<Double, Double>();
    
    Iterator<String> stringMapIterator = predefinedValuesStringMap.keySet().iterator();
    while (stringMapIterator.hasNext()) {
      String xStr = stringMapIterator.next();
      String yStr = predefinedValuesStringMap.get(xStr);
      Double y = StringUtils.isNotBlank(yStr) ? NumberUtils.createDouble(yStr.replaceAll(",", ".")) : null;
      predefinedValuesMap.put(NumberUtils.createDouble(xStr), y);
    }
    
    for (double x = minX; x <= maxX; x += stepGCD) {
      int index = (int) Math.round((x - minX) / stepGCD);
      Double y = predefinedValuesMap.get(x);
      
      if (y != null) {
        preliminaryValues.set(index, y);
        lastPredefinedValue = y;
        lastPredefinedX = x;
        predefinedCount++;
      }
      
      categoryCaptions.set(index, Math.floor(x) == x ? String.valueOf((int) x) : String.valueOf(x));
    }

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    
    if (predefinedCount > 0) {
      // last predefined value is set as first value in all series connecting the predefined line with the actual series
      int lastPredefinedIndex = (int) Math.round((lastPredefinedX - minX) / stepGCD); 
      averageValues.set(lastPredefinedIndex, lastPredefinedValue);
      q1Values.set(lastPredefinedIndex, lastPredefinedValue);
      q3Values.set(lastPredefinedIndex, lastPredefinedValue);
      minValues.set(lastPredefinedIndex, lastPredefinedValue);
      maxValues.set(lastPredefinedIndex, lastPredefinedValue);
    }

    for (Double x = Math.max(minX, lastPredefinedX != null ? lastPredefinedX + userStepX : 0); x <= maxX; x += userStepX) {
      String fieldName = getFieldName(x);
      QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName); 
      List<Double> numberFieldData = ReportUtils.getNumberFieldData(queryField, queryReplies);
      int index = (int) Math.round((x - minX) / stepGCD);

      QueryFieldDataStatistics statistics = new QueryFieldDataStatistics(numberFieldData);

      averageValues.set(index, statistics.getAvg());
      q1Values.set(index, statistics.getQ1());
      q3Values.set(index, statistics.getQ3());
      minValues.set(index, statistics.getMin());
      maxValues.set(index, statistics.getMax());
    }

    String predefinedValuesCaption = QueryPageUtils.getSetting(queryPage, "time_serie.predefinedSetLabel");

    String thesis = QueryPageUtils.getSetting(queryPage, "thesis.text");
    String pageTitle = queryPage.getTitle();
    String chartTitle = StringUtils.isNotBlank(thesis) ? thesis : pageTitle;

    return ChartModelProvider.createTimeSeriesChart(
        chartTitle,
        categoryCaptions, 
        minY, maxY,
        preliminaryValues.size() > 0 ? new ChartDataSeries(predefinedValuesCaption, preliminaryValues) : null,
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.averageValuesValuesCaption"), averageValues), 
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.1stQuartileValuesValuesCaption"), q1Values),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.3rdQuartileValuesValuesCaption"), q3Values),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.minValuesValuesCaption"), minValues),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.maxValuesValuesCaption"), maxValues));
  }
  
  private String getFieldName(Double x) {
    return  "time_serie." + x;
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
    Double maxX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxX");
    String axisXTitle = QueryPageUtils.getSetting(queryPage, "time_serie.xAxisTitle");
    
    ReportPageCommentProcessor sorter = new TimeSerieReportPageCommentProcessor(panelStamp, queryPage, answers, maxX, axisXTitle);
    appendQueryPageComments(requestContext, queryPage, sorter);
  }
}
