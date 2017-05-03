package fi.metatavu.edelphi.utils;

import java.util.Date;
import java.util.List;

import fi.metatavu.edelphi.dao.users.NotificationDAO;
import fi.metatavu.edelphi.dao.users.UserNotificationDAO;
import fi.metatavu.edelphi.domainmodel.users.Notification;
import fi.metatavu.edelphi.domainmodel.users.NotificationType;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserNotification;

public class NotificationUtils {
  
  private NotificationUtils() {
  }
  
  public static void clearUserNotifications(NotificationType type, User user) {
    UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    
    for (Notification notification : listNotificationsByType(type)) {
      List<UserNotification> userNotifications = userNotificationDAO.listByNotificationAndUser(notification, user);
      for (UserNotification userNotification : userNotifications) {
        userNotificationDAO.delete(userNotification);
      }
    }
  }
  
  public static List<Notification> listNotificationsByType(NotificationType type) {
    NotificationDAO notificationDAO = new NotificationDAO();
    return notificationDAO.listByType(type);
  }
  
  public static boolean isAlreadyNotified(Notification notification, User user) {
    UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    return userNotificationDAO.countByNotificationAndUser(notification, user) > 0;
  }

  public static void markNotified(Notification notification, User user) {
    UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    userNotificationDAO.create(notification, user, new Date());
  }
  
}
