package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.jsons.JSONController;

public class CreatePanelExpertGroupUserJSONRequestController extends JSONController {

  public CreatePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    
    Long expertiseGroupId = jsonRequestContext.getLong("expertiseGroupId");
    PanelUserExpertiseGroup expertiseGroup = panelUserExpertiseGroupDAO.findById(expertiseGroupId);
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    PanelUser panelUser = panelUserDAO.findById(panelUserId);
    Double weight = jsonRequestContext.getDouble("weight");

    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.create(expertiseGroup, panelUser, weight);
    
    jsonRequestContext.addResponseParameter("id", panelExpertiseGroupUser.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelExpertiseGroupUser.getPanelUser().getUser().getFullName(true, false));
    jsonRequestContext.addResponseParameter("email", panelExpertiseGroupUser.getPanelUser().getUser().getDefaultEmailAsString());
  }
  
}
