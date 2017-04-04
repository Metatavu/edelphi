package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManagePanelPageController extends PanelPageController {
  
  public ManagePanelPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepanel.jsp");
  }
}
