package fi.metatavu.edelphi.pages.panel.admin.report.util;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;

public abstract class QueryReportPageController {

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
  
  private final QueryPageType queryPageType;
}
