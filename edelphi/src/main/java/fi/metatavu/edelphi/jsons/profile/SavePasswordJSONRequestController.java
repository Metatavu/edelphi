package fi.metatavu.edelphi.jsons.profile;

import java.util.Locale;

import fi.metatavu.edelphi.smvc.AccessDeniedException;
import fi.metatavu.edelphi.smvc.Severity;
import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserPasswordDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class SavePasswordJSONRequestController extends JSONController {

  public SavePasswordJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    
    User user = userDAO.findById(jsonRequestContext.getLong("userId"));
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    UserPassword userPassword = userPasswordDAO.findByUser(user);

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    // Verify that the logged user is the same as the user being edited.
    if (!loggedUser.getId().equals(user.getId()))
      throw new AccessDeniedException(jsonRequestContext.getRequest().getLocale());

    if (userPassword != null) {
      String oldPasswordHash = jsonRequestContext.getString("oldPassword");
      
      if ((oldPasswordHash == null) || (!oldPasswordHash.equals(userPassword.getPasswordHash()))) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
      }
    }
    
    String passwordHash = jsonRequestContext.getString("newPassword");

    if (passwordHash != null) {
      if (userPassword != null) {
        // Update user's password
        userPasswordDAO.updatePasswordHash(userPassword, passwordHash);
      } else {
        // Create password for user
        userPasswordDAO.create(user, passwordHash);
      }
    } else {
      if (userPassword != null)
        userPasswordDAO.delete(userPassword);
    }

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "profile.block.passwordUpdatedMessage"));
  }

}
