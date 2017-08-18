package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

public class PrivacyPolicyController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/privacypolicy.jsp");
  }

}
