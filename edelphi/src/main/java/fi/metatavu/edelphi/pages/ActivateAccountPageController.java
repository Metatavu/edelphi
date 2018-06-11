package fi.metatavu.edelphi.pages;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcMessage;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserActivationDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ActivateAccountPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }
  
  @Override
  public void process(PageRequestContext pageRequestContext) {
    String email = StringUtils.lowerCase(pageRequestContext.getString("email"));
    String hash = pageRequestContext.getString("hash");
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByEmailAndHash(email, hash);
    if (userActivation == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    userActivationDAO.delete(userActivation);
    User user = userActivation.getUser();
    RequestUtils.loginUser(pageRequestContext, user, null);

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    SmvcMessage message = new SmvcMessage(Severity.INFORMATION, messages.getText(locale, "index.block.accountActivated"));
    RequestUtils.storeRedirectMessage(pageRequestContext, message);
    
    String baseUrl = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
    String redirectUrl = baseUrl + "/index.page";
    pageRequestContext.setRedirectURL(redirectUrl);
  }

}
