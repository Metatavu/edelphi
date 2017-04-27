package fi.metatavu.edelphi;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
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
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

@Singleton
public class SubscriptionEndScheduler {

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
        
          Date subscriptionEnd = Date.from(OffsetDateTime.now()
            .toInstant());
          
          List<User> users = userDAO.listByNeSubscriptionLevelAndSubscriptionEndsBefore(SubscriptionLevel.BASIC, subscriptionEnd);
          for (User user : users) {
            SubscriptionLevel oldSubscriptionLevel = user.getSubscriptionLevel();
            userDAO.updateSubscriptionLevel(user, SubscriptionLevel.BASIC);
            userDAO.updateSubscriptionEnds(user, null);
            userDAO.updateSubscriptionStarted(user, null);
            sendNotification(oldSubscriptionLevel, user);
          }
        
        startTimer(SystemUtils.isTestEnvironment() ? 1000 : TIMER_INTERVAL);
      } finally {
        GenericDAO.setEntityManager(null);
      }
    }
  }
  
  private void sendNotification(SubscriptionLevel subscriptionLevel, User user) {
    Messages messages = Messages.getInstance();
    
    Locale locale = LocalizationUtils.resolveSupportedLocale(user.getLocale());
    
    if (subscriptionLevel != null) {  
      String subjectTemplate = messages.getText(locale, "generic.subscriptionEndedMailSubject");
      String contentTemplate = messages.getText(locale, "generic.subscriptionEndedMailContent");
      String subscriptionName = Messages.getInstance().getText(locale, String.format("generic.subscriptionLevels.%s", subscriptionLevel.name()));
      
      Object[] templateParameters = new Object[] { subscriptionName };
  
      String email = user.getDefaultEmailAsString();
      String subject = subjectTemplate;
      String content = MessageFormat.format(contentTemplate, templateParameters);
      
      MailUtils.sendMail(new String[] { email }, null, subject, content, MailUtils.HTML);
    }
  }

}