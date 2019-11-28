package fi.metatavu.edelphi.liquibase.changes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;

/**
 * Creates default actions when running in functional test environment
 * 
 * @author Antti Leppä
 */
public class TestDefaultActions extends AbstractCustomChange {
  
  @Override
  public void execute(Database database) throws CustomChangeException {
    initializeDelfoiActions(database);
    initializeDefaultPermissions(database);
    initializePanelSettingTemplates(database);  
  }
  
  private void initializeDelfoiActions(Database database) throws CustomChangeException {
    createDelfoiAction(database, "CREATE_PANEL", "DELFOI");
    createDelfoiAction(database, "MANAGE_SYSTEM_SETTINGS", "DELFOI");
    createDelfoiAction(database, "MANAGE_USER_PROFILE", "DELFOI");
    createDelfoiAction(database, "MANAGE_DELFOI", "DELFOI");
    createDelfoiAction(database, "MANAGE_DELFOI_MATERIALS", "DELFOI");
    createDelfoiAction(database, "MANAGE_BULLETINS", "DELFOI");
    createDelfoiAction(database, "MANAGE_SUBSCRIPTION_LEVELS", "DELFOI");
    createDelfoiAction(database, "MANAGE_USER_SUBSCRIPTIONS", "DELFOI");
    createDelfoiAction(database, "MANAGE_USERS", "DELFOI");
    createDelfoiAction(database, "MANAGE_PLANS", "DELFOI");

    createDelfoiAction(database, "ACCESS_PANEL", "PANEL");
    createDelfoiAction(database, "ACCESS_PANEL_REPORTS", "PANEL");
    createDelfoiAction(database, "ACCESS_QUERY_COMMENTS", "PANEL");
    createDelfoiAction(database, "CREATE_QUERY_COMMENTS", "PANEL");
    createDelfoiAction(database, "CREATE_QUERY_RESPONSE", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL_INVITATIONS", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL_MATERIALS", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL_SYSTEM_SETTINGS", "PANEL");
    createDelfoiAction(database, "MANAGE_QUERY_COMMENTS", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL_USERS", "PANEL");
    createDelfoiAction(database, "MANAGE_PANEL_BULLETINS", "PANEL");
    createDelfoiAction(database, "CLEAR_QUERY_DATA", "PANEL");
    createDelfoiAction(database, "MANAGE_QUERY_RESULTS", "PANEL");
  }

  /**
   * Initializes panel setting templates
   * 
   * @param connection database connection
   * @throws CustomChangeException thrown when execution fails
   */
  private void initializePanelSettingTemplates(Database database) throws CustomChangeException {
    createPanelSettingsTemplate(database, "traditional", "Traditional Delphi panel");
  }

  /**
   * Creates panel settings template
   * 
   * @param database database connection
   * @param name name
   * @param description description
   * @throws CustomChangeException thrown when execution fails
   */
  private void createPanelSettingsTemplate(Database database, String name, String description) throws CustomChangeException {
    Long panelSettingsTemplateId = createPanelSettingsTemplate(database, name, description, PanelState.DESIGN, PanelAccessLevel.CLOSED, findPanelistRole(), Boolean.FALSE);
    
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "ACCESS_PANEL", true, true, true);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "ACCESS_PANEL_REPORTS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "ACCESS_QUERY_COMMENTS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "CREATE_QUERY_COMMENTS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "CREATE_QUERY_RESPONSE", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL_INVITATIONS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL_MATERIALS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL_SYSTEM_SETTINGS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_QUERY_COMMENTS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL_USERS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_PANEL_BULLETINS", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "CLEAR_QUERY_DATA", true, true, false);
    createPanelSettingsTemplateRole(database, panelSettingsTemplateId, "MANAGE_QUERY_RESULTS", true, true, false);
     
    createDelfoiActionDefaults(database, "CREATE_PANEL", true, true, true, false);
  } 
  
  /**
   * Creates panel settings template
   * 
   * @param database database connection
   * @param name name
   * @param description description
   * @param state state
   * @param accessLevel access level
   * @param panelistRoleId panelistRoleId
   * @param archived archived
   * @return id
   * @throws CustomChangeException thrown when execution fails
   */
  private Long createPanelSettingsTemplate(Database database, String name, String description, PanelState state, PanelAccessLevel accessLevel, Long panelistRoleId, Boolean archived) throws CustomChangeException {
    Long id = getNextSequenceId(database, "PanelSettingsTemplate");
    executeInsert(database, "INSERT INTO PanelSettingsTemplate (id, name, description, defaultPanelUserRole_id, accessLevel, state, archived) VALUES (?, ?, ?, ?, ?, ?, ?)", id, name, description, panelistRoleId, accessLevel.name(), state.name(), archived);
    return id;
  }

