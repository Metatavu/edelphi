  package fi.metatavu.edelphi.pages.panel.admin.report.text;

import fi.metatavu.edelphi.smvc.controllers.RequestContext;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;

public class TextQueryReportPage extends QueryReportPageController {

  public TextQueryReportPage() {
    super(QueryPageType.TEXT);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    String text = QueryPageUtils.getSetting(queryPage, "text.content");
    
    QueryUtils.appendQueryPageComments(requestContext, queryPage);

    return new TextQueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/text.jsp", text);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/text.jsp");
    reportPage.setDescription(QueryPageUtils.getSetting(queryPage, "text.content"));
    ReportUtils.appendComments(reportPage, queryPage, reportContext);
    return reportPage;
  }
 
  public class TextQueryReportPageData extends QueryReportPageData {
    
    public TextQueryReportPageData(QueryPage queryPage, String jspFile, String text) {
      super(queryPage, jspFile, null);
      
      this.text = text;
    }
    
    public String getText() {
      return text;
    }
    
    private String text;
  }
}
