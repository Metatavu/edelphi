package fi.metatavu.edelphi.auth;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

/**
 * Strategy that is used in automated tests
 * 
 * @author Antti Leppä
 */
public class TestAuthenticationStrategy extends AbstractAuthenticationStrategy implements AuthenticationProvider {

  public static final String STRATEGY_NAME = "test";

  @Override
  public String getName() {
    return STRATEGY_NAME;
  }

  @Override
  public void logout(RequestContext requestContext, String redirectUrl, OAuthAccessToken keycloakToken) {
    // Does not support logout
  }

  @Override
  public boolean requiresCredentials() {
    return true;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    if (!SystemUtils.isTestEnvironment()) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNAUTHORIZED, "Not running in test mode");
    }
    
    UserEmailDAO userEmailDAO = new UserEmailDAO();

    Messages messages = Messages.getInstance();
    Locale locale = requestContext.getRequest().getLocale();

    String email = StringUtils.lowerCase(requestContext.getString("username"));
    UserEmail userEmail = userEmailDAO.findByAddress(email);

    if (userEmail != null) {
      RequestUtils.loginUser(requestContext, userEmail.getUser(), authSource.getId());
      return AuthenticationResult.LOGIN;
    } else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
    }
  }

  @Override
  public String[] getKeys() {
    return new String[0];
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    return null;
  }

}
