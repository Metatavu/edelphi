package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.Defaults;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiUser;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.KeycloakUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class AddUserJSONRequestController extends JSONController {

  public AddUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    UserDAO userDAO = new UserDAO();
    QueryDAO queryDAO = new QueryDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    User creator = userDAO.findById(jsonRequestContext.getLoggedUserId());

    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    Long queryId = jsonRequestContext.getLong("queryId");
    Long userId = jsonRequestContext.getLong("userId");
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String email = StringUtils.lowerCase(jsonRequestContext.getString("email"));
    String password = jsonRequestContext.getString("password");

    User user = userId == null ? null : userDAO.findById(userId);
    Query query = queryId == null ? null : queryDAO.findById(queryId);

    if (user == null) {
      // create a new user with the given name and email address
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      UserEmail userEmail = userEmailDAO.findByAddress(email);
      user = userEmail == null ? null : userEmail.getUser();
      
      if (user == null) {
        user = userDAO.create(firstName, lastName, null, creator, Defaults.NEW_USER_SUBSCRIPTION_LEVEL, null, null, locale.getLanguage());
        userEmail = userEmailDAO.create(user, email);
        userDAO.addUserEmail(user, userEmail, true, creator);
        KeycloakUtils.createUser(user, password, false, true);
      }
      
      DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
      DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(RequestUtils.getDefaults(jsonRequestContext).getDelfoi(), user);
      
      if (delfoiUser == null) {
        Delfoi delfoi = RequestUtils.getDefaults(jsonRequestContext).getDelfoi();
        DelfoiUserRole delfoiUserRole = RequestUtils.getDefaults(jsonRequestContext).getDefaultDelfoiUserRole();
        delfoiUserDAO.create(delfoi, user, delfoiUserRole, creator);
      }
    }
      
    PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
    if (panelUser == null) {
      panelUserDAO.create(panel, user, panel.getDefaultPanelUserRole(), PanelUserJoinType.ADDED, panel.getCurrentStamp(), creator);
    } else {
      jsonRequestContext.addMessage(Severity.WARNING, "USER_EXISTS");
    }
    
    PanelInvitation panelInvitation = panelInvitationDAO.findByPanelAndQueryAndEmail(panel, query, email);
    if (panelInvitation != null) {
      panelInvitationDAO.delete(panelInvitation);
    }
  }

}
