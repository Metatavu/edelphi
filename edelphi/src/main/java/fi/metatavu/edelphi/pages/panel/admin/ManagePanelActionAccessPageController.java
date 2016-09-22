package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.users.UserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.actions.PanelUserRoleAction;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManagePanelActionAccessPageController extends PanelPageController {

  public ManagePanelActionAccessPageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_PANEL_SYSTEM_SETTINGS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    UserRoleDAO userRoleDAO = new UserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelUserRoleActionDAO panelUserRoleActionDAO = new PanelUserRoleActionDAO();

    // Populate role list
    List<UserRole> roleList = userRoleDAO.listAll();
    
    List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.PANEL);
    List<PanelUserRoleAction> roleActionsByPanel = panelUserRoleActionDAO.listByPanel(RequestUtils.getPanel(pageRequestContext));
    
    Map<Long, Map<Long, Boolean>> roleMapForActions = new HashMap<Long, Map<Long, Boolean>>();

    for (UserRole role : roleList) {
      roleMapForActions.put(role.getId(), new HashMap<Long, Boolean>());
    }

    for (PanelUserRoleAction action : roleActionsByPanel) {
      Map<Long, Boolean> actionsEnabledForRole = roleMapForActions.get(action.getUserRole().getId());
      
      actionsEnabledForRole.put(action.getDelfoiAction().getId(), Boolean.TRUE);
    }
    
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (UserRole userRole : roleList) {
      String name = LocalizationUtils.getLocalizedText(userRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(userRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("roleList", userRoleBeans);
    pageRequestContext.getRequest().setAttribute("panelId", panel.getId());
    pageRequestContext.getRequest().setAttribute("actionList", actionList);
    pageRequestContext.getRequest().setAttribute("actionStatus", roleMapForActions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managepanelactionaccess.jsp");
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