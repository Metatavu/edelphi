package fi.metatavu.edelphi;

import java.util.Locale;

import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;

/**
 * Exception that is thrown when requested feature is not 
 * available on current user's subscription level
 */
public class FeatureNotAvailableOnSubscriptionLevelException extends SmvcRuntimeException {

  private static final long serialVersionUID = -902755199311607327L;

  public FeatureNotAvailableOnSubscriptionLevelException(Locale locale, SubscriptionLevel userSubscriptionLevel, SubscriptionLevel minimumSubscriptionLevel) {
    super(EdelfoiStatusCode.FEATURE_NOT_AVAILABLE_ON_SUBSCRIPTION_LEVEL, createMessage(locale, userSubscriptionLevel, minimumSubscriptionLevel));
  }
  
  private static String createMessage(Locale locale, SubscriptionLevel userSubscriptionLevel, SubscriptionLevel minimumSubscriptionLevel) {
    Messages messages = Messages.getInstance();
    String userLevel = messages.getText(locale, String.format("generic.subscriptionLevels.%s", userSubscriptionLevel));
    String minimumLevel = messages.getText(locale, String.format("generic.subscriptionLevels.%s", minimumSubscriptionLevel));
    return messages.getText(locale, "exception.1044.featureNotAvailableNotSubscriptionLevel", new Object[] {
      userLevel,
      minimumLevel
    });
  }

}