  /**
   * Creates a panel settings template role
   * 
   * @param database
   * @param panelSettingsTemplateId
   * @param delfoiActionName
   * @param administrators
   * @param panelManagers
   * @param panelists
   * @throws CustomChangeException
   */
  private void createPanelSettingsTemplateRole(Database database, Long panelSettingsTemplateId, String delfoiActionName, boolean administrators, boolean panelManagers, boolean panelists) throws CustomChangeException {
    Long delfoiActionId = findDelfoiActionByName(database, delfoiActionName);
    
    if (administrators) {
      executeInsert(database, "INSERT INTO PanelSettingsTemplateRole (id, panelSettingsTemplate_id, delfoiAction_id, userRole_id) VALUES (?, ?, ?, ?)", getNextSequenceId(database, "PanelSettingsTemplateRole"), panelSettingsTemplateId, delfoiActionId, findAdministratorRole());
    }
    
    if (panelManagers) {
      executeInsert(database, "INSERT INTO PanelSettingsTemplateRole (id, panelSettingsTemplate_id, delfoiAction_id, userRole_id) VALUES (?, ?, ?, ?)", getNextSequenceId(database, "PanelSettingsTemplateRole"), panelSettingsTemplateId, delfoiActionId, findPanelManagerRole());
    }
    
    if (panelists) {
      executeInsert(database, "INSERT INTO PanelSettingsTemplateRole (id, panelSettingsTemplate_id, delfoiAction_id, userRole_id) VALUES (?, ?, ?, ?)", getNextSequenceId(database, "PanelSettingsTemplateRole"), panelSettingsTemplateId, delfoiActionId, findPanelistRole());
    }
  }

  /**
   * Initializes default permissions
   * 
   * @param database
   * @throws CustomChangeException
   */
  private void initializeDefaultPermissions(Database database) throws CustomChangeException {
    createDelfoiActionDefaults(database, "CREATE_PANEL", true, true, true, false);
    createDelfoiActionDefaults(database, "MANAGE_BULLETINS", true, true, false, false);
    createDelfoiActionDefaults(database, "MANAGE_USER_PROFILE", true, true, true, false);
    createDelfoiActionDefaults(database, "MANAGE_DELFOI_MATERIALS", true, true, false, false);
    createDelfoiActionDefaults(database, "MANAGE_SYSTEM_SETTINGS", true, false, false, false);
    createDelfoiActionDefaults(database, "MANAGE_DELFOI", true, true, false, false);
  }
  
  /**
   * Creates delfoi action defaults
   * 
   * @param database
   * @param delfoiActionName
   * @param administrators
   * @param managers
   * @param users
   * @param guents
   * @throws CustomChangeException
   */
  private void createDelfoiActionDefaults(Database database, String delfoiActionName, boolean administrators, boolean managers, boolean users, boolean guents) throws CustomChangeException {
    Long administratorsRoleId = 2l;
    Long managersId = 3l;
    Long usersId = 4l;
    Long guestsId = 5l;
    
    if (administrators) {
      createDelfoiUserRoleAction(database, delfoiActionName, administratorsRoleId);
    }

    if (managers) {
      createDelfoiUserRoleAction(database, delfoiActionName, managersId);
    }

    if (users) {
      createDelfoiUserRoleAction(database, delfoiActionName, usersId);
    }
    
    if (guents) {
      createDelfoiUserRoleAction(database, delfoiActionName, guestsId);
    }
  }
  
  /**
   * Creates delfoi action
   * 
   * @param database
   * @param delfoiActionName
   * @param scope
   * @throws CustomChangeException
   */
  private void createDelfoiAction(Database database, String delfoiActionName, String scope) throws CustomChangeException {
    long  delfoiActionId = getNextSequenceId(database, "DelfoiAction");
    executeInsert(database, "INSERT INTO DelfoiAction (id, actionName, scope) VALUES (?, ?, ?)", delfoiActionId, delfoiActionName, scope);
  }
 
  /**
   * Creates delfoi user role action
   * 
   * @param database
   * @param delfoiActionName
   * @param roleId
   * @throws CustomChangeException
   */
  private void createDelfoiUserRoleAction(Database database, String delfoiActionName, Long roleId) throws CustomChangeException {
    Long delfoiActionId = findDelfoiActionByName(database, delfoiActionName);
    long id = executeInsert(database, "INSERT INTO UserRoleAction (delfoiAction_id, userRole_id) VALUES (?, ?)", delfoiActionId, roleId);
    executeInsert(database, "INSERT INTO DelfoiUserRoleAction (id, delfoi_id) VALUES (?, ?)", id, 1l);
  }

  /**
   * Finds a delfoi action by name
   * 
   * @param database
   * @param delfoiActionName
   * @return
   * @throws CustomChangeException
   */
  private Long findDelfoiActionByName(Database database, String delfoiActionName) throws CustomChangeException {
    JdbcConnection connection = (JdbcConnection) database.getConnection();
    
    try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM DelfoiAction WHERE actionName = ?")) {
      statement.setString(1, delfoiActionName);
      
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getLong(1);
        }
      }
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }

    return null;
  }
  
  /**
   * Returns administrator role id
   * 
   * @return administrator role id
   */
  private Long findAdministratorRole() {
    return 2l;
  }
  
  /**
   * Returns panelist role id
   * 
   * @return panelist role id
   */
  private Long findPanelistRole() {
    return 7l;
  }
  
  /**
   * Returns manager role id
   * 
   * @return manager role id
   */
  private Long findPanelManagerRole() {
    return 6l;
  }
  
}
