package fi.metatavu.edelphi.jsons.resources;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class MoveResourceJSONRequestController extends JSONController {

  public MoveResourceJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    ResourceDAO resourceDAO = new ResourceDAO();
    UserDAO userDAO = new UserDAO();

    Long resourceId = jsonRequestContext.getLong("resourceId");
    Long newParentResourceId = jsonRequestContext.getLong("newParentResourceId");
    
    Folder newParentFolder = folderDAO.findById(newParentResourceId);
    Resource resource = resourceDAO.findById(resourceId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    if (ResourceUtils.isUrlNameAvailable(resource.getUrlName(), newParentFolder)) {
      resourceDAO.updateParentFolder(resource, newParentFolder, loggedUser);
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
  }
}
