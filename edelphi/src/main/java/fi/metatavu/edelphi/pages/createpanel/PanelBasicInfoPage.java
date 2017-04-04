package fi.metatavu.edelphi.pages.createpanel;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.PageController;

public class PanelBasicInfoPage extends PageController {

  public PanelBasicInfoPage() {
    setAccessAction(DelfoiActionName.CREATE_PANEL, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/blocks/createpanel/panelbasicinfo.jsp");
  }
}
