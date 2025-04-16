package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.keycloak.KeycloakController;
import fi.metatavu.edelphi.keycloak.KeycloakException;
import fi.metatavu.edelphi.users.UserController;
import org.slf4j.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledUserDeletion {
  @Inject
  private Logger logger;

  @Inject
  private UserController userController;

  @Inject
  private KeycloakController keycloakController;

  @Inject
  private UserIdentificationDAO userIdentificationDAO;

  @Schedule (hour = "*", minute = "*/5")
  public void delete() {
    if (SchedulerUtils.userDeletionSchedulerActive()) {
      List<User> usersToDelete = userController.listUsersToDelete(30, 1);
      for (User userToDelete: usersToDelete) {
        logger.info("Deleting user: {}, reason: archived and last modified at: {}", userToDelete.getId(), userToDelete.getLastModified());
        userController.deleteUser(userToDelete);
      }
    }
  }

}
