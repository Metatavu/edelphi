package fi.metatavu.edelphi.jsons;

import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.PasswordResetDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.users.PasswordReset;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ResetPasswordJSONRequestController extends JSONController {

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

    // Reset request
    
    PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    PasswordReset passwordReset = passwordResetDAO.findByEmail(email);
    if (passwordReset == null) {
      passwordReset = passwordResetDAO.create(email, UUID.randomUUID().toString());
    }
    
    // Reset e-mail
    
    String mailSubject = messages.getText(locale, "passwordReset.mailSubject");
    String resetLink = RequestUtils.getBaseUrl(jsonRequestContext.getRequest()) + "/resetpassword.page?email=" + passwordReset.getEmail() + "&hash=" + passwordReset.getHash();
    String mailContent = messages.getText(locale, "passwordReset.mailTemplate", new String [] { email, resetLink });
    String infoMessage = messages.getText(locale, "passwordReset.infoMessage", new String [] { email });
    MailUtils.sendMail(locale, email, mailSubject, mailContent);
    jsonRequestContext.addMessage(Severity.INFORMATION, infoMessage);
  }
  
}
