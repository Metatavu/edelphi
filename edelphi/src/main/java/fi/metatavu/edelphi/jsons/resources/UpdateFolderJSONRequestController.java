package fi.metatavu.edelphi.jsons.resources;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class UpdateFolderJSONRequestController extends JSONController {

  public UpdateFolderJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();

    Long folderId = jsonRequestContext.getLong("folderId");
    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);

    Folder folder = folderDAO.findById(folderId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    if (ResourceUtils.isUrlNameAvailable(urlName, folder.getParentFolder(), folder)) {
      folderDAO.updateName(folder, name, urlName, loggedUser);
      jsonRequestContext.addResponseParameter("folderId", folder.getId());
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
    
  }
}
