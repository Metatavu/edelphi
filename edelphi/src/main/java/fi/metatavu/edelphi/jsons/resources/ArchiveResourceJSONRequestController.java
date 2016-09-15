package fi.metatavu.edelphi.jsons.resources;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class ArchiveResourceJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long resourceId = requestContext.getLong("resourceId");
    
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
    ResourceDAO resourceDAO = new ResourceDAO();
    UserDAO userDAO = new UserDAO();

    Long resourceId = jsonRequestContext.getLong("resourceId");
    
    Resource resource = resourceDAO.findById(resourceId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    String urlName = resource.getId() + "." + resource.getUrlName();
    resourceDAO.updateUrlNameAndArchived(resource, urlName, Boolean.TRUE, loggedUser);
  }
}
