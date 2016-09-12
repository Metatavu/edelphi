package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class LoginPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Boolean logout = pageRequestContext.getBoolean("logoff");
    if (logout) {
      RequestUtils.logoutUser(pageRequestContext);
    }
    AuthUtils.includeAuthSources(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/pages/login.jsp");
  }

}
