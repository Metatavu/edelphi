package fi.metatavu.edelphi.test.mock;

import java.util.Date;

public class AbstractResourceMocker extends AbstractMocker {

  protected void deleteResource(Long resourceId) {
    executeSql("DELETE FROM ResourceLock WHERE resource_id = ?", resourceId);
    executeSql("DELETE FROM Resource WHERE id = ?", resourceId);
  }
  
  protected void createResource(Long id, Integer indexNumber, String name, String urlName, String description, Long parentFolderId, String type, boolean visible, boolean archived, Date created, Long lastModifierId, Date lastModified) {
    String sql = 
        "insert into " +
        "  Resource (id, indexNumber, name, urlName, description, parentFolder_id, type, visible, archived, created, lastModifier_id, lastModified) " +
        "values " +
        "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    executeSql(sql, id, indexNumber, name, urlName, description, parentFolderId, type, visible, archived, created, lastModifierId, lastModified);
  }
  

}
