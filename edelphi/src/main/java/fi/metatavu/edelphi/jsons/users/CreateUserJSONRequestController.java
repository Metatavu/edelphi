package fi.metatavu.edelphi.jsons.users;

import java.util.StringTokenizer;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreateUserJSONRequestController extends JSONController {

  public CreateUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO paneluserRoleDAO = new PanelUserRoleDAO();
    
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String nickname = jsonRequestContext.getString("nickname");
    User creator = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    Panel panel = panelDAO.findById(jsonRequestContext.getLong("panelId"));
    User user = userDAO.create(firstName, lastName, nickname, creator, SubscriptionLevel.NONE, null, null);
    
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    DelfoiDefaults delfoiDefaults = RequestUtils.getDefaults(jsonRequestContext);
    delfoiUserDAO.create(delfoi, user, delfoiDefaults.getDefaultDelfoiUserRole(), creator);

    String email = jsonRequestContext.getLowercaseString("email");
    if (email != null) {
      UserEmail userEmail = userEmailDAO.create(user, email);
      userDAO.updateDefaultEmail(user, userEmail, creator);
    }
    
    String roles = jsonRequestContext.getString("roles");
    if (roles != null) {
      StringTokenizer roleTokenizer = new StringTokenizer(roles, ",");
      while (roleTokenizer.hasMoreTokens()) {
        PanelUserRole role = paneluserRoleDAO.findById(new Long(roleTokenizer.nextToken()));
        panelUserDAO.create(panel, user, role, PanelUserJoinType.ADDED, panel.getCurrentStamp(), creator);
      }
    }
  }

}
