package fi.metatavu.edelphi.jsons.admin;

import java.util.Date;
import java.util.Locale;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreateDelfoiBulletinJSONRequestController extends JSONController {

  public CreateDelfoiBulletinJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    UserDAO userDAO = new UserDAO();

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    String title = jsonRequestContext.getString("title");
    String message = jsonRequestContext.getString("message");
    Boolean important = jsonRequestContext.getBoolean("important");
    Date importantEnds = jsonRequestContext.getDate("importantEnds");

    DelfoiBulletin bulletin = bulletinDAO.create(delfoi, title, message, loggedUser, important, importantEnds);
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "admin.managePanelBulletins.bulletinCreated"));
    jsonRequestContext.addResponseParameter("bulletinId", bulletin.getId());
  }

}
