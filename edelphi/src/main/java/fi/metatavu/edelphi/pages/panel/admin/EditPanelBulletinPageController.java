package fi.metatavu.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelBulletinDAO;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;

public class EditPanelBulletinPageController extends PanelPageController {

  public EditPanelBulletinPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_BULLETINS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();

    Long panelId = pageRequestContext.getLong("panelId");
    Long bulletinId = pageRequestContext.getLong("bulletinId");
    
    Panel panel = panelDAO.findById(panelId);
    PanelBulletin panelBulletin = panelBulletinDAO.findById(bulletinId);

    List<PanelBulletin> bulletins = panelBulletinDAO.listByPanelAndArchived(panel, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<PanelBulletin>() {
      @Override
      public int compare(PanelBulletin o1, PanelBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    pageRequestContext.getRequest().setAttribute("bulletin", panelBulletin);

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/editpanelbulletin.jsp");
  }

}