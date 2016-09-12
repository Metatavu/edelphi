package fi.metatavu.edelphi.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvc.controllers.RequestContext;
import fi.metatavu.edelphi.smvc.logging.Logging;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiUserRoleAction;
import fi.metatavu.edelphi.domainmodel.actions.PanelUserRoleAction;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.users.SuperUser;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserRole;

public class ActionUtils {
 
  public static boolean isSuperUser(RequestContext requestContext) {
    User user = RequestUtils.getUser(requestContext);
    
    return user instanceof SuperUser;
  }
  
  public static boolean hasDelfoiAccess(RequestContext requestContext, String actionName) {
    if (isSuperUser(requestContext))
      return true;
    
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO(); 
    DelfoiAction action = delfoiActionDAO.findByActionName(actionName);
    if (action == null) {
      Logging.logInfo("ActionUtils.hasDelfoiAccess - undefined action: '" + actionName + "'");
      return false;
    }
    UserRole userRole = RequestUtils.getUserRole(requestContext, action.getScope());
    return hasDelfoiAccess(requestContext, action, userRole);
  }

  public static boolean hasPanelAccess(RequestContext requestContext, String actionName) {
    if (isSuperUser(requestContext))
      return true;

    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO(); 
    UserRole userRole = RequestUtils.getUserRole(requestContext, DelfoiActionScope.PANEL);
    DelfoiAction action = delfoiActionDAO.findByActionName(actionName);
    
    if (action == null) {
      Logging.logInfo("ActionUtils.hasDelfoiAccess - undefined action: '" + actionName + "'");
      return false;
    }
    
    return hasPanelAccess(requestContext, action, userRole);
  }

  public static void includeRoleAccessList(RequestContext requestContext) {
    if (!isSuperUser(requestContext)) {
      // Delfoi Actions
      Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
      DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
      UserRole userRole = RequestUtils.getUserRole(requestContext, DelfoiActionScope.DELFOI);
      
      List<DelfoiUserRoleAction> delfoiUserRoleActions = delfoiUserRoleActionDAO.listByDelfoiAndUserRole(delfoi, userRole);
      Map<String, Boolean> delfoiUserRoleActionMap = new HashMap<String, Boolean>();
      
      for (DelfoiUserRoleAction action : delfoiUserRoleActions) {
        delfoiUserRoleActionMap.put(action.getDelfoiAction().getActionName(), Boolean.TRUE);
      }
      
      requestContext.getRequest().setAttribute("actions", delfoiUserRoleActionMap);
      
      // Panel Actions    
      Panel panel = RequestUtils.getPanel(requestContext);
      
      if (panel != null) {
        PanelUserRoleActionDAO panelUserRoleActionDAO = new PanelUserRoleActionDAO();
        UserRole panelUserRole = RequestUtils.getUserRole(requestContext, DelfoiActionScope.PANEL);
  
        List<PanelUserRoleAction> panelUserRoleActions = panelUserRoleActionDAO.listByPanelAndUserRole(panel, panelUserRole);
        Map<String, Boolean> panelUserRoleActionMap = new HashMap<String, Boolean>();
        
        for (PanelUserRoleAction action : panelUserRoleActions) {
          panelUserRoleActionMap.put(action.getDelfoiAction().getActionName(), Boolean.TRUE);
        }
        
        requestContext.getRequest().setAttribute("panelActions", panelUserRoleActionMap);
      }
    } else {
      // All Actions for SuperUsers
      
      DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
      List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.DELFOI);
      Map<String, Boolean> delfoiUserRoleActionMap = new HashMap<String, Boolean>();
      
      for (DelfoiAction action : actionList) {
        delfoiUserRoleActionMap.put(action.getActionName(), Boolean.TRUE);
      }
      requestContext.getRequest().setAttribute("actions", delfoiUserRoleActionMap);

      // Panel
      actionList = delfoiActionDAO.listByScope(DelfoiActionScope.PANEL);
      delfoiUserRoleActionMap = new HashMap<String, Boolean>();
      
      for (DelfoiAction action : actionList) {
        delfoiUserRoleActionMap.put(action.getActionName(), Boolean.TRUE);
      }
      requestContext.getRequest().setAttribute("panelActions", delfoiUserRoleActionMap);
    }
  }

  private static boolean hasDelfoiAccess(RequestContext requestContext, DelfoiAction action, UserRole userRole) {
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(requestContext);

    return delfoiUserRoleActionDAO.hasDelfoiActionAccess(delfoi, userRole, action);
  }

  private static boolean hasPanelAccess(RequestContext requestContext, DelfoiAction action, UserRole userRole) {
    PanelUserRoleActionDAO panelUserRoleActionDAO = new PanelUserRoleActionDAO();
    Panel panel = RequestUtils.getPanel(requestContext);
    
    return panelUserRoleActionDAO.hasPanelActionAccess(panel, userRole, action);
  }
}
