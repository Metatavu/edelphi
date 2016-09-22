package fi.metatavu.edelphi.jsons.admin;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;

public class ArchiveDelfoiBulletinJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    UserDAO userDAO = new UserDAO();

    Long bulletinId = jsonRequestContext.getLong("bulletinId");

    DelfoiBulletin bulletin = bulletinDAO.findById(bulletinId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    bulletinDAO.archive(bulletin, loggedUser);
  }
}
