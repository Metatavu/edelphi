package fi.metatavu.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CompareReportsPageController extends PanelPageController {

  public CompareReportsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {

    // Data access objects

    QueryDAO queryDAO = new QueryDAO();
    
    // Page attribute: panel (for displaying panel name)

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    // Page attribute: queries (for listing all queries in dropdown menus)
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    // Main JSP
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/comparereports.jsp");
  }

}