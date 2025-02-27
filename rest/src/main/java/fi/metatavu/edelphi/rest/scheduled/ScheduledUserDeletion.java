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

    @Schedule (hour = "*", minute = "*", second = "*/1", info = "Every 5 seconds timer")
    public void delete() {
        List<User> users = userController.listUsersToDelete(0, 100000);
        System.out.println("Users to delete: " + users.size());

        userController.listUsersToDelete(0, 5).forEach(userController::deleteUser);
    }

}
