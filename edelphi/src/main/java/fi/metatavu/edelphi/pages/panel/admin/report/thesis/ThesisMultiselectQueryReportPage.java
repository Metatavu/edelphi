package fi.metatavu.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;

public class ThesisMultiselectQueryReportPage extends QueryReportPageController {

  public ThesisMultiselectQueryReportPage() {
    super(QueryPageType.THESIS_MULTI_SELECT);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_multiselect.jsp", null);
  }

  private QueryOptionField getOptionFieldFromScale1DPage(QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO(); 
  
    List<QueryField> pageFields = queryFieldDAO.listByQueryPage(queryPage);
    
    if (pageFields.size() == 1) 
      return (QueryOptionField) pageFields.get(0);
    else
      throw new RuntimeException("");
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/multiselect.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "thesis.description"));
    reportPage.setThesis(QueryPageUtils.getSetting(queryPage, "thesis.text"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    return reportPage;
  }
  
  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();

    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());

    Map<Long, Long> listOptionAnswerCounts = ReportUtils.getMultiselectData(queryOptionField, queryFieldOptions, queryReplies);
    
    List<String> categoryCaptions = new ArrayList<String>();
    List<Double> values = new ArrayList<Double>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      
      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(listOptionAnswerCounts.get(optionId)));
    }
    
    return ChartModelProvider.createBarChartHorizontal(queryPage.getTitle(), null, categoryCaptions, values, null, null, null);
  }
}
