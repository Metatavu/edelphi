package fi.metatavu.edelphi.jsons;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;

import java.util.Locale;

import fi.metatavu.edelphi.Defaults;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ProcessNewEmailJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    Boolean linkEmail = jsonRequestContext.getBoolean("link");
    String email = jsonRequestContext.getString("email");
    if (linkEmail) {
      if (email != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        UserEmail userEmail = userEmailDAO.findByAddress(email);
        if (userEmail == null) {
          User user = RequestUtils.getUser(jsonRequestContext);
          if (user != null) {
            UserDAO userDAO = new UserDAO();
            userEmail = userEmailDAO.create(user, email);
            userDAO.addUserEmail(user, userEmail, user.getDefaultEmail() == null, user);
          }
        }
      }
    }
    else {
      UserDAO userDAO = new UserDAO();
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
      UserEmail userEmail = userEmailDAO.findByAddress(email);
      if (userEmail == null) {
        User user = userDAO.create(null, null, null, null, Defaults.NEW_USER_SUBSCRIPTION_LEVEL, null, null, locale.getLanguage());
        userEmail = userEmailDAO.create(user, email);
        userDAO.addUserEmail(user, userEmail, true, user);
        Delfoi delfoi = RequestUtils.getDefaults(jsonRequestContext).getDelfoi();
        DelfoiUserRole delfoiUserRole = RequestUtils.getDefaults(jsonRequestContext).getDefaultDelfoiUserRole();
        delfoiUserDAO.create(delfoi, user, delfoiUserRole, user);
        RequestUtils.loginUser(jsonRequestContext, user, null);
      }
    }
    String baseURL = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
    String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(jsonRequestContext);
    String redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
    jsonRequestContext.setRedirectURL(redirectUrl);
  }
  
}
