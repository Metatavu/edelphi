package fi.metatavu.edelphi.jsons.resources;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;

public class SetResourceVisibilityJSONRequestController extends JSONController {

  public SetResourceVisibilityJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    ResourceDAO resourceDAO = new ResourceDAO();
    UserDAO userDAO = new UserDAO();

    Long resourceId = jsonRequestContext.getLong("resourceId");
    Boolean visible = "1".equals(jsonRequestContext.getString("visible"));
    
    Resource resource = resourceDAO.findById(resourceId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    resourceDAO.updateVisible(resource, visible, loggedUser);
  }
}
