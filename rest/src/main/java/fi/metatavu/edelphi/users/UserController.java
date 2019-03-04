package fi.metatavu.edelphi.users;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;

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
  
}
