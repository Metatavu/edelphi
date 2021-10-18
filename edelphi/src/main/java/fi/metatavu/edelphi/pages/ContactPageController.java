package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ContactPageController extends PageController {

  public ContactPageController() {
    super();
    // TODO: User should be at least guest to access this page
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String email = Base64.getEncoder().encodeToString(System.getenv("CONTACT_EMAIL").getBytes(StandardCharsets.UTF_8));

    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    pageRequestContext.getRequest().setAttribute("email", email);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/contact.jsp");
  }

}
