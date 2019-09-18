package fi.metatavu.edelphi.pages.panel.admin;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

public class LiveViewPageController extends PanelPageController {

  public LiveViewPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.ACCESS_LIVE_VIEW;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/liveview.jsp");
  }

}