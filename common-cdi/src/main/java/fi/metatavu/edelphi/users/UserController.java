package fi.metatavu.edelphi.users;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.edelphi.Defaults;
import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.dao.users.UserPictureDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.base.DelfoiUser;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserPicture;
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
  private UserEmailDAO userEmailDAO;

  @Inject
  private DelfoiUserDAO delfoiUserDAO;
  
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
   * Finds user by id
   * 
   * @param id id
   * @return user id
   */
  public User findUserById(Long id) {
    return userDAO.findById(id);
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
  
}
