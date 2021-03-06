package fi.metatavu.edelphi.pages.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.panel.PanelPageController;

public class SelectQueryReportsPageController extends PanelPageController {

  public SelectQueryReportsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.ACCESS_PANEL_QUERY_RESULTS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/selectqueryreports.jsp");
  }

}