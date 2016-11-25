package fi.metatavu.edelphi.pages;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcMessage;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserActivationDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ResendActivationPageRequestController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    String email = StringUtils.lowerCase(pageRequestContext.getString("email"));
    UserEmail userEmail = email == null ? null : userEmailDAO.findByAddress(email);
    if (userEmail == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByUser(userEmail.getUser());
    if (userActivation == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    String mailSubject = messages.getText(locale, "userRegistration.mailSubject");
    String verificationLink = RequestUtils.getBaseUrl(pageRequestContext.getRequest()) + "/activateaccount.page?email=" + userActivation.getEmail() + "&hash=" + userActivation.getHash();
    String mailContent = messages.getText(locale, "userRegistration.mailTemplate", new String [] { email, verificationLink });
    String infoMessage = messages.getText(locale, "userRegistration.infoMessage", new String [] { email });
    MailUtils.sendMail(email, mailSubject, mailContent);
    
    RequestUtils.storeRedirectMessage(pageRequestContext, new SmvcMessage(Severity.INFORMATION, infoMessage));
    String baseUrl = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
    String redirectUrl = baseUrl + "/index.page";
    pageRequestContext.setRedirectURL(redirectUrl);
  }
  
}
