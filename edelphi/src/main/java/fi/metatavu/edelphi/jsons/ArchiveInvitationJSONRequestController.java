package fi.metatavu.edelphi.jsons;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.jsons.JSONController;

public class ArchiveInvitationJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Long invitationId = jsonRequestContext.getLong("invitationId");
    PanelInvitation invitation = panelInvitationDAO.findById(invitationId);
    
    if (invitation != null) {
      UserEmail userEmail = userEmailDAO.findByAddress(invitation.getEmail());
      if (userEmail != null && userEmail.getUser().getId().equals(loggedUser.getId())) {
        panelInvitationDAO.archive(invitation, loggedUser);
      }
    }
  }
}
