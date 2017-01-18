package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;
import java.util.UUID;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreateInvitationJSONRequestController extends JSONController {

  public CreateInvitationJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    UserDAO userDAO = new UserDAO();
    QueryDAO queryDAO = new QueryDAO();
    
    User creator = userDAO.findById(jsonRequestContext.getLoggedUserId());

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    String invitationMessage = jsonRequestContext.getString("invitationMessage");

    Long queryId = jsonRequestContext.getLong("queryId");
    Query query = queryId == null ? null : queryDAO.findById(queryId);

    String baseUrl = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
    String email = jsonRequestContext.getString("email");
    Long userId = jsonRequestContext.getLong("userId");
    
    if (isDeclined(panel, query, email)) {
      // If user has already declined the request, we wont bother him/her anymore
      String personName = getPersonName(email);
      jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.userDeclinedAlready", new String[] { personName }));
    } else {
      String inviteEmail = email;
      
      // Get the user e-mail address. Since the user interface passes on obfuscated
      // e-mail addresses for existing users, they need to be reverted back

      if (userId != null && userId > 0) {
        User user = userDAO.findById(userId);
        inviteEmail = user.getDefaultEmailAsString();
      }

      // Create invitations and mail them to users
      PanelInvitation invitation = sendInvitation(locale, panel, query, creator, inviteEmail, invitationMessage, baseUrl);
      if (invitation == null) {
        jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.noInvitationsSent"));
      } else {
        if (invitation.getState() == PanelInvitationState.SEND_FAIL) {
          jsonRequestContext.addMessage(Severity.ERROR, messages.getText(locale, "panel.admin.inviteUsers.invitationsSendFailed"));
        } else {
          jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.inviteUsers.invitationSent"));
        }
      }
    }
  }
  
  private boolean isDeclined(Panel panel, Query query, String email) {
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();

    PanelInvitation invitation = panelInvitationDAO.findByPanelAndQueryAndEmail(panel, query, email);

    if (invitation != null && invitation.getState() == PanelInvitationState.DECLINED) {
      return true;
    }
    
    return false;
  }
  
  private String getPersonName(String email) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    return userEmail == null ? email : userEmail.getUser().getFullName(false, true);
  }

  private PanelInvitation sendInvitation(Locale locale, Panel panel, Query query, User creator, String email, String invitationMessage, String baseUrl) {
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    
    Messages messages = Messages.getInstance();
    
    String panelNameReplace = messages.getText(locale, "panel.admin.inviteUsers.panelNameReplace");
    String acceptReplace = messages.getText(locale, "panel.admin.inviteUsers.acceptReplace");
    String declineReplace = messages.getText(locale, "panel.admin.inviteUsers.declineReplace");
    String senderReplace = messages.getText(locale, "panel.admin.inviteUsers.senderReplace");

    if (invitationMessage == null || invitationMessage.indexOf(acceptReplace) == -1) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_MAIL_TEMPLATE, messages.getText(locale, "exception.1000.noAcceptLink"));
    }
    
    // See if the user already has an existing invitation that points to the invitation target,
    // i.e. the front page of the panel or the front page of a single query within the panel 
    PanelInvitation invitation = panelInvitationDAO.findByPanelAndQueryAndEmail(panel, query, email);
    
    // Reuse the hash of an existing invitation, if we have one 
    String invitationHash = invitation == null ? UUID.randomUUID().toString() : invitation.getHash();
    
    // Construct the invitation e-mail message
    String mailSubject = messages.getText(locale, "panel.admin.inviteUsers.mailSubject");
    int index = mailSubject.indexOf(panelNameReplace);
    if (index >= 0) {
      mailSubject = mailSubject.replace(panelNameReplace, panel.getName());
    }
    
    String mailContent = invitationMessage;
    index = mailContent.indexOf(panelNameReplace);
    if (index >= 0) {
      mailContent = mailContent.replace(panelNameReplace, panel.getName());
    }
    
    index = mailContent.indexOf(acceptReplace);
    if (index >= 0) {
      mailContent = mailContent.replace(acceptReplace, baseUrl + "/joinpanel.page?panelId=" + panel.getId() + "&hash=" + invitationHash + "&join=1");
    }
    
    index = mailContent.indexOf(declineReplace);
    if (index >= 0) {
      mailContent = mailContent.replace(declineReplace, baseUrl + "/joinpanel.page?panelId=" + panel.getId() + "&hash=" + invitationHash + "&join=0");
    }
    
    index = mailContent.indexOf(senderReplace);
    if (index >= 0) {
      mailContent = mailContent.replace(senderReplace, creator.getFullName(false, false));
    }
    
    // Create or update the invitation
    
    if (invitation == null) {
      // Invitation doesn't exist, so create both a new e-mail message and the actual invitation
      invitation = panelInvitationDAO.create(panel, query, email, invitationHash, panel.getDefaultPanelUserRole(), PanelInvitationState.IN_QUEUE, creator);
    } else {
      // Otherwise, send new invitation
      invitation = panelInvitationDAO.updateState(invitation, PanelInvitationState.IN_QUEUE, creator);
    }
    
    if (MailUtils.sendMail(creator.getDefaultEmailAsString(), email, mailSubject, mailContent)) {
      panelInvitationDAO.updateState(invitation, PanelInvitationState.PENDING, creator);
    } else {
      panelInvitationDAO.updateState(invitation, PanelInvitationState.SEND_FAIL, creator);
    }
    
    return invitation;
  }

}
