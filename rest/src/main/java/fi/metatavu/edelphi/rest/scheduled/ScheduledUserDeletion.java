package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.panels.PanelController;
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

  @Schedule (hour = "*", minute = "*", second = "*/60", info = "User deletion scheduler. Runs every 60 seconds.")
  public void delete() {
    userController.listUsersToDelete(0, 1).forEach(userController::deleteUser);
  }

}
