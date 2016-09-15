package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.jsons.JSONController;

public class RemovePanelExpertGroupUserJSONRequestController extends JSONController {

  public RemovePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    
    Long panelExpertGroupUserId = jsonRequestContext.getLong("panelExpertGroupUserId");
    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.findById(panelExpertGroupUserId);

    panelExpertiseGroupUserDAO.delete(panelExpertiseGroupUser);
  }
  
}
