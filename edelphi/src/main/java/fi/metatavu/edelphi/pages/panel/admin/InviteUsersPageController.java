package fi.metatavu.edelphi.pages.panel.admin;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

public class InviteUsersPageController extends PanelPageController {

  public InviteUsersPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_INVITATIONS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/inviteusers.jsp");
  }

}