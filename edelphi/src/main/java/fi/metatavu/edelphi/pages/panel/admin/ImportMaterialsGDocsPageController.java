package fi.metatavu.edelphi.pages.panel.admin;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ImportMaterialsGDocsPageController extends PanelPageController {

  public ImportMaterialsGDocsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    pageRequestContext.setRedirectURL(panel.getFullPath());

  }

}