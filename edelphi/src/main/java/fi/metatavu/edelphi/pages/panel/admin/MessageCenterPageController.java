package fi.metatavu.edelphi.pages.panel.admin;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class MessageCenterPageController extends PanelPageController {

  public MessageCenterPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    PanelUserDAO panelUserDAO = new PanelUserDAO();

    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("panelUsers", panelUserDAO.listByPanelAndStamp(panel, panel.getCurrentStamp()));
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    setJsDataVariable(pageRequestContext, "panelId", panel.getId().toString());
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/messagecenter.jsp");
  }

}