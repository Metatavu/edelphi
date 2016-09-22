package fi.metatavu.edelphi.jsons.admin;

import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.metatavu.edelphi.dao.users.UserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.jsons.JSONController;

public class SavePanelTemplateJSONRequestController extends JSONController {

  public SavePanelTemplateJSONRequestController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserRoleDAO userRoleDAO = new UserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelSettingsTemplateDAO templateDAO = new PanelSettingsTemplateDAO();
    PanelSettingsTemplateRoleDAO templateRoleDAO = new PanelSettingsTemplateRoleDAO();
    
    List<UserRole> roleList = userRoleDAO.listAll();
    
    Long templateId = jsonRequestContext.getLong("templateId");
    PanelSettingsTemplate template = templateDAO.findById(templateId);

    String templateName = jsonRequestContext.getString("templateName");
    String templateDesc = jsonRequestContext.getString("templateDesc");
    
    template = templateDAO.updateTemplateNameAndDescription(template, templateName, templateDesc);
    
    List<DelfoiAction> delfoiActions = delfoiActionDAO.listByScope(DelfoiActionScope.PANEL);

    // List the old rules that this role has
    List<PanelSettingsTemplateRole> oldRoles = templateRoleDAO.listByTemplate(template);

    for (UserRole role : roleList) {
      // Go through all the actions in system
      for (DelfoiAction action : delfoiActions) {
        Long long1 = jsonRequestContext.getLong("delfoiActionRole." + role.getId() + "." + action.getId());
        boolean selected = long1 != null ? long1.intValue() == 1 : false;
        PanelSettingsTemplateRole oldTemplateRole = null;
        
        // Check if the rule already exists 
        for (PanelSettingsTemplateRole oldRole : oldRoles) {
          if ((oldRole.getDelfoiAction().getId().equals(action.getId())) && (oldRole.getUserRole().getId().equals(role.getId()))) {
            oldTemplateRole = oldRole;
            break;
          }
        }
        
        if ((!selected) && (oldTemplateRole != null)) {
          // Exists but is not selected -> delete
          templateRoleDAO.delete(oldTemplateRole);
        } else if ((selected) && (oldTemplateRole == null)) {
          // Selected but doesn't exist -> create
          templateRoleDAO.create(template, action, role);
        }
      }
    }
  }
  
}
