package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class LoginPageController extends PageController {
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Boolean logout = pageRequestContext.getBoolean("logoff");
    if (logout) {
      RequestUtils.logoutUser(pageRequestContext, null);
    }
 
    Long authSourceId = AuthUtils.getAuthSource("Keycloak").getId();
    // String registerUrl = AuthUtils.getKeycloakStrategy().getRegisterUrl();
    
    // pageRequestContext.getRequest().setAttribute("registerUrl", registerUrl);
    pageRequestContext.getRequest().setAttribute("authSourceId", authSourceId);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/login.jsp");
  }

}
