package fi.metatavu.edelphi.pages;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class LogoutPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String redirectUrl = pageRequestContext.getString("redirectUrl");
    if (StringUtils.isBlank(redirectUrl)) {
      redirectUrl = String.format("%s/index.page", RequestUtils.getBaseUrl(pageRequestContext.getRequest()));
    }
    
    RequestUtils.logoutUser(pageRequestContext, redirectUrl);
    
    if (StringUtils.isBlank(pageRequestContext.getRedirectURL())) {
      pageRequestContext.setRedirectURL(redirectUrl);
    }
  }
}
