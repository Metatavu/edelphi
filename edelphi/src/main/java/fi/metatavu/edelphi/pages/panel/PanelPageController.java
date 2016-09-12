package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.utils.RequestUtils;

public abstract class PanelPageController extends PageController {

  public abstract void processPageRequest(PageRequestContext pageRequestContext);

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new IllegalStateException("PanelPageController has no panel");
    }
    setJsDataVariable(pageRequestContext, "securityContextId", panel.getId().toString());
    setJsDataVariable(pageRequestContext, "securityContextType", "PANEL");
    
    processPageRequest(pageRequestContext);
  }
}
