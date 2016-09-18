package fi.metatavu.edelphi.jsons.resources;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.resources.ResourceLockDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.resources.ResourceLock;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceLockUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class TouchResourceLockJSONRequestController extends JSONController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long resourceId = requestContext.getLong("resourceId");
    if (resourceId != null) {
      Resource resource = resourceDAO.findById(resourceId);
      Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
      if (resourcePanel != null) {
        authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
      }
      else {
        Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
        authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
      }
    }
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    ResourceDAO resourceDAO = new ResourceDAO(); 
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    
    Long resourceId = jsonRequestContext.getLong("resourceId");
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    if (resourceId != null) {
      Locale locale = jsonRequestContext.getRequest().getLocale();
      Resource resource = resourceDAO.findById(resourceId);
      ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
      if (resourceLock == null) {
        Messages messages = Messages.getInstance();
        throw new SmvcRuntimeException(EdelfoiStatusCode.RESOURCE_LOCK_NOT_FOUND, messages.getText(locale, "exception.1022.resourceLockNotFound"));
      }
      else {
        if (!resourceLock.getCreator().getId().equals(loggedUser.getId()))
          throw new AccessDeniedException(locale);
        
        ResourceLockUtils.touchResourceLock(resource);
      }
    }
  }
}
