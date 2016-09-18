package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.List;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.users.UserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.actions.PanelUserRoleAction;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class SavePanelActionAccessJSONRequestController extends JSONController {

  public SavePanelActionAccessJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_SYSTEM_SETTINGS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    UserRoleDAO userRoleDAO = new UserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelUserRoleActionDAO panelUserRoleActionDAO = new PanelUserRoleActionDAO();
    
    List<UserRole> roleList = userRoleDAO.listAll();
    
    List<DelfoiAction> delfoiActions = delfoiActionDAO.listAll();

    for (UserRole role : roleList) {

      // List the old rules that this role has
      List<PanelUserRoleAction> oldRoleActions = panelUserRoleActionDAO.listByPanelAndUserRole(panel, role);
      
      // Go through all the actions in system
      for (DelfoiAction action : delfoiActions) {
        Long long1 = jsonRequestContext.getLong("delfoiActionRole." + role.getId() + "." + action.getId());
        boolean selected = long1 != null ? long1.intValue() == 1 : false;
        PanelUserRoleAction oldAction = null;
        
        // Check if the rule already exists 
        for (PanelUserRoleAction oldRoleAction : oldRoleActions) {
          if (oldRoleAction.getDelfoiAction().getId() == action.getId()) {
            oldAction = oldRoleAction;
            break;
          }
        }
        
        if ((!selected) && (oldAction != null)) {
          // Exists but is not selected -> delete
          panelUserRoleActionDAO.delete(oldAction);
        } else if ((selected) && (oldAction == null)) {
          // Selected but doesn't exists -> create
          panelUserRoleActionDAO.create(panel, action, role);
        }
      }
    }
  }
  
}
