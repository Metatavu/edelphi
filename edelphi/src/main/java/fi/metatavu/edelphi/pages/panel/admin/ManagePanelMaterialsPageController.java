package fi.metatavu.edelphi.pages.panel.admin;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.MaterialUtils;

public class ManagePanelMaterialsPageController extends PanelPageController {

  public ManagePanelMaterialsPageController() {
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

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    try {
      pageRequestContext.getRequest().setAttribute("panel", panel);
      pageRequestContext.getRequest().setAttribute("materials", MaterialUtils.listPanelMaterials(panel, true));

      pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managematerials.jsp");
    }
    catch (Exception e) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
  }

}