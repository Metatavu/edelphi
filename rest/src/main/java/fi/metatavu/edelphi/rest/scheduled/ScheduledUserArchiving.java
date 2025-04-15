package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.users.UserController;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class ScheduledUserArchiving {
  @Inject
  private UserController userController;

  @Schedule (hour = "*", minute = "*", second = "*/1", info = "User archiving scheduler. Runs every 60 seconds.")
  public void archive() {
    if (true) {
      System.out.println("Users to archive: " + userController.listUsersToArchive(730, 100000).size());
      userController.listUsersToArchive(730, 100).forEach(userController::archiveUser);
    }
  }

}
