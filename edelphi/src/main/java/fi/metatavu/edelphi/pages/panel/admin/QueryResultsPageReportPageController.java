package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;

/**
 * Single query page report as shown in the Query Results of panel administration. 
 */
public class QueryResultsPageReportPageController extends AbstractQueryReportPageController {

  public QueryResultsPageReportPageController() {
    super("/jsp/pages/panel/admin/queryresults_pagereport.jsp");
    setAccessAction(DelfoiActionName.MANAGE_QUERY_RESULTS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.ACCESS_PANEL_QUERY_RESULTS;
  }

  @Override
  protected List<QueryReportPageData> getPageDatas(PageRequestContext pageRequestContext, ReportContext reportContext, Query query) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    Long pageId = pageRequestContext.getLong("pageId");
    QueryPage queryPage = queryPageDAO.findById(pageId);

    pageRequestContext.getRequest().setAttribute("pageId", pageId);
    
    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();

    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    
    // We need to check whether page type has a report controller or not before continuing.
    if (queryReportPageController != null) {
      QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, reportContext, queryPage);
      pageDatas.add(pageData);

      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
      QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
    }
    
    return pageDatas;
  }
  
}
