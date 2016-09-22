package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserGroupDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.jsons.JSONController;

public class ArchiveUserGroupJSONRequestController extends JSONController {

  public ArchiveUserGroupJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long userGroupId = jsonRequestContext.getLong("userGroupId");
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    PanelUserGroup panelUserGroup = panelUserGroupDAO.findById(userGroupId);
    panelUserGroupDAO.archive(panelUserGroup);
  }
  
}
