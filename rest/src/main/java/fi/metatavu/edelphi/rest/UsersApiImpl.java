package fi.metatavu.edelphi.rest;

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.rest.api.UsersApi;
import fi.metatavu.edelphi.rest.translate.UserTranslator;
import fi.metatavu.edelphi.users.UserController;

/**
 * Users API implementation
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
@SecurityDomain("keycloak")
public class UsersApiImpl extends AbstractApi implements UsersApi {
  
  @Inject
  private UserController userController;

  @Inject
  private UserTranslator userTranslator;

  @Override
  @RolesAllowed("user")
  public Response findUser(UUID userId) {
    UUID loggedUserId = getLoggedUserId();
    if (!loggedUserId.equals(userId)) {
      return createForbidden("You do not have permission to see this user");
    }
    
    User user = userController.findUserByKeycloakId(userId);
    if (user == null) {
      return createNotFound();
    }
    
    return createOk(userTranslator.translate(user));
  }

}
