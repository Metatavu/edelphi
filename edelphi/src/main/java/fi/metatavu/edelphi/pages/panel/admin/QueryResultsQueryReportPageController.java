package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.ReportUtils;

/**
 * Full query report as shown in the Query Results of panel administration. 
 */
public class QueryResultsQueryReportPageController extends AbstractQueryReportPageController {

  public QueryResultsQueryReportPageController() {
    super("/jsp/pages/panel/admin/queryresults_queryreport.jsp");
    setAccessAction(DelfoiActionName.MANAGE_QUERY_RESULTS, DelfoiActionScope.PANEL);
  }

  @Override
  protected List<QueryReportPageData> getPageDatas(PageRequestContext pageRequestContext, ReportContext reportContext, Query query) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();
    
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new Comparator<QuerySection>() {
      @Override
      public int compare(QuerySection o1, QuerySection o2) {
        return o1.getSectionNumber() - o2.getSectionNumber();
      }
    });
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        
        // TODO: Will Sections need something in reports??
        
        List<QueryPage> queryPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(queryPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        for (QueryPage queryPage : queryPages) {
          if (queryPage.getVisible()) {
            QueryPageType queryPageType = queryPage.getPageType();
            QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPageType);
            
            // We need to remove pages w/o report controller from the list.
            if (queryReportPageController != null) {
              QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, reportContext, queryPage);
              pageDatas.add(pageData);

              List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
              QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
            }
            
          }
        }
      }
    }
    return pageDatas;
  }
  
}
