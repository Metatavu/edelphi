package fi.metatavu.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;

public class ViewQueryReportPageController extends PanelPageController {
  
  private static Logger logger = Logger.getLogger(ViewQueryReportPageController.class.getName());

  public ViewQueryReportPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.ACCESS_PANEL_QUERY_RESULTS;
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    // Data access objects
    
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    // Report context

    ReportContext reportContext = null;
    String serializedContext = pageRequestContext.getString("serializedReportContext");
    try {
      ObjectMapper om = new ObjectMapper();
      byte[] serializedData = Base64.decodeBase64(serializedContext);
      String stringifiedData = new String(serializedData, "UTF-8");
      reportContext = om.readValue(stringifiedData, ReportContext.class); 
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to unserialize reportContenxt", e);
    }
    
    // Query and panel
    
    Long queryId = pageRequestContext.getLong("queryId");
    Query query = queryDAO.findById(queryId);
    
    // Generate the report page(s) 

    List<QueryReportPage> queryReportPages = new ArrayList<>();
    Long queryPageId = pageRequestContext.getLong("queryPageId");
    if (queryPageId == null || queryPageId == 0) {
      queryReportPages.addAll(getReportPages(pageRequestContext, reportContext, query));
    } else {
      QueryPage queryPage = queryPageDAO.findById(queryPageId);
      QueryReportPage queryReportPage = getReportPage(pageRequestContext, reportContext, queryPage);
      if (queryReportPage != null) {
        queryReportPages.add(queryReportPage);
      }
    }
    
    // Render the report 

    pageRequestContext.getRequest().setAttribute("reportContext", reportContext);
    pageRequestContext.getRequest().setAttribute("queryReportPages", queryReportPages);
    pageRequestContext.setIncludeJSP("/jsp/blocks/panel/admin/report/queryreport.jsp");
  }

  private List<QueryReportPage> getReportPages(PageRequestContext pageRequestContext, ReportContext reportContext, Query query) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    List<QueryReportPage> reportPages = new ArrayList<>();
    
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new QuerySectionComparator());
    
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        List<QueryPage> queryPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(queryPages, new QueryPageComparator());
        
        for (QueryPage queryPage : queryPages) {
          if (queryPage.getVisible()) {
            QueryReportPage reportPage = getReportPage(pageRequestContext, reportContext, queryPage);
            if (reportPage != null) {
              reportPages.add(reportPage);
            }
          }
        }
      }
    }
    
    return reportPages;
  }
  
  public QueryReportPage getReportPage(PageRequestContext pageRequestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryPageType queryPageType = queryPage.getPageType();
    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPageType);
    
    if (queryReportPageController != null) {
      return queryReportPageController.generateReportPage(pageRequestContext, reportContext, queryPage);
    }
    
    return null;
  }

  private final class QuerySectionComparator implements Comparator<QuerySection> {
    @Override
    public int compare(QuerySection o1, QuerySection o2) {
      return o1.getSectionNumber() - o2.getSectionNumber();
    }
  }

  private final class QueryPageComparator implements Comparator<QueryPage> {
    @Override
    public int compare(QueryPage o1, QueryPage o2) {
      return o1.getPageNumber() - o2.getPageNumber();
    }
  }

}
