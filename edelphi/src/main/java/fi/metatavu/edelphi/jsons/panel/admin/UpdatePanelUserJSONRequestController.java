package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdatePanelUserJSONRequestController extends JSONController {

  public UpdatePanelUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    Long newRoleId = jsonRequestContext.getLong("newRoleId");
    
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();

    PanelUser panelUser = panelUserDAO.findById(panelUserId);
    PanelUserRole panelUserRole = panelUserRoleDAO.findById(newRoleId);
    
    User modifier = RequestUtils.getUser(jsonRequestContext);

    // Update role of panel user
    
    if (!panelUser.getUser().getId().equals(jsonRequestContext.getLoggedUserId())) {
      panelUserDAO.updateRole(panelUser, panelUserRole, modifier);
    }
  }
  
}
