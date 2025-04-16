package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.users.UserController;
import org.slf4j.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledUserArchiving {
  @Inject
  private Logger logger;

  @Inject
  private UserController userController;

  @Schedule (hour = "*", minute = "*/1")
  public void archive() {
    if (SchedulerUtils.userArchivingScheduleActive()) {
      List<User> usersToArchive = userController.listUsersToArchive(730, 1);
      for (User userToArchive: usersToArchive) {
        logger.info("Archiving user: {}, reason: archived and last modified at: {}", userToArchive.getId(), userToArchive.getLastModified());
        userController.archiveUser(userToArchive);
      }
    }
  }

}
