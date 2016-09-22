package fi.metatavu.edelphi.jsons.resources;

import java.util.StringTokenizer;

import net.sf.json.JSONObject;
import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdateResourceIndexNumbersJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();

    JSONObject folderMap = JSONObject.fromObject(jsonRequestContext.getString("folderOrder"));
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");

    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    Folder folder = folderDAO.findById(parentFolderId);
    
    handleFolder(folder, folderMap, loggedUser);
  }

  private void handleFolder(Folder folder, JSONObject folderMap, User loggedUser) {
    ResourceDAO resourceDAO = new ResourceDAO();
    StringTokenizer tokx = new StringTokenizer(folderMap.getString(folder.getId().toString()), ",");
    
    int i = 0;
    while (tokx.hasMoreTokens()) {
      Long resourceId = Long.parseLong(tokx.nextToken());
      
      Resource resource = resourceDAO.findById(resourceId);
      resourceDAO.updateResourceIndexNumber(resource, i, loggedUser);
      
      if (!resource.getParentFolder().getId().equals(folder.getId()))
        resourceDAO.updateParentFolder(resource, folder, loggedUser);
      
      if (resource instanceof Folder) {
        handleFolder((Folder) resource, folderMap, loggedUser);
      }
      
      i++;
    }
  }
}
