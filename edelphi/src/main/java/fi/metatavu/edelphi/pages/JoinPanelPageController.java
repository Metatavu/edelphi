package fi.metatavu.edelphi.pages;

import java.util.Locale;
import java.util.UUID;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.auth.OAuthAccessToken;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.KeycloakUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.UserUtils;

public class JoinPanelPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Long panelId = pageRequestContext.getLong("panelId");
    String hash = pageRequestContext.getString("hash");
    boolean accepted = new Integer(1).equals(pageRequestContext.getInteger("join"));
    
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    PanelInvitation panelInvitation = panelInvitationDAO.findByHash(hash);
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    if (panelInvitation == null || !panelInvitation.getPanel().getId().equals(panelId)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_INVITATION, messages.getText(locale, "exception.1008.invalidInvitation"));
    }

    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();

    if (accepted) {
      
      // Invitation was accepted
      
      User user = RequestUtils.getUser(pageRequestContext);
      UserEmail userEmail = userEmailDAO.findByAddress(panelInvitation.getEmail());
      Panel panel = panelInvitation.getPanel();
      Query query = panelInvitation.getQuery();
      
      if (user != null && userEmail != null && !user.getId().equals(userEmail.getUser().getId())) {

        // Exception; someone is already logged in but the invitation email resolves to another account
        // -> ask the user to log out and try again

        AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("dualAccount", Boolean.TRUE);
        pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
        pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
      }
      else if (user != null && userEmail == null) {

        // Exception; someone is already logged in but the invitation email resolves to no account
        // -> ask whether to create a new account or link invitation email to current account 

        AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("confirmLinking", Boolean.TRUE);
        pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
        pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
        setJsDataVariable(pageRequestContext, "invitationUserMail", panelInvitation.getEmail());
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
      }
      else {
        if (user == null && userEmail == null) {

          // No one is logged in and the invitation email is available
          // -> automatically create a new account
          
          user = UserUtils.createUser(null, null, null, null, locale);          
          userEmail = userEmailDAO.create(user, panelInvitation.getEmail());
          userDAO.addUserEmail(user, userEmail, true, user);
          Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
          DelfoiDefaults delfoiDefaults = RequestUtils.getDefaults(pageRequestContext);
          delfoiUserDAO.create(delfoi, user, delfoiDefaults.getDefaultDelfoiUserRole(), user);
        }
        else {
          user = userEmail.getUser();
        }
        
        // Ensure user has a Keycloak user
        
        KeycloakUtils.createUser(user, UUID.randomUUID().toString(), false, true);
        
        // Ensure panel membership 
        
        PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panelInvitation.getPanel(), user, panelInvitation.getPanel().getCurrentStamp());
        if (panelUser == null) {
          panelUser = panelUserDAO.create(panelInvitation.getPanel(), user, panelInvitation.getRole(), PanelUserJoinType.INVITED, panelInvitation.getPanel().getCurrentStamp(), user);
        }
        
        // Mark invitation as accepted
        
        panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.ACCEPTED, user);
        
        // Ensure user is logged in
        
        RequestUtils.loginUser(pageRequestContext, user, null);
        
        OAuthAccessToken impersonatedToken = KeycloakUtils.getImpersonatedToken(user);
        if (impersonatedToken != null) {
          AuthUtils.storeOAuthAccessToken(pageRequestContext, KeycloakUtils.KEYCLOAK_AUTH_SOURCE, impersonatedToken);
        }
        
        // TODO if user has no password or external authentication, add a welcome message 
        
        // Redirect to the invitation target
        
        String redirectUrl = pageRequestContext.getRequest().getContextPath() + "/" + panel.getRootFolder().getUrlName();
        if (query != null) {
          redirectUrl += "/" + query.getUrlName();
        }
        pageRequestContext.setRedirectURL(redirectUrl);
      }
    }
    else {

      // Invitation was rejected
      
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.DECLINED, null);
      pageRequestContext.getRequest().setAttribute("statusCode", EdelfoiStatusCode.OK);
      pageRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.invitationDeclined"));
      pageRequestContext.setIncludeJSP("/jsp/pages/error.jsp");
    }
  }

  @Override
  public boolean isSynchronous() {
    return true;
  }
}
