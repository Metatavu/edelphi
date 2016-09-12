package fi.metatavu.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class QueryResultsPageController extends PanelPageController {

  public QueryResultsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_QUERY_RESULTS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    List<PanelStamp> stamps = panelStampDAO.listByPanel(panel);
    Collections.sort(stamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? 1 : o2.getStampTime() == null ? -1 : o1.getStampTime().compareTo(o2.getStampTime());
      }
    });
    PanelStamp latestStamp = panel.getCurrentStamp();
    PanelStamp activeStamp = RequestUtils.getActiveStamp(pageRequestContext);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    
    Map<Long, List<QueryPage>> queryPages = new HashMap<Long, List<QueryPage>>();
    Map<Long, Long> queryReplyCounts = new HashMap<Long, Long>();
    for (Query query : queries) {
      queryPages.put(query.getId(), queryPageDAO.listByQuery(query));
      queryReplyCounts.put(query.getId(), queryReplyDAO.countByQueryAndStamp(query, activeStamp));      
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queries", queries);
    pageRequestContext.getRequest().setAttribute("queryPages", queryPages);
    pageRequestContext.getRequest().setAttribute("queryReplyCounts", queryReplyCounts);
    pageRequestContext.getRequest().setAttribute("stamps", stamps);
    pageRequestContext.getRequest().setAttribute("latestStamp", latestStamp);
    pageRequestContext.getRequest().setAttribute("activeStamp", activeStamp);

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/queryresults.jsp");
  }
}