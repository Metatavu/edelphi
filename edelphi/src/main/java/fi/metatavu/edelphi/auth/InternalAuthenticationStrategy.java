package fi.metatavu.edelphi.auth;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserActivationDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserPasswordDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.RequestUtils;

/**
 * Strategy that uses email and password for user authentication
 * 
 * @author Antti Leppä
 */
public class InternalAuthenticationStrategy extends AbstractAuthenticationStrategy implements AuthenticationProvider {

  public static final String STRATEGY_NAME = "eDelfoi";

  @Override
  public String getName() {
    return STRATEGY_NAME;
  }

  @Override
  public boolean requiresCredentials() {
    return true;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();

    String username = StringUtils.lowerCase(requestContext.getString("username"));
    String password = RequestUtils.md5EncodeString(requestContext.getString("password"));

    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();

    UserEmail userEmail = userEmailDAO.findByAddress(username);

    if (userEmail != null) {
      User user = userEmail.getUser();
      UserPassword userPassword = userPasswordDAO.findByUser(user);
      UserActivationDAO userActivationDAO = new UserActivationDAO();
      UserActivation userActivation = userActivationDAO.findByUser(user);

      if (userActivation != null) {
        String errorLink = messages.getText(locale, "exception.1039.accountNotYetActivated.link");
        errorLink = "<a href=\"" + RequestUtils.getBaseUrl(requestContext.getRequest())
            + "/resendactivation.page?email=" + username + "\">" + errorLink + "</a>";
        String errorTemplate = messages.getText(locale, "exception.1039.accountNotYetActivated.template",
            new String[] { errorLink });
        throw new SmvcRuntimeException(EdelfoiStatusCode.ACCOUNT_NOT_YET_ACTIVATED, errorTemplate);
      }

      if (userPassword != null && password.equals(userPassword.getPasswordHash())) {
        RequestUtils.loginUser(requestContext, user);
        return AuthenticationResult.LOGIN;
      } else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN,
            messages.getText(locale, "exception.1007.invalidLogin"));
      }
    } else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN,
          messages.getText(locale, "exception.1007.invalidLogin"));
    }
  }

  @Override
  public String[] getKeys() {
    // Internal login needs no settings
    return new String[0];
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // Internal login needs no settings
    return null;
  }

}
