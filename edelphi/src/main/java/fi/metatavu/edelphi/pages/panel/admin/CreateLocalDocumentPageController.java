package fi.metatavu.edelphi.pages.panel.admin;

import java.io.IOException;
import java.util.Locale;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreateLocalDocumentPageController extends PanelPageController {

  public CreateLocalDocumentPageController() {
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
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    try {
      pageRequestContext.getRequest().setAttribute("materials", MaterialUtils.listPanelMaterials(panel, true));
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
 
    ActionUtils.includeRoleAccessList(pageRequestContext);

    String googlePickerApiKey = System.getenv("GOOGLE_PICKER_API_KEY");
    String googlePickerClientId = System.getenv("GOOGLE_PICKER_CLIENT_ID");
    String googlePickerAppId = System.getenv("GOOGLE_PICKER_APP_ID");

    pageRequestContext.getRequest().setAttribute("googlePickerApiKey", googlePickerApiKey);
    pageRequestContext.getRequest().setAttribute("googlePickerAppId", googlePickerAppId);
    pageRequestContext.getRequest().setAttribute("googlePickerClientId", googlePickerClientId);
    pageRequestContext.getRequest().setAttribute("panelId", panel.getId());
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/createlocaldocument.jsp");
  }

}