package fi.metatavu.edelphi.jsons;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.PasswordResetDAO;
import fi.metatavu.edelphi.dao.users.UserActivationDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserPasswordDAO;
import fi.metatavu.edelphi.domainmodel.users.PasswordReset;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;

public class ChangePasswordJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    // E-mail given and exists?
    
    String email = StringUtils.lowerCase(jsonRequestContext.getString("email"));
    if (email == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.PASSWORD_RESET_NO_EMAIL, messages.getText(locale, "exception.1017.passwordResetNoEmail"));
    }
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    if (userEmail == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.PASSWORD_RESET_UNKNOWN_EMAIL, messages.getText(locale, "exception.1018.passwordResetUnknownEmail"));
    }

    // Password reset request exists?
    
    String hash = jsonRequestContext.getString("hash");
    PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    PasswordReset passwordReset = passwordResetDAO.findByEmailAndHash(email, hash);
    if (passwordReset == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_PASSWORD_RESET_REQUEST, messages.getText(locale, "exception.1019.invalidPasswordReset"));
    }
    
    String password = jsonRequestContext.getString("password");
    
    // Create or change password
    
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    UserPassword userPassword = userPasswordDAO.findByUser(userEmail.getUser());
    if (userPassword != null) {
      userPasswordDAO.updatePasswordHash(userPassword, password);
    }
    else {
      userPasswordDAO.create(userEmail.getUser(), password);
    }

    // Delete password reset request
    
    passwordResetDAO.delete(passwordReset);
    
    // Remove possible user account activation as password resets go via e-mail as well
    
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByUser(userEmail.getUser());
    if (userActivation != null) {
      userActivationDAO.delete(userActivation);
    }
    
    // All done
    
    jsonRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.passwordChanged"));
  }
  
}
