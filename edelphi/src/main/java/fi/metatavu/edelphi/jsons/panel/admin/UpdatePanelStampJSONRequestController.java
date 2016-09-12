package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdatePanelStampJSONRequestController extends JSONController {

  public UpdatePanelStampJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    String name = jsonRequestContext.getString("title");
    String description = jsonRequestContext.getString("description");
    PanelStamp panelStamp = panelStampDAO.findById(jsonRequestContext.getLong("stampId"));
    panelStampDAO.update(panelStamp, name, description, panelStamp.getStampTime(), loggedUser);
  }
  
}
