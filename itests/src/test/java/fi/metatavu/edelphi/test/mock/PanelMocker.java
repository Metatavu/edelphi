package fi.metatavu.edelphi.test.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanelMocker extends AbstractMocker {
  
  private static final long PANELIST_ROLE_ID = 6l;
  private static final long SYSTEM_USER_ID = 1l;
  
  private List<Long> panelIds = new ArrayList<>();
  private List<Long> stampIds = new ArrayList<>();
  
  public PanelMocker mock() {
    return this;
  }
  
  public PanelMocker cleanup() {
    for (Long stampId : stampIds) {
      deleteStamp(stampId);
    }
    
    for (Long panelId : panelIds) {
      deletePanel(panelId);
    }
    
    return this;
  }

  public void addCreatedPanel(String name) {
    panelIds.add(findPanelByName(name));
  }
  
  public long findPanelByRootFolder(Long rootFolderId) {
    String sql = 
      "SELECT " + 
      "  id " + 
      "FROM " + 
      "  Panel " +  
      "WHERE " + 
      "  rootFolder_id = ?";
    
    return executeSqlLong(sql, rootFolderId);
  }
  
  public long findPanelByName(String name) {
    String sql = 
      "SELECT " + 
      "  id " + 
      "FROM " + 
      "  Panel " +  
      "WHERE " + 
      "  name = ?";
    
    return executeSqlLong(sql, name);
  }

  public long addPanelist(Long panelId, Long stampId, Long userId) {
    long id = getNextId("PanelUser");
    Date now = new Date();
    addPanelUser(id, panelId, userId, PANELIST_ROLE_ID, false, SYSTEM_USER_ID, now, SYSTEM_USER_ID, now, "ADDED", stampId);
    stampIds.add(id);
    return id;
  }
    
  protected void addPanelUser(Long id, Long panelId, Long userId, Long roleId, String joinType, Long stampId) {
    Date now = new Date();
    addPanelUser(id, panelId, userId, roleId, Boolean.FALSE, userId, now, userId, now, joinType, stampId);
  }
  
  private void addPanelUser(Long id, Long panelId, Long userId, Long roleId, Boolean archived, Long creatorId, Date created, Long lastModifierId, Date lastModified, String joinType, Long stampId) {
    String sql = 
      "INSERT INTO " + 
      "  PanelUser (id, panel_id, user_id, role_id, archived, creator_id, created, lastModifier_id, lastModified, joinType, stamp_id) " + 
      "VALUES " + 
      "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    executeSql(sql, id, panelId, userId, roleId, archived, creatorId, created, lastModifierId, lastModified, joinType, stampId);
  }
  
  @SuppressWarnings("unused")
  private void addPanelUserRoleAction(Long id, Long delfoiActionId, Long userRoleId, Long panelId) {
    String userRoleActionSql = 
        "INSERT INTO " + 
        "  UserRoleAction (id, delfoiAction_id, userRole_id) " + 
        "VALUES " + 
        "  (?, ?, ?)";
    executeSql(userRoleActionSql, id, delfoiActionId, userRoleId);
    
    String panelUserRoleActionSql = 
        "INSERT INTO " + 
        "  PanelUserRoleAction (id, panel_id) " + 
        "VALUES " + 
        "  (?, ?)";
    executeSql(panelUserRoleActionSql, id, panelId);
  }

  private void deleteStamp(Long id) {
    executeSql("delete from PanelStamp where id = ?", id);
  }

  private void deletePanel(Long panelId) {
    long folderId = executeSqlLong("SELECT rootFolder_id from Panel WHERE id = ?", panelId);
    executeSql("DELETE FROM PanelUserRoleAction where panel_id = ?", panelId);
    executeSql("DELETE FROM PanelUser WHERE panel_id = ?", panelId);
    executeSql("UPDATE Panel SET currentStamp_id = null, rootFolder_id = null WHERE id = ?", panelId);
    executeSql("DELETE FROM PanelStamp where panel_id = ?", panelId);
    executeSql("DELETE FROM Folder WHERE id = ?", folderId);
    executeSql("DELETE FROM Resource WHERE id = ?", folderId);
    executeSql("DELETE FROM Panel WHERE id = ?", panelId);
  }
}
