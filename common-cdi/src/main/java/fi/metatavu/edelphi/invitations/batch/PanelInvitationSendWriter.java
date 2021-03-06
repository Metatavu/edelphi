package fi.metatavu.edelphi.invitations.batch;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.batch.i18n.BatchMessages;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.settings.SettingsController;
import fi.metatavu.edelphi.users.UserController;

/**
 * Batch item writer for sending panel invitations
 * 
 * @author Antti Leppä
 */
@Named
public class PanelInvitationSendWriter extends TypedItemWriter<PanelInvitation> {

  @Inject
  private Logger logger;

  @Inject
  private SettingsController settingsController;

  @Inject
  private PanelController panelController;

  @Inject
  private UserController userController;

  @Inject
  private BatchMessages batchMessages;

  @Inject
  private PanelInvitationDAO panelInvitationDAO;
  
  @Inject
  private Mailer mailer;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private String invitationMessage;

  @Inject
  @JobProperty
  private String password;
  
  @Inject
  @JobProperty
  private Boolean skipInvitaion;

  @Inject
  @JobProperty
  private Long panelId;

  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private UUID loggedUserId;

  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
  }

  @Override
  public void write(List<PanelInvitation> items) throws Exception {
    if (skipInvitaion) {
      items.stream().forEach(this::addUser);
    } else {
      items.stream().forEach(this::sendInvitation);
    }
  }

  /**
   * Sends an invitation to panelist
   * 
   * @param panelInvitation panel invitation
   */
  private void sendInvitation(PanelInvitation panelInvitation) {
    User loggedUser = userController.findUserByKeycloakId(loggedUserId);
    
    try {
      String panelNameReplace = batchMessages.getText(locale, "batch.inviteUsers.panelNameReplace");
      String acceptReplace = batchMessages.getText(locale, "batch.inviteUsers.acceptReplace");
      String declineReplace = batchMessages.getText(locale, "batch.inviteUsers.declineReplace");
      String senderReplace = batchMessages.getText(locale, "batch.inviteUsers.senderReplace");
  
      if (invitationMessage == null || invitationMessage.indexOf(acceptReplace) == -1) {
        throw new PanelInvitationException(String.format("Accept link %s is missing from %s", acceptReplace, invitationMessage));
      }
      
      Panel panel = panelController.findPanelById(panelId);
      if (panel == null) {
        throw new PanelInvitationException("Invalid panel id");
      }
      
      String invitationHash = panelInvitation.getHash();    
      
      // Construct the invitation e-mail message
      String mailSubject = batchMessages.getText(locale, "batch.inviteUsers.mailSubject");
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
        mailContent = mailContent.replace(senderReplace, loggedUser.getFullName(false, false));
      }
      
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.BEING_SENT, loggedUser);
      
      Email email = EmailBuilder.startingBlank()
        .from(settingsController.getEmailFromAddress())
        .to(panelInvitation.getEmail())
        .withSubject(mailSubject)
        .withPlainText(mailContent)
        .buildEmail();
      
      mailer.sendMail(email);
      
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.PENDING, loggedUser);
    } catch (Exception e) {
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.SEND_FAIL, loggedUser);
      logger.error("Failed to send invitation email", e);
    }
  }
  
  /**
   * Adds user to panel
   * 
   * @param panelInvitation panel invitation
   */
  private void addUser(PanelInvitation panelInvitation) {
    User loggedUser = userController.findUserByKeycloakId(loggedUserId);
    
    try {
      Panel panel = panelController.findPanelById(panelId);
      if (panel == null) {
        throw new PanelInvitationException("Invalid panel id");
      }
      
      User user = userController.createUser(null, null, panelInvitation.getEmail(), password, locale, loggedUser);
      
      PanelUser panelUser = panelController.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
      if (panelUser == null) {
        panelUser = panelController.createPanelUser(panel, user, panelInvitation.getRole(), PanelUserJoinType.ADDED, loggedUser);
      } else {
        logger.warn(String.format("User %d already member of panel", user.getId()));
      }
      
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.ADDED, loggedUser);
    } catch (Exception e) {
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.SEND_FAIL, loggedUser);
      logger.error("Failed to send invitation email", e);
    }    
  }
}
