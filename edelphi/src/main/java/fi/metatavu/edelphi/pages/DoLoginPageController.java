package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.auth.AuthenticationProvider;
import fi.metatavu.edelphi.auth.AuthenticationProviderFactory;
import fi.metatavu.edelphi.auth.AuthenticationResult;
import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.users.UserActivationDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.BulletinUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SessionUtils;

public class DoLoginPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    // Authentication source and its parameters
    Long authSourceId = pageRequestContext.getLong("authSource");
    if (authSourceId == null) {
      authSourceId = AuthUtils.retrieveAuthSourceId(pageRequestContext);
    } else {
      AuthUtils.storeAuthSourceId(pageRequestContext, authSourceId);
    }
    
    String redirectUrlParam = pageRequestContext.getString("redirectUrl");
    if (redirectUrlParam != null) {
      AuthUtils.storeRedirectUrl(pageRequestContext, redirectUrlParam);
    }
    
    if (authSourceId != null) {
      AuthSourceDAO authSourceDAO = new AuthSourceDAO();
      AuthSource authSource = authSourceDAO.findById(authSourceId);
      AuthenticationProvider authProvider = (AuthenticationProvider) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(authSource);
      AuthenticationResult result = authProvider.processLogin(pageRequestContext);
      if (result != AuthenticationResult.PROCESSING) {
        AuthUtils.addAuthenticationStrategy(pageRequestContext, authProvider.getName());
        
        String redirectUrl = null;
        String baseURL = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
        
        UserDAO userDAO = new UserDAO();
        User loggedUser = userDAO.findById(pageRequestContext.getLoggedUserId());   
        
        SessionUtils.setHasImportantBulletins(pageRequestContext.getRequest().getSession(false),BulletinUtils.hasUnreadImportantBulletins(loggedUser));
        if (result == AuthenticationResult.NEW_ACCOUNT || (loggedUser != null && (loggedUser.getFirstName() == null || loggedUser.getLastName() == null || loggedUser.getDefaultEmail() == null))) {
          redirectUrl = baseURL + "/profile.page";
        }
        else {

          // Delete a possible user activation request due to a successful login 
          
          if (loggedUser != null) {
            UserActivationDAO userActivationDAO = new UserActivationDAO();
            UserActivation userActivation = userActivationDAO.findByUser(loggedUser);
            if (userActivation != null) {
              userActivationDAO.delete(userActivation);
            }
          }

          // Redirect to wherever we were going in the first place
          
          String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(pageRequestContext);
          redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
        }
        
        pageRequestContext.setRedirectURL(redirectUrl);
      }
    }
    else {
      pageRequestContext.setRedirectURL(RequestUtils.getBaseUrl(pageRequestContext.getRequest()) + "/index.page");
    }
  }

}
