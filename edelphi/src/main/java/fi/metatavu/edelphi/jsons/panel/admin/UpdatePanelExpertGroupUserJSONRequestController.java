package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.jsons.JSONController;

public class UpdatePanelExpertGroupUserJSONRequestController extends JSONController {

  public UpdatePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    
    Long newExpertiseGroupId = jsonRequestContext.getLong("newExpertiseGroupId");
    PanelUserExpertiseGroup newExpertiseGroup = panelUserExpertiseGroupDAO.findById(newExpertiseGroupId);
    Long panelExpertGroupUserId = jsonRequestContext.getLong("panelExpertGroupUserId");
    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.findById(panelExpertGroupUserId);

    panelExpertiseGroupUser = panelExpertiseGroupUserDAO.updateGroup(panelExpertiseGroupUser, newExpertiseGroup);
    
    jsonRequestContext.addResponseParameter("id", panelExpertiseGroupUser.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelExpertiseGroupUser.getPanelUser().getUser().getFullName(true, false));
    jsonRequestContext.addResponseParameter("email", panelExpertiseGroupUser.getPanelUser().getUser().getDefaultEmailAsString());
  }
  
}
