package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.utils.ActionUtils;

public class ManagePanelDocumentsPageController extends PanelPageController {

  public ManagePanelDocumentsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();

    Long panelId = pageRequestContext.getLong("panelId");
    Panel panel = panelDAO.findById(panelId);
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);

    String googlePickerApiKey = System.getenv("GOOGLE_PICKER_API_KEY");
    String googlePickerClientId = System.getenv("GOOGLE_PICKER_CLIENT_ID");
    String googlePickerAppId = System.getenv("GOOGLE_PICKER_APP_ID");

    pageRequestContext.getRequest().setAttribute("googlePickerApiKey", googlePickerApiKey);
    pageRequestContext.getRequest().setAttribute("googlePickerAppId", googlePickerAppId);
    pageRequestContext.getRequest().setAttribute("googlePickerClientId", googlePickerClientId);
    pageRequestContext.getRequest().setAttribute("panelId", panelId);

    pageRequestContext.setIncludeJSP("/jsp/panels/managepaneldocuments.jsp");
  }
}
