package fi.metatavu.edelphi.users;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.batch.i18n.BatchMessages;
import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.drafts.FormDraftDAO;
import fi.metatavu.edelphi.dao.orders.OrderHistoryDAO;
import fi.metatavu.edelphi.dao.panels.*;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentCategoryDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageTemplateDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.resources.ResourceLockDAO;
import fi.metatavu.edelphi.dao.users.*;
import fi.metatavu.edelphi.domainmodel.base.*;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.*;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.queries.QueryPageController;
import org.apache.commons.lang3.LocaleUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.Defaults;
import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.keycloak.KeycloakController;
import fi.metatavu.edelphi.keycloak.KeycloakException;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Controller for users
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class UserController {
  
  private static final String KEYCLOAK_AUTH_SOURCE = "Keycloak";

  @Inject
  private Logger logger;

  @Inject
  private KeycloakController keycloakController;

  @Inject
  private SettingsController settingsController;
  
  @Inject
  private UserIdentificationDAO userIdentificationDAO;

  @Inject
  private AuthSourceDAO authSourceDAO;
  
  @Inject
  private UserPictureDAO userPictureDAO;

  @Inject
  private UserDAO userDAO;

  @Inject
  private PanelUserDAO panelUserDAO;

  @Inject
  private PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO;

  @Inject
  private PanelUserGroupDAO panelUserGroupDAO;

  @Inject
  private UserRoleDAO userRoleDAO;

  @Inject
  private UserActivationDAO userActivationDAO;

  @Inject
  private UserPasswordDAO userPasswordDAO;

  @Inject
  private UserNotificationDAO userNotificationDAO;

  @Inject
  private UserEmailDAO userEmailDAO;

  @Inject
  private DelfoiUserDAO delfoiUserDAO;

  @Inject
  private UserSettingDAO userSettingDAO;

  @Inject
  private Mailer mailer;

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private BatchMessages batchMessages;

  @Inject
  private BulletinReadDAO bulletinReadDAO;

  @Inject
  private QueryReplyDAO queryReplyDAO;

  @Inject
  private OrderHistoryDAO orderHistoryDAO;

  @Inject
  private PanelBulletinDAO panelBulletinDAO;

  @Inject
  private DelfoiBulletinDAO delfoiBulletinDAO;

  @Inject
  private FormDraftDAO formDraftDAO;

  @Inject
  private PanelDAO panelDAO;

  @Inject
  private PanelInvitationDAO panelInvitationDAO;

  @Inject
  private PanelStampDAO panelStampDAO;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private QueryQuestionCommentCategoryDAO queryQuestionCommentCategoryDAO;

  @Inject
  private QueryPageDAO queryPageDAO;

  @Inject
  private QueryPageTemplateDAO queryPageTemplateDAO;

  @Inject
  private QuerySectionDAO querySectionDAO;

  @Inject
  private ResourceDAO resourceDAO;

  @Inject
  private ResourceLockDAO resourceLockDAO;

  private static final long ADMINISTRATORS_ROLE_ID = 2;

  private static final long ADMIN_USER_ID = 1;

  /**
   * Creates new user to the system. Method check whether user already exists and uses existing user if one is available.
   * 
   * Method also ensures that user exists in the Keycloak.
   * 
   * @param firstName first name
   * @param lastName last name
   * @param email email
   * @param password password
   * @param locale locale
   * @param creator creating user
   * @return created or existing user
   * @throws KeycloakException thrown when Keycloak related error occurs
   */
  public User createUser(String firstName, String lastName, String email, String password, Locale locale, User creator) throws KeycloakException {
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    User user = userEmail == null ? null : userEmail.getUser();
    Delfoi delfoi = settingsController.getDelfoi();
    DelfoiDefaults defaults = settingsController.getDefaults(delfoi);
    
    if (user == null) {
      user = userDAO.create(firstName, lastName, null, creator, Defaults.NEW_USER_SUBSCRIPTION_LEVEL, null, null, locale.getLanguage());
      userSettingDAO.create(user, UserSettingKey.MAIL_COMMENT_REPLY, "1");
      userEmail = userEmailDAO.create(user, email);
      userDAO.addUserEmail(user, userEmail, true, creator);
    }

    keycloakController.createUser(user, password, false, true);
    
    DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(delfoi, user);
    
    if (delfoiUser == null) {      
      DelfoiUserRole delfoiUserRole = defaults.getDefaultDelfoiUserRole();
      delfoiUserDAO.create(delfoi, user, delfoiUserRole, creator);
    }
    
    return user;
  }

  /**
   * Returns whether user has comment reply notifications enabled
   *
   * @param user user
   * @return whether user has comment reply notifications enabled
   */
  public boolean isNotifyCommentReplyEnabled(User user) {
    if (user == null) {
      return false;
    }

    UserSetting userSetting = userSettingDAO.findByUserAndKey(user, UserSettingKey.MAIL_COMMENT_REPLY);
    return userSetting != null && "1".equals(userSetting.getValue());
  }

  /**
   * Notifies user about new reply to his/her comment
   *
   * @param baseUrl System base URL
   * @param reply reply comment
   * @param panel panel
   */
  public void notifyCommentReply(String baseUrl, QueryQuestionComment reply, Panel panel) {
    try {
      QueryQuestionComment parentComment = reply.getParentComment();
      if (parentComment == null) {
        return;
      }

      User parentCommentCreator = parentComment.getCreator();
      Locale locale = LocaleUtils.toLocale(parentCommentCreator.getLocale());
      QueryPage queryPage = parentComment.getQueryPage();
      Query query = queryPage.getQuerySection().getQuery();

      String pageUrl = queryPageController.getPageUrl(baseUrl, panel, queryPage);
      String subject = batchMessages.getText(locale, "mail.newReply.template.subject");
      String content = batchMessages.getText(locale, "mail.newReply.template.content", panel.getName(), query.getName(), pageUrl);

      Email email = EmailBuilder.startingBlank()
        .from(settingsController.getEmailFromAddress())
        .to(parentCommentCreator.getDefaultEmailAsString())
        .withSubject(subject)
        .withPlainText(content)
        .buildEmail();

      mailer.sendMailAsync(email);
    } catch (Exception e) {
      logger.error("Failed to notify user about new comment reply", e);
    }
  }

  /**
   * Finds user by Keycloak id
   * 
   * @param userId Keycloak user id
   * @return user or null if not found
   */
  public User findUserByKeycloakId(UUID userId) {
    AuthSource authSource = authSourceDAO.findByStrategy(KEYCLOAK_AUTH_SOURCE);
    if (authSource == null) {
      logger.error("Could not find Keycloak auth source");
    }

    User user = userIdentificationDAO.findUserByAuthSourceAndExternalId(userId.toString(), authSource);
    if (user != null && user.getArchived()) {
      return null;
    }

    return user;
  }

  /**
   * Finds user by email
   *
   * @param email email
   * @return user or null if not found
   */
  public User findUserByEmail(String email) {
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    User user = userEmail == null ? null : userEmail.getUser();
    return user == null || user.getArchived() ? null : user;
  }

  /**
   * Returns Keycloak id for an user
   * 
   * @param user user
   * @return Keycloak id or null if id could not be resolved
   */
  public UUID getUserKeycloakId(User user) {
    AuthSource authSource = authSourceDAO.findByStrategy(KEYCLOAK_AUTH_SOURCE);
    if (authSource == null) {
      logger.error("Could not find Keycloak auth source");
    }
    
    List<UserIdentification> userIdentifications = userIdentificationDAO.listByUserAndAuthSource(user, authSource);
    if (userIdentifications.size() == 1) {
      return UUID.fromString(userIdentifications.get(0).getExternalId());
    }
    
    if (userIdentifications.size() > 1) {
      logger.warn("User {} has more than one identity", user.getId());
    }
    
    return new UUID(0L, 0L);
  }

  /**
   * Returns path for profile image
   * 
   * @param user user
   * @return path for profile image
   */
  public String getProfileImagePath(User user) {
    if (user == null) {
      return null;
    }
    
    UserPicture userPicture = userPictureDAO.findByUser(user);
    if (userPicture == null) {
      return null;
    }
    
    return String.format("/user/picture.binary?userId=%d", user.getId());
  }

  /**
   * List users to archive
   *
   * @param waitDays wait days before archiving
   * @param maxResults max results
   * @return users to archive
   */
  public List<User> listUsersToArchive(int waitDays, int maxResults) {
    Date before = Date.from(OffsetDateTime.now().minusDays(waitDays).toInstant());

    UserRole excludeRole = userRoleDAO.findById(ADMINISTRATORS_ROLE_ID);
    return userDAO.listUsersToArchive(before, maxResults, excludeRole);
  }

  /**
   * List users to delete
   *
   * @param waitDays wait this amount of days before deleting archived users
   * @param maxResults max results
   * @return users to delete
   */
  public List<User> listUsersToDelete(int waitDays, int maxResults) {
    Date before = Date.from(OffsetDateTime.now().minusDays(waitDays).toInstant());
    return userDAO.listUsersToDelete(before, maxResults);
  }

  /**
   * Archive user
   *
   * @param user user to archive
   */
  public void archiveUser(User user) {
    delfoiUserDAO.listAllByUser(user).forEach(delfoiUserDAO::archive);
    user.setLastModified(new Date());
    user.setArchived(true);
    userDAO.persist(user);
  }

  /**
   * Delete user
   *
   * @param user user to delete
   */
  public void deleteUser(User user) {
    transferUserOwnedItemsToAdmin(user);

    AuthSource authSource = keycloakController.getKeycloakAuthSource();
    List<UserIdentification> userIdentifications = userIdentificationDAO.listByUserAndAuthSource(user, authSource);
    for (UserIdentification userIdentification : userIdentifications) {
      try {
        if (keycloakController.getUser(userIdentification.getExternalId()) != null) {
          keycloakController.deleteUser(userIdentification.getExternalId());
        }
      } catch (KeycloakException e) {
        logger.error("Failed to delete user from Keycloak: {}", e.getMessage());
        throw new RuntimeException(e);
      }
    }

    User admin = userDAO.findById(ADMIN_USER_ID);
    user = userDAO.updateDefaultEmail(
      user,
      null,
      admin
    );

    user = userDAO.removeAllUserEmails(
      user,
      admin
    );

    userDAO.persist(user);

    List<UserEmail> emails = userEmailDAO.listByUser(user);
    emails.forEach(userEmailDAO::delete);
    userIdentificationDAO.listByUser(user).forEach(userIdentificationDAO::delete);
    bulletinReadDAO.listByUser(user).forEach(bulletinReadDAO::delete);
    userSettingDAO.listByUser(user).forEach(userSettingDAO::delete);

    List<PanelUser> panelUsers = panelUserDAO.listAllByUser(user);
    for (PanelUser panelUser : panelUsers) {
      panelExpertiseGroupUserDAO.listByUser(panelUser).forEach(panelExpertiseGroupUserDAO::delete);
      panelUserDAO.delete(panelUser);
    }

    UserActivation userActivation = userActivationDAO.findByUser(user);
    if (userActivation != null) {
      userActivationDAO.delete(userActivation);
    }

    UserPicture userPicture = userPictureDAO.findByUser(user);
    if (userPicture != null) {
      userPictureDAO.delete(userPicture);
    }

    userNotificationDAO.listByUser(user).forEach(userNotificationDAO::delete);
    userPasswordDAO.listAllByUser(user).forEach(userPasswordDAO::delete);

    delfoiUserDAO.listAllByUser(user).forEach(delfoiUserDAO::delete);

    List<PanelUserGroup> groups = panelUserGroupDAO.listUserPanelGroups(user);
    for (PanelUserGroup group : groups) {
      group.removeUser(user);
      panelUserGroupDAO.persist(group);
    }

    List<QueryReply> replies = queryReplyDAO.listAllByUser(user);
    for (QueryReply reply : replies) {
      reply.setUser(null);
      queryReplyDAO.persist(reply);
    }

    orderHistoryDAO.listAllByUser(user).forEach(orderHistoryDAO::delete);
    userDAO.delete(user);
  }

  /**
   * Clear all creator and lastModifier fields where this user is used.
   * The fields are set to the main admin user.
   *
   * @param user user whose fields to reset
   */
  public void transferUserOwnedItemsToAdmin(User user) {
    User resetUser = userDAO.findById(ADMIN_USER_ID);

    panelUserDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelUserDAO.persist(entity);
    });
    panelUserDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelUserDAO.persist(entity);
    });

    panelBulletinDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelBulletinDAO.persist(entity);
    });
    panelBulletinDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelBulletinDAO.persist(entity);
    });

    delfoiUserDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      delfoiUserDAO.persist(entity);
    });
    delfoiUserDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      delfoiUserDAO.persist(entity);
    });

    formDraftDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      formDraftDAO.persist(entity);
    });
    formDraftDAO.listAllByModifier(user).forEach(entity -> {
      formDraftDAO.persist(entity);
    });

    panelDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelDAO.persist(entity);
    });
    panelDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelDAO.persist(entity);
    });

    delfoiBulletinDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      delfoiBulletinDAO.persist(entity);
    });
    delfoiBulletinDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      delfoiBulletinDAO.persist(entity);
    });

    panelInvitationDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelInvitationDAO.persist(entity);
    });
    panelInvitationDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelInvitationDAO.persist(entity);
    });

    panelStampDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelStampDAO.persist(entity);
    });
    panelStampDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelStampDAO.persist(entity);
    });

    panelUserGroupDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelUserGroupDAO.persist(entity);
    });
    panelUserGroupDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelUserGroupDAO.persist(entity);
    });

    queryQuestionCommentDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryQuestionCommentDAO.persist(entity);
    });
    queryQuestionCommentDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryQuestionCommentDAO.persist(entity);
    });

    queryQuestionCommentCategoryDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryQuestionCommentCategoryDAO.persist(entity);
    });
    queryQuestionCommentCategoryDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryQuestionCommentCategoryDAO.persist(entity);
    });

    queryReplyDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryReplyDAO.persist(entity);
    });
    queryReplyDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryReplyDAO.persist(entity);
    });

    queryPageDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryPageDAO.persist(entity);
    });
    queryPageDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryPageDAO.persist(entity);
    });

    queryPageTemplateDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryPageTemplateDAO.persist(entity);
    });
    queryPageTemplateDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryPageTemplateDAO.persist(entity);
    });

    querySectionDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      querySectionDAO.persist(entity);
    });
    querySectionDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      querySectionDAO.persist(entity);
    });

    resourceDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      resourceDAO.persist(entity);
    });
    resourceDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      resourceDAO.persist(entity);
    });

    resourceLockDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      resourceLockDAO.persist(entity);
    });
    resourceLockDAO.listAllByModifier(user).forEach(entity -> {
      resourceLockDAO.persist(entity);
    });

    userDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      userDAO.persist(entity);
    });
    userDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      userDAO.persist(entity);
    });

  }
}
