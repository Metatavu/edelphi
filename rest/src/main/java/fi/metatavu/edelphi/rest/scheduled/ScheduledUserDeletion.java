package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.keycloak.KeycloakController;
import fi.metatavu.edelphi.keycloak.KeycloakException;
import fi.metatavu.edelphi.users.UserController;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledUserDeletion {
  @Inject
  private UserController userController;

  @Inject
  private KeycloakController keycloakController;

  @Inject
  private UserIdentificationDAO userIdentificationDAO;

  @Schedule (hour = "*", minute = "*", second = "*/1", info = "User deletion scheduler. Runs every 60 seconds.")
  public void delete() {
    if (true) {
      System.out.println("Users to delete: " + userController.listUsersToDelete(0, 100000).size());
      userController.listUsersToDelete(0, 1).forEach(userController::deleteUser);
    }
  }

}
