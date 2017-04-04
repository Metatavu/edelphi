package fi.metatavu.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManageQueriesPageController extends PanelPageController {

  public ManageQueriesPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_QUERIES;
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managequeries.jsp");
  }
}