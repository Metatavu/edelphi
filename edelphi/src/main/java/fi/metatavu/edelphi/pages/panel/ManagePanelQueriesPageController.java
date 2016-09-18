package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.utils.ActionUtils;

public class ManagePanelQueriesPageController extends PanelPageController {

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();

    Long panelId = pageRequestContext.getLong("panelId");
    Panel panel = panelDAO.findById(panelId);
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepanelqueries.jsp");
  }
}
