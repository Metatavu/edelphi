package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.utils.LocalizationUtils;

public class EditUserPageController extends PanelPageController {

  public EditUserPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelDAO panelDAO = new PanelDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();

    User user = userDAO.findById(pageRequestContext.getLong("userId"));
    if (user == null) {
      pageRequestContext.setIncludeJSP("/jsp/pages/index.jsp");
    } else {
      pageRequestContext.getRequest().setAttribute("user", user);

      List<PanelUserRole> panelUserRoles = panelUserRoleDAO.listAll();
      List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
      for (PanelUserRole panelUserRole : panelUserRoles) {
        String name = LocalizationUtils.getLocalizedText(panelUserRole.getName(), pageRequestContext.getRequest().getLocale());
        userRoleBeans.add(new UserRoleBean(panelUserRole.getId(), name));
      }
      pageRequestContext.getRequest().setAttribute("panelUserRoles", userRoleBeans);

      HashSet<Long> userRoles = new HashSet<Long>();
      Panel panel = panelDAO.findById(pageRequestContext.getLong("panelId"));
      List<PanelUser> panelUsers = panelUserDAO.listByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
      for (PanelUser pUser : panelUsers) {
        userRoles.add(pUser.getRole().getId());
      }
      pageRequestContext.getRequest().setAttribute("userRoles", userRoles);

      pageRequestContext.setIncludeJSP("/jsp/panels/edituser.jsp");
    }

  }

  public class UserRoleBean {
    
    public UserRoleBean(Long id, String name) {
      this.id = id;
      this.name = name;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getName() {
      return name;
    }
    
    private Long id;
    private String name;
  }

}