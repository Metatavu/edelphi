package fi.metatavu.edelphi.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.Defaults;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.base.AuthSourceSettingDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.AuthSourceSetting;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public abstract class AbstractAuthenticationStrategy implements AuthenticationProvider {
  
  /**
   * Initializes this authentication strategy to work as per the given authentication source.
   * 
   * @param authSource The authentication source
   */
  public void initialize(AuthSource authSource) {
    this.authSource = authSource;
    AuthSourceSettingDAO authSourceSettingDAO = new AuthSourceSettingDAO();
    List<AuthSourceSetting> authSourceSettings = authSourceSettingDAO.listByAuthSource(authSource);
    this.settings = new HashMap<>();
    for (AuthSourceSetting setting : authSourceSettings) {
      settings.put(setting.getKey(), setting.getValue());
    }
  }
  
  protected User registerExternalUser(String firstName, String lastName, String email, Delfoi delfoi, DelfoiUserRole userRole, String externalId, Locale locale) {
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    
    // Creator/modifier of entities is null as they're created by system itself (should it be sys user?)
    User creatorModifier = null;
    
    // Create User
    User user = userDAO.create(firstName, lastName, null, creatorModifier, Defaults.NEW_USER_SUBSCRIPTION_LEVEL, null, null, locale.getLanguage());

    if (email != null) {
      // Create UserEmail
      UserEmail userEmail = userEmailDAO.create(user, email);
      userDAO.addUserEmail(user, userEmail, true, creatorModifier);
    }
    
    // Create DelfoiUser
    delfoiUserDAO.create(delfoi, user, userRole, creatorModifier);
    // Create AuthenticationId
    userIdentificationDAO.create(user, externalId, authSource);
    
    return user;
  }
  
  // TODO If the user has an account activation in progress, should logging in via external sources be denied? 
  
  protected AuthenticationResult processExternalLogin(RequestContext requestContext, String externalId, List<String> emails, String firstName, String lastName) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    Locale locale = requestContext.getRequest().getLocale();
    UserIdentification userIdentification = userIdentificationDAO.findByExternalId(externalId, authSource);
    // Resolve to a common user account based on a variety of possible sources
    User currentUser = RequestUtils.getUser(requestContext);
    User emailUser = resolveUser(requestContext, emails);
    User idUser = userIdentification ==  null ? null : userIdentification.getUser();
    User resolvedUser = resolveUser(requestContext, new User[] {currentUser, emailUser, idUser});
    if (resolvedUser == null) {
      // Entirely new user account
      Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
      DelfoiUserRole userRole = RequestUtils.getDefaults(requestContext).getDefaultDelfoiUserRole();
      String email = emails.size() > 0 ? emails.get(0) : null;
      resolvedUser = registerExternalUser(firstName, lastName, email, delfoi, userRole, externalId, locale);
      for (int i = 1; i < emails.size(); i++) {
        userEmailDAO.create(resolvedUser, emails.get(i));
      }
      RequestUtils.loginUser(requestContext, resolvedUser, authSource.getId());
      return AuthenticationResult.NEW_ACCOUNT;
    }
    else {
      if (idUser == null) {
        // Existing user account but new external identification source 
        userIdentificationDAO.create(resolvedUser, externalId, authSource);
      }
      
      for (String email : emails) {
        UserEmail userEmail = userEmailDAO.findByAddress(email);
        if (userEmail == null) {
          userEmailDAO.create(resolvedUser, email);
        }
      }
      
      RequestUtils.loginUser(requestContext, resolvedUser, authSource.getId());
      
      if (idUser == null) {
        return AuthenticationResult.NEW_ACCOUNT;
      }
      
      return AuthenticationResult.LOGIN;
    }
  }
  
  /**
   * Determines a common <code>User</code> corresponding to the given list of e-mail addresses. If none of the e-mail addresses are in
   * use, returns <code>null</code>. If the e-mail addresses are associated to multiple user accounts, an <code>SmvcRuntimeException</code>
   * is thrown.  
   * 
   * @param requestContext Request context
   * @param emails The list of e-mail addresses to validate
   * 
   * @return The common <code>User</code> corresponding to the given list of e-mail addresses, or <code>null</code> if the addresses are
   * not in use
   * 
   * @throws SmvcRuntimeException If the e-mail addresses are associated to multiple user accounts
   */
  private User resolveUser(RequestContext requestContext, List<String> emails) {
    User user = null;
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    for (String email : emails) {
      UserEmail userEmail = userEmailDAO.findByAddress(email);
      if (userEmail != null) {
        if (user == null) {
          user = userEmail.getUser();
        }
        else if (!user.getId().equals(userEmail.getUser().getId())) {
          Messages messages = Messages.getInstance();
          Locale locale = requestContext.getRequest().getLocale();
          throw new SmvcRuntimeException(EdelfoiStatusCode.LOGIN_MULTIPLE_ACCOUNTS, messages.getText(locale, "exception.1023.loginMultipleAccounts"));
        }
      }
    }
    return user;
  }
  
  /**
   * Determines a common <code>User</code> corresponding to the given array of users. If all users are <code>null</code>, returns <code>null</code>.
   * If the users do not share the same identifier, an <code>SmvcRuntimeException</code> is thrown.  
   * 
   * @param requestContext Request context
   * @param users The list of users to validate
   * 
   * @return The common <code>User</code> corresponding to the given array of users, or <code>null</code> if all given users are <code>null</code>
   * 
   * @throws SmvcRuntimeException If all given users do not share the same identifier 
   */
  private User resolveUser(RequestContext requestContext, User[] users) {
    User resolvedUser = null;
    for (User user : users) {
      if (user != null && resolvedUser != null && !user.getId().equals(resolvedUser.getId())) {
        Messages messages = Messages.getInstance();
        Locale locale = requestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.LOGIN_MULTIPLE_ACCOUNTS, messages.getText(locale, "exception.1023.loginMultipleAccounts"));
      }
      else {
        resolvedUser = user == null ? resolvedUser : user;
      }
    }
    return resolvedUser;
  }
  
  protected AuthSource authSource;
  protected HashMap<String, String> settings;
  
}
