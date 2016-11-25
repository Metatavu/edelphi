package fi.metatavu.edelphi;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.utils.SystemUtils;

@Singleton
@Startup
@DependsOn (value = "ActionsInitializer")
public class TestDefaultActionsInitializer {
  
  private static final Logger logger = Logger.getLogger(TestDefaultActionsInitializer.class.getName());

  @PersistenceContext
  private EntityManager entityManager;

  @PostConstruct
  public void init() {
    GenericDAO.setEntityManager(entityManager);
    try {
      if (SystemUtils.isTestEnvironment()) {
        initializeDefaultPermissions();
        initializePanelSettingTemplates();
      }
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  private void initializePanelSettingTemplates() {
    createPanelSettingsTemplate("traditional", "Traditional Delphi panel");
  }

  private void createPanelSettingsTemplate(String name, String description) {
    PanelSettingsTemplateDAO panelSettingsTemplateDAO = new PanelSettingsTemplateDAO();
    PanelSettingsTemplate panelSettingsTemplate = panelSettingsTemplateDAO.create(name, description, PanelState.DESIGN, PanelAccessLevel.CLOSED, findPanelistRole(), Boolean.FALSE);
    
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.ACCESS_PANEL, true, true, true);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.ACCESS_PANEL_REPORTS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.ACCESS_QUERY_COMMENTS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.CREATE_QUERY_COMMENTS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.CREATE_QUERY_RESPONSE, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL_INVITATIONS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL_MATERIALS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL_SYSTEM_SETTINGS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_QUERY_COMMENTS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL_USERS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_PANEL_BULLETINS, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.CLEAR_QUERY_DATA, true, true, false);
    createPanelSettingsTemplateRole(panelSettingsTemplate, DelfoiActionName.MANAGE_QUERY_RESULTS, true, true, false);
     
    createDelfoiActionDefaults(DelfoiActionName.CREATE_PANEL, true, true, true, false);
  }
  
  private void createPanelSettingsTemplateRole(PanelSettingsTemplate panelSettingsTemplate, DelfoiActionName delfoiActionName, boolean administrators, boolean panelManagers, boolean panelists) {
    PanelSettingsTemplateRoleDAO panelSettingsTemplateRoleDAO = new PanelSettingsTemplateRoleDAO();
    
    DelfoiAction delfoiAction = findDelfoiActionByName(delfoiActionName);
    if (delfoiAction == null) {
      logger.severe(String.format("Could not find delfoi action %s", delfoiActionName.name()));
      return;
    }
    
    if (administrators) {
      panelSettingsTemplateRoleDAO.create(panelSettingsTemplate, delfoiAction, findAdministratorRole());
    }
    
    if (panelManagers) {
      panelSettingsTemplateRoleDAO.create(panelSettingsTemplate, delfoiAction, findPanelManagerRole());
    }
    
    if (panelists) {
      panelSettingsTemplateRoleDAO.create(panelSettingsTemplate, delfoiAction, findPanelistRole());
    }
  }

  private void initializeDefaultPermissions() {
    createDelfoiActionDefaults(DelfoiActionName.CREATE_PANEL, true, true, true, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_BULLETINS, true, true, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_USER_PROFILE, true, true, true, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_DELFOI_MATERIALS, true, true, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, true, false, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_DELFOI, true, true, false, false);
  }
  
  private void createDelfoiActionDefaults(DelfoiActionName delfoiActionName, boolean administrators, boolean managers, boolean users, boolean guents) {
    Long administratorsRoleId = 2l;
    Long managersId = 3l;
    Long usersId = 4l;
    Long guestsId = 5l;
    
    if (administrators) {
      createDelfoiAction(delfoiActionName, administratorsRoleId);
    }

    if (managers) {
      createDelfoiAction(delfoiActionName, managersId);
    }

    if (users) {
      createDelfoiAction(delfoiActionName, usersId);
    }
    
    if (guents) {
      createDelfoiAction(delfoiActionName, guestsId);
    }
  }
  
  private void createDelfoiAction(DelfoiActionName delfoiActionName, Long roleId) {
    DelfoiUserRoleDAO delfoiUserRoleDAO = new DelfoiUserRoleDAO();
    
    DelfoiUserRole userRole = delfoiUserRoleDAO.findById(roleId);
    if (userRole == null) {
      logger.severe(String.format("Could not find user role %d", roleId));
    } else {
      createDelfoiAction(delfoiActionName, userRole);
    }
  }
  
  private void createDelfoiAction(DelfoiActionName delfoiActionName, UserRole userRole) {
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    DelfoiDAO delfoiDAO = new DelfoiDAO();
    Delfoi delfoi = delfoiDAO.findById(1l);
    DelfoiAction delfoiAction = findDelfoiActionByName(delfoiActionName);
    
    delfoiUserRoleActionDAO.create(delfoi, delfoiAction, userRole);
    
  }

  private DelfoiAction findDelfoiActionByName(DelfoiActionName delfoiActionName) {
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    return delfoiActionDAO.findByActionName(delfoiActionName.name());
  }
  
  private UserRole findAdministratorRole() {
    return findUserRoleById(2l);
  }
  
  private PanelUserRole findPanelistRole() {
    return (PanelUserRole) findUserRoleById(7l);
  }
  
  private PanelUserRole findPanelManagerRole() {
    return (PanelUserRole) findUserRoleById(6l);
  }
  
  private UserRole findUserRoleById(Long id) {
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();
    return panelUserRoleDAO.findById(id);
  }
  
}
