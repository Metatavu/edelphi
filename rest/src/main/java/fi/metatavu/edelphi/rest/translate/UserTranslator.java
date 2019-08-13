package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.rest.model.User;
import fi.metatavu.edelphi.users.UserController;

/**
 * Translator for users
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class UserTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.users.User, fi.metatavu.edelphi.rest.model.User> {
  
  @Inject
  private UserController userController;
  
  @Override
  public fi.metatavu.edelphi.rest.model.User translate(fi.metatavu.edelphi.domainmodel.users.User entity) {
    if (entity == null) {
      return null;
    }

    User result = new User();
    result.setFirstName(entity.getFirstName());
    result.setLastName(entity.getLastName());
    result.setProfileImageUrl(userController.getProfileImagePath(entity));
    result.setId(userController.getUserKeycloakId(entity));
  
    return result;
  }
  
}