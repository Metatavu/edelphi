package fi.metatavu.edelphi.users;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.dao.users.UserPictureDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserPicture;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Controller for users
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class UserController {
  
  private static final String KEYCLOAK_AUTH_SOURCE = "Keycloak";
  private static final SubscriptionLevel NEW_USER_SUBSCRIPTION_LEVEL = SubscriptionLevel.BASIC;
  
  @Inject
  private Logger logger;

  @Inject
  private SettingsController settingsController; 

  @Inject
  private UserDAO userDAO;

  @Inject
  private UserEmailDAO userEmailDAO;
  
  @Inject
  private UserIdentificationDAO userIdentificationDAO;

  @Inject
  private AuthSourceDAO authSourceDAO;
  
  @Inject
  private UserPictureDAO userPictureDAO;

  @Inject
  private DelfoiUserDAO delfoiUserDAO;

  @Inject
  private DelfoiDAO delfoiDAO;

  @Inject
  private DelfoiDefaultsDAO delfoiDefaultsDAO;

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
    
    UserIdentification userIdentification = userIdentificationDAO.findByExternalId(userId.toString(), authSource);
    if (userIdentification != null) {
      return userIdentification.getUser();
    }
    
    return null;
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
   * Creates new local user using details from Keycloak access token
   * 
   * @param locale locale
   * @param accessToken Keycloak access token
   * @return created user
   */
  public User createUserFromKeycloakToken(Locale locale, AccessToken accessToken) {
    AuthSource authSource = authSourceDAO.findByStrategy(KEYCLOAK_AUTH_SOURCE);
    if (authSource == null) {
      logger.error("Could not find Keycloak auth source");
    }
    
    String firstName = accessToken.getGivenName();
    String lastName = accessToken.getFamilyName();
    String email = accessToken.getEmail();
    String externalId = accessToken.getId();
    User user;
    
    UserEmail existingUserEmail = email != null ? userEmailDAO.findByAddress(email) : null;
    if (existingUserEmail == null) {
      // Create User
      user = userDAO.create(firstName, lastName, null, null, NEW_USER_SUBSCRIPTION_LEVEL, null, null, locale.getLanguage());
  
      if (email != null) {
        // Create UserEmail
        UserEmail userEmail = userEmailDAO.create(user, email);
        userDAO.addUserEmail(user, userEmail, true, user);
      }

      Delfoi delfoi = delfoiDAO.findById(settingsController.getDelfoiId());
      DelfoiDefaults defaults = delfoiDefaultsDAO.findByDelfoi(delfoi);
      delfoiUserDAO.create(delfoi, user, defaults.getDefaultDelfoiUserRole(), user);
    } else {
      user = existingUserEmail.getUser();
    }
    
    userIdentificationDAO.create(user, externalId, authSource);
    
    return user;
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

}
