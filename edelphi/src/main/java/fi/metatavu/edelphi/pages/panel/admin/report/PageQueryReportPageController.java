package fi.metatavu.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportChartFormat;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

/**
 * Single query page report for export purposes. 
 */
public class PageQueryReportPageController extends PanelPageController {

  private static Logger logger = Logger.getLogger(PageQueryReportPageController.class.getName());
  
  public PageQueryReportPageController() {
  }

  @Override
  public Feature getFeature() {
    return Feature.ACCESS_PANEL_QUERY_RESULTS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    logger.info("Exporting query report page");

    QueryPageDAO queryPageDAO = new QueryPageDAO();
    Locale locale = pageRequestContext.getRequest().getLocale();

    String internalAuthorization = AuthUtils.getInternalAuthorization(pageRequestContext.getRequest());
    if (StringUtils.isBlank(internalAuthorization)) {
      logger.warning("Failed to export report page: Unauthorized");
      throw new AccessDeniedException(locale);
    }

    if (!internalAuthorization.equals(SystemUtils.getSettingValue("system.internalAuthorizationHash"))) {
      logger.warning("Failed to export report page: Forbidden");
      throw new AccessDeniedException(locale);
    }
    
    Long pageId = pageRequestContext.getLong("pageId");
    ReportChartFormat chartFormat = ReportChartFormat.valueOf(pageRequestContext.getString("chartFormat"));

    logger.info(String.format("Export query report page in using following settings: pageId: %d, locale %s, chartFormat: %s", pageId, locale, chartFormat));

    ReportContext reportContext = null;
    String serializedContext = pageRequestContext.getString("serializedContext");
    try {
      ObjectMapper om = new ObjectMapper();
      byte[] serializedData = Base64.decodeBase64(serializedContext);
      String stringifiedData = new String(serializedData, "UTF-8");
      reportContext = om.readValue(stringifiedData, ReportContext.class); 
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to deserialize serialized context", e);
    }
    
    QueryPage queryPage = queryPageDAO.findById(pageId);
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();

    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    if (queryReportPageController == null) {
      return;
    }

    QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, reportContext, queryPage);
    pageDatas.add(pageData);
    
    // Query reply ids are needed for proper filtering of comments
    
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
    QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("pageId", pageId);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("chartFormat", chartFormat);
    pageRequestContext.getRequest().setAttribute("reportPageDatas", pageDatas);
    pageRequestContext.getRequest().setAttribute("reportContext", reportContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/report/showreport.jsp");
  }
  
}
