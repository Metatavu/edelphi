package fi.metatavu.edelphi.jsons.locale;

import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;

public class SetLocaleJSONRequestController extends JSONController {

  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = new UserDAO();
    
    if (requestContext.isLoggedIn()) {
      User user = userDAO.findById(requestContext.getLoggedUserId());
      if (user != null) {
        Locale locale = LocaleUtils.toLocale(requestContext.getString("locale"));
        userDAO.updateLocale(user, locale.getLanguage());
      }
    }
  }

}