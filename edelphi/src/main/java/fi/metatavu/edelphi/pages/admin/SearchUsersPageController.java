package fi.metatavu.edelphi.pages.admin;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

public class SearchUsersPageController extends DelfoiPageController {

  public SearchUsersPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USERS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/searchusers.jsp");
  }

}