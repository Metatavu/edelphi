package fi.metatavu.edelphi.liquibase.changes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

//
//import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
//import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
//import fi.metatavu.edelphi.dao.base.DelfoiDAO;
//import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
//import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
//import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
//import fi.metatavu.edelphi.dao.users.DelfoiUserRoleDAO;
//import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
//import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
//import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
//import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
//import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
//import fi.metatavu.edelphi.domainmodel.users.UserRole;
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
   * @param connection database connection
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
  
  private Long createPanelSettingsTemplate(Database database, String name, String description, PanelState state, PanelAccessLevel accessLevel, Long panelistRoleId, Boolean archived) throws CustomChangeException {
    Long id = getNextSequenceId(database, "PanelSettingsTemplate");
    executeInsert(database, "INSERT INTO PanelSettingsTemplate (id, name, description, defaultPanelUserRole_id, accessLevel, state, archived) VALUES (?, ?, ?, ?, ?, ?, ?)", id, name, description, panelistRoleId, accessLevel.name(), state.name(), archived);
    return id;
  }

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

  private void initializeDefaultPermissions(Database database) throws CustomChangeException {
    createDelfoiActionDefaults(database, "CREATE_PANEL", true, true, true, false);
    createDelfoiActionDefaults(database, "MANAGE_BULLETINS", true, true, false, false);
    createDelfoiActionDefaults(database, "MANAGE_USER_PROFILE", true, true, true, false);
    createDelfoiActionDefaults(database, "MANAGE_DELFOI_MATERIALS", true, true, false, false);
    createDelfoiActionDefaults(database, "MANAGE_SYSTEM_SETTINGS", true, false, false, false);
    createDelfoiActionDefaults(database, "MANAGE_DELFOI", true, true, false, false);
  }
  
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
  
  private void createDelfoiAction(Database database, String delfoiActionName, String scope) throws CustomChangeException {
    long  delfoiActionId = getNextSequenceId(database, "DelfoiAction");
    executeInsert(database, "INSERT INTO DelfoiAction (id, actionName, scope) VALUES (?, ?, ?)", delfoiActionId, delfoiActionName, scope);
  }
 
  private void createDelfoiUserRoleAction(Database database, String delfoiActionName, Long roleId) throws CustomChangeException {
    Long delfoiActionId = findDelfoiActionByName(database, delfoiActionName);
    long id = getNextSequenceId(database, "UserRoleAction");
    executeInsert(database, "INSERT INTO UserRoleAction (id, delfoiAction_id, userRole_id) VALUES (?, ?, ?)", id, delfoiActionId, roleId);
    executeInsert(database, "INSERT INTO DelfoiUserRoleAction (id, delfoi_id) VALUES (?, ?)", id, 1l);
    
//    
//    DelfoiUserRole userRole = delfoiUserRoleDAO.findById(roleId);
//    if (userRole == null) {
//      logger.severe(String.format("Could not find user role %d", roleId));
//    } else {
//      createDelfoiAction(delfoiActionName, userRole);
//    }
  }
//  
//  private void createDelfoiAction(String delfoiActionName, UserRole userRole) {
//    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
//    DelfoiDAO delfoiDAO = new DelfoiDAO();
//    Delfoi delfoi = delfoiDAO.findById(1l);
//    DelfoiAction delfoiAction = findDelfoiActionByName(delfoiActionName);
//    
//    delfoiUserRoleActionDAO.create(delfoi, delfoiAction, userRole);
//    
//  }

  private long getNextSequenceId(Database database, String entity) throws CustomChangeException {
    executeDelete(database, "DELETE FROM hibernate_sequences WHERE sequence_name = ?", entity);

    JdbcConnection connection = (JdbcConnection) database.getConnection();
    
    try (PreparedStatement statement = connection.prepareStatement(String.format("SELECT max(id) + 1 FROM %s", entity))) {
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Long id = resultSet.getLong(1);
          executeInsert(database, "INSERT INTO hibernate_sequences (sequence_next_hi_value, sequence_name) VALUES (?, ?)", id, entity);
          return id;
        }
      }
    } catch (Exception e) {
      throw new CustomChangeException(e);
    }

    return 0;
  }

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
  
  private Long findAdministratorRole() {
    return 2l;
  }
  
  private Long findPanelistRole() {
    return 7l;
  }
  
  private Long findPanelManagerRole() {
    return 6l;
  }
  
}
