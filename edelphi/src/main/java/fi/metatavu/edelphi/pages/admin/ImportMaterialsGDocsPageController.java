package fi.metatavu.edelphi.pages.admin;


import java.util.logging.Logger;

import fi.metatavu.edelphi.domainmodel.panels.Panel;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ImportMaterialsGDocsPageController extends DelfoiPageController {

  private static final Logger logger = Logger.getLogger(ImportMaterialsGDocsPageController.class.getName());

  public ImportMaterialsGDocsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    pageRequestContext.setRedirectURL(panel.getFullPath());
  }

}