package fi.metatavu.edelphi.jsons.resources;

import java.util.Locale;

import fi.metatavu.edelphi.smvc.AccessDeniedException;
import fi.metatavu.edelphi.smvc.LoginRequiredException;
import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvc.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class CreateFolderJSONRequestController extends JSONController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long resourceId = requestContext.getLong("parentFolderId");
    
    Resource resource = resourceDAO.findById(resourceId);
    
    Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
    
    if (resourcePanel != null) {
      authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
    } else {
      Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
      authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
    }
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();

    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Folder parentFolder = folderDAO.findById(parentFolderId);
    
    if (ResourceUtils.isUrlNameAvailable(urlName, parentFolder)) {
      Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
      Folder folder = folderDAO.create(loggedUser, name, urlName, parentFolder, indexNumber);
      jsonRequestContext.addResponseParameter("folderId", folder.getId());
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
  }

}
