package fi.metatavu.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.SystemUserRoleDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiUserRoleAction;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRole;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRoleType;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManageActionAccessPageController extends PageController {

  public ManageActionAccessPageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    DelfoiUserRoleDAO userRoleDAO = new DelfoiUserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    SystemUserRoleDAO systemUserRoleDAO = new SystemUserRoleDAO();

    // Populate role list
    List<DelfoiUserRole> delfoiRoleList = userRoleDAO.listAll();
    List<UserRole> roleList = new ArrayList<UserRole>();
    roleList.addAll(delfoiRoleList);
    SystemUserRole systemUserRole = systemUserRoleDAO.findByType(SystemUserRoleType.EVERYONE);
    roleList.add(systemUserRole);
    
    List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.DELFOI);
    List<DelfoiUserRoleAction> roleActionsByDelfoi = delfoiUserRoleActionDAO.listByDelfoi(RequestUtils.getDelfoi(pageRequestContext));
    
    Map<Long, Map<Long, Boolean>> roleMapForActions = new HashMap<Long, Map<Long, Boolean>>();

    for (UserRole role : roleList) {
      roleMapForActions.put(role.getId(), new HashMap<Long, Boolean>());
    }

    for (DelfoiUserRoleAction action : roleActionsByDelfoi) {
      Map<Long, Boolean> actionsEnabledForRole = roleMapForActions.get(action.getUserRole().getId());
      
      actionsEnabledForRole.put(action.getDelfoiAction().getId(), Boolean.TRUE);
    }
    
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (UserRole userRole : roleList) {
      String name = LocalizationUtils.getLocalizedText(userRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(userRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("roleList", userRoleBeans);
    pageRequestContext.getRequest().setAttribute("actionList", actionList);
    pageRequestContext.getRequest().setAttribute("actionStatus", roleMapForActions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/managedelfoiactionaccess.jsp");
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