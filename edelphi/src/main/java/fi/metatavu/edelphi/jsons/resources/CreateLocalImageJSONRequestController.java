package fi.metatavu.edelphi.jsons.resources;

import java.util.Locale;

import org.apache.commons.fileupload.FileItem;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.LocalImageDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class CreateLocalImageJSONRequestController extends JSONController {

  public CreateLocalImageJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();
    LocalImageDAO localImageDAO = new LocalImageDAO();
    
    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);

    FileItem file = jsonRequestContext.getFile("imageData");
    byte[] data = file.get();
    String contentType = file.getContentType();
    
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Folder parentFolder = folderDAO.findById(parentFolderId);
    
    if (ResourceUtils.isUrlNameAvailable(urlName, parentFolder)) {
      Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
      localImageDAO.create(name, urlName, contentType, data, parentFolder, loggedUser, indexNumber);
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
  }
  
}
