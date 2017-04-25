package fi.metatavu.edelphi;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;

import org.apache.commons.lang3.LocaleUtils;

import com.bertoncelj.wildflysingletonservice.Start;
import com.bertoncelj.wildflysingletonservice.Stop;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.resources.GoogleDocumentDAO;
import fi.metatavu.edelphi.dao.resources.GoogleImageDAO;
import fi.metatavu.edelphi.dao.users.NotificationDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserNotificationDAO;
import fi.metatavu.edelphi.domainmodel.resources.GoogleDocument;
import fi.metatavu.edelphi.domainmodel.resources.GoogleImage;
import fi.metatavu.edelphi.domainmodel.users.Notification;
import fi.metatavu.edelphi.domainmodel.users.NotificationType;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserNotification;
import fi.metatavu.edelphi.drive.DriveImageCache;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.UserUtils;

@Singleton
public class NotificationMailScheduler {

  private static final int TIMER_INTERVAL = 1000;//1000 * 60 * 10;

  @PersistenceContext
  private EntityManager entityManager;
  
  @Resource
  private TimerService timerService;
  
  private boolean stopped;
  
  @Start
  public void start() {
    stopped = false;
    startTimer(TIMER_INTERVAL);
  }
  
  @Stop
  public void stop() {
    stopped = true;
  }
  
  private void startTimer(int duration) {
    stopped = false;
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    timerService.createSingleActionTimer(duration, timerConfig);
  }
  
  @Timeout
  public void timeout(Timer timer) {
    if (!stopped) {
      GenericDAO.setEntityManager(entityManager);
      try {
        NotificationDAO notificationDAO = new NotificationDAO();
        UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
        UserDAO userDAO = new UserDAO();
        
        for (Notification notification : notificationDAO.listByType(NotificationType.SUBSCRIPTION_END)) {
          Date subscriptionEnd = Date.from(OffsetDateTime.now()
            .plus(notification.getMillisBefore(), ChronoUnit.MILLIS)
            .toInstant());
          
          List<User> users = userDAO.listByNeSubscriptionLevelAndSubscriptionEndsBefore(SubscriptionLevel.BASIC, subscriptionEnd);
          for (User user : users) {
            boolean alreadyNotified = userNotificationDAO.count() > 0;
            if (alreadyNotified) {
              sendNotification(user, notification);
            }
          }
        }
        
        startTimer(TIMER_INTERVAL);
      } finally {
        GenericDAO.setEntityManager(null);
      }
    }
  }
  
  private void sendNotification(User user, Notification notification) {
    // TODO: Locale
    Locale locale = LocaleUtils.toLocale("fi");
    
    String subjectTemplate = LocalizationUtils.getLocalizedText(notification.getSubjectTemplate(), locale);
    String contentTemplate = LocalizationUtils.getLocalizedText(notification.getContentTemplate(), locale);
   
    Object[] templateParameters = new Object[] { user.getFirstName(), user.getLastName(), user.getSubscriptionLevel(), user.getSubscriptionEnds() };

    String email = user.getDefaultEmailAsString();
    String subject = MessageFormat.format(subjectTemplate, templateParameters);
    String content = MessageFormat.format(contentTemplate, templateParameters);

    System.out.println(String.format("%s: %s %s", subject, content, email));
    
    // MailUtils.sendMail(email, mailSubject, mailContent);
  }

}