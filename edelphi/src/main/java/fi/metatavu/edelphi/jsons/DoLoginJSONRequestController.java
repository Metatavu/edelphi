package fi.metatavu.edelphi.jsons;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.auth.AuthenticationProvider;
import fi.metatavu.edelphi.auth.AuthenticationProviderFactory;
import fi.metatavu.edelphi.auth.AuthenticationResult;
import fi.metatavu.edelphi.dao.base.AuthSourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.BulletinUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SessionUtils;

public class DoLoginJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    // Authentication source and its parameters
    Long authSourceId = jsonRequestContext.getLong("authSource");
    if (authSourceId == null) {
      authSourceId = AuthUtils.retrieveAuthSourceId(jsonRequestContext);
    }
    else {
      AuthUtils.storeAuthSourceId(jsonRequestContext, authSourceId);
    }
    AuthSourceDAO authSourceDAO = new AuthSourceDAO();
    AuthSource authSource = authSourceDAO.findById(authSourceId);
    AuthenticationProvider authProvider = AuthenticationProviderFactory.getInstance().createAuthenticationProvider(authSource);
    AuthenticationResult result = authProvider.processLogin(jsonRequestContext);
    if (result != AuthenticationResult.PROCESSING) {
      AuthUtils.addAuthenticationStrategy(jsonRequestContext, authProvider.getName());

      UserDAO userDAO = new UserDAO();
      User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());      
      
      String baseURL = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
      String redirectUrl;
      boolean missingDetails = loggedUser != null && (loggedUser.getFirstName() == null || loggedUser.getLastName() == null || loggedUser.getDefaultEmail() == null);
      SessionUtils.setHasImportantBulletins(jsonRequestContext.getRequest().getSession(false), BulletinUtils.hasUnreadImportantBulletins(loggedUser));
      
      if (result == AuthenticationResult.NEW_ACCOUNT || missingDetails) {
        redirectUrl = baseURL + "/profile.page";
      } else {
        String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(jsonRequestContext);
        redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
      }
      
      jsonRequestContext.setRedirectURL(redirectUrl);
    }
  }
  
}
