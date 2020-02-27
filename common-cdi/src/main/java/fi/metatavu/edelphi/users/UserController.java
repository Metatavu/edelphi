package fi.metatavu.edelphi.users;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.dao.users.UserPictureDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserPicture;

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
  private UserIdentificationDAO userIdentificationDAO;

  @Inject
  private AuthSourceDAO authSourceDAO;
  
  @Inject
  private UserPictureDAO userPictureDAO;

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
