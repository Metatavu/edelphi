package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelBulletinDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ViewBulletinPageController extends PanelPageController {

  public ViewBulletinPageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) { 
    // TODO: If query is hidden only users with manage material rights should be able to enter
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();

    Long bulletinId = pageRequestContext.getLong("bulletinId");
    PanelBulletin bulletin = panelBulletinDAO.findById(bulletinId);
    if (bulletin == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("bulletin", bulletin);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewbulletin.jsp");
  }
}
