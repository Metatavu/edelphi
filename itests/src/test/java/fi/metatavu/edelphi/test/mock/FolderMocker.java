package fi.metatavu.edelphi.test.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FolderMocker extends AbstractResourceMocker {
  
  private List<Long> folderIds = new ArrayList<>();
  
  public FolderMocker mock() {
    return this;
  }
  
  public FolderMocker cleanup() {
    for (Long folderId : folderIds) {
      deleteFolder(folderId);
    }
    
    super.cleanup();
    
    return this;
  }
  
  public long createFolder(Integer indexNumber, String name, String urlName, Long parentFolderId) {
    String description = null;
    boolean visible = true;
    boolean archived = false;
    Date created = new Date();
    Long lastModifierId = 1l;
    Date lastModified = new Date();
    Long id = getNextId("Resource");
    createFolder(id, indexNumber, name, urlName, description, parentFolderId, visible, archived, created, lastModifierId, lastModified);
    return id;
  }

  public void addCreatedFolder(long folderId) {
    folderIds.add(folderId);
  }
  
  public long findPanelRoot(String name) {
    String sql = 
      "SELECT " + 
      "  rootFolder_id " + 
      "FROM " + 
      "  Panel " +  
      "WHERE " + 
      "  name = ?";
    
    return executeSqlLong(sql, name);
  }
  
  protected void createFolder(Long id, Integer indexNumber, String name, String urlName, String description, Long parentFolderId, boolean visible, boolean archived, Date created, Long lastModifierId, Date lastModified) {
    createResource(id, indexNumber, name, urlName, description, parentFolderId, "FOLDER", visible, archived, created, lastModifierId, lastModified);
    executeSql("insert into Folder (id) values (?)", id);
  }
  

  private void deleteFolder(Long folderId) {
    executeSql("delete from Folder where id = ?", folderId);
    deleteResource(folderId);
  }
  
}
