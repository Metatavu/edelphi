package fi.metatavu.edelphi.permissions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.base.SystemUserRoleDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiUser;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRole;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRoleType;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.users.SuperUser;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserRole;

/**
 * Controller for permissions
 * 
 * @author Antti Leppä
 *
 */
@ApplicationScoped
public class PermissionController {
  
  private static final long DELFOI_ID = 1l;
  
  @Inject
  private Logger logger;
  
  @Inject
  private SystemUserRoleDAO systemUserRoleDAO;
  
  @Inject
  private DelfoiDAO delfoiDAO;

  @Inject
  private DelfoiActionDAO delfoiActionDAO; 

  @Inject
  private DelfoiUserRoleActionDAO delfoiUserRoleActionDAO;
  
  @Inject
  private PanelUserRoleActionDAO panelUserRoleActionDAO;

  @Inject
  private PanelUserDAO panelUserDAO;
  
  /**
   * Returns whether user has required delfoi action access
   * 
   * @param user user
   * @param actionName action
   * @return whether user role required action access
   */
  public boolean hasDelfoiAccess(User user, DelfoiActionName actionName) {
    if (isSuperUser(user)) {
      return true;
    }
    
    DelfoiAction action = delfoiActionDAO.findByActionName(actionName.toString());
    if (action == null) {
      logger.info(String.format("ActionUtils.hasDelfoiAccess - undefined action: '%s'", actionName));
      return false;
    }
    
    UserRole userRole = getDelfoiRole(user);
    return hasDelfoiAccess(action, userRole);
  }

  /**
   * Returns whether user has required panel action access
   * 
   * @param panel panel
   * @param user user
   * @param actionName action
   * @return whether user role required action access
   */
  public boolean hasPanelAccess(Panel panel, User user, DelfoiActionName actionName) {
    if (panel == null) {
      logger.warn("Panel was null when checking panel access");
      return false;
    }

    if (user == null) {
      logger.warn("User was null when checking panel access");
      return false;
    }

    if (isSuperUser(user)) {
      return true;
    }

    UserRole userRole = getPanelRole(user, panel);
    if (userRole == null) {
      return false;
    }

    DelfoiAction action = delfoiActionDAO.findByActionName(actionName.toString());
    
    if (action == null) {
      logger.info(String.format("ActionUtils.hasDelfoiAccess - undefined action: '%s'", actionName));
      return false;
    }

    return hasPanelAccess(panel, action, userRole);
  }

  /**
   * Returns user's panel role
   * 
   * @param user user
   * @param panel panel
   * @return user's panel role
   */
  private UserRole getPanelRole(User user, Panel panel) {
    if (panel != null) {
      PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
      return panelUser == null ? getEveryoneRole() : panelUser.getRole();
    }
   
    return getEveryoneRole();
  }

  /**
   * Returns user's Delfoi role
   * 
   * @param user user
   * @return user's Delfoi role
   */
  private UserRole getDelfoiRole(User user) {
    Delfoi delfoi = getDelfoi();
    if (delfoi != null) {
      DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
      DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(delfoi, user);
      return delfoiUser == null ? getEveryoneRole() : delfoiUser.getRole();
    }
    
    return getEveryoneRole();
  }

  /**
   * Returns whether user is an super user or not
   * 
   * @param user user
   * @return whether user is an super user or not
   */
  private boolean isSuperUser(User user) {
    return user instanceof SuperUser;
  }

  /**
   * Returns everyone role
   * 
   * @return everyone role
   */
  private SystemUserRole getEveryoneRole() {
    return systemUserRoleDAO.findByType(SystemUserRoleType.EVERYONE);
  }

  /**
   * Returns whether user role required action access
   * 
   * @param action action
   * @param userRole role
   * @return whether user role required action access
   */
  private boolean hasDelfoiAccess(DelfoiAction action, UserRole userRole) {
    Delfoi delfoi = getDelfoi();
    return delfoiUserRoleActionDAO.hasDelfoiActionAccess(delfoi, userRole, action);
  }

  /**
   * Returns whether user role required action access
   * 
   * @param panel panel
   * @param action action
   * @param userRole role
   * @return whether user role required action access
   */
  private boolean hasPanelAccess(Panel panel, DelfoiAction action, UserRole userRole) {
    return panelUserRoleActionDAO.hasPanelActionAccess(panel, userRole, action);
  }
  
  /**
   * Returns Delfoi instance
   * 
   * @return Delfoi instance
   */
  private Delfoi getDelfoi() {
    return delfoiDAO.findById(DELFOI_ID);
  }
  
}
