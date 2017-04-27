package fi.metatavu.edelphi;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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

import com.bertoncelj.wildflysingletonservice.Start;
import com.bertoncelj.wildflysingletonservice.Stop;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.users.Notification;
import fi.metatavu.edelphi.domainmodel.users.NotificationType;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.users.NotificationUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

@Singleton
public class NotificationMailScheduler {

  private static final int TIMER_INTERVAL = 1000 * 60 * 60 * 4;

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
        UserDAO userDAO = new UserDAO();
        
        for (Notification notification : NotificationUtils.listNotificationsByType(NotificationType.SUBSCRIPTION_END)) {
          Date subscriptionEnd = Date.from(OffsetDateTime.now()
            .plus(notification.getMillisBefore(), ChronoUnit.MILLIS)
            .toInstant());
          
          List<User> users = userDAO.listByNeSubscriptionLevelAndSubscriptionEndsBefore(SubscriptionLevel.BASIC, subscriptionEnd);
          for (User user : users) {
            if (!NotificationUtils.isAlreadyNotified(notification, user)) {
              sendNotification(user, notification);
              NotificationUtils.markNotified(notification, user);
            }
          }
        }

        startTimer(SystemUtils.isTestEnvironment() ? 1000 : TIMER_INTERVAL);
      } finally {
        GenericDAO.setEntityManager(null);
      }
    }
  }
  
  private void sendNotification(User user, Notification notification) {
    Locale locale = LocalizationUtils.resolveSupportedLocale(user.getLocale());
    
    SubscriptionLevel subscriptionLevel = user.getSubscriptionLevel();
    if (subscriptionLevel != null) {
      String subjectTemplate = LocalizationUtils.getLocalizedText(notification.getSubjectTemplate(), locale);
      String contentTemplate = LocalizationUtils.getLocalizedText(notification.getContentTemplate(), locale);
     
      String subscriptionName = Messages.getInstance().getText(locale, String.format("generic.subscriptionLevels.%s", subscriptionLevel.name()));
      long daysRemaining = SubscriptionLevelUtils.getDaysRemaining(user.getSubscriptionEnds());
      
      Object[] templateParameters = new Object[] { subscriptionName, daysRemaining };
  
      String email = user.getDefaultEmailAsString();
      String subject = subjectTemplate;
      String content = MessageFormat.format(contentTemplate, templateParameters);
      
      MailUtils.sendMail(new String[] { email }, null, subject, content, MailUtils.HTML);
    }
  }

}