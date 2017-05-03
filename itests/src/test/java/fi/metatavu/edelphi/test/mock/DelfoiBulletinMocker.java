package fi.metatavu.edelphi.test.mock;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DelfoiBulletinMocker extends AbstractMocker {

  private List<Long> bulletinIds = new ArrayList<>();
    
  public DelfoiBulletinMocker mock() {
    return this;
  }
  
  public DelfoiBulletinMocker cleanup() {
    for (Long bulletinId : bulletinIds) {
      deleteBulletin(bulletinId);
    }
    
    super.cleanup();
    
    return this;
  }

  public Long getBulletinId(int index) {
    Long result = bulletinIds.get(index);
    assertNotNull(result);
    return result;
  }
  
  public long createBulletin(String title, String message, Long creatorId, Date created, Boolean important, Date importantEnds) {
    return createBulletin(title, message, creatorId, created, creatorId, created, important, importantEnds, Boolean.FALSE);
  }
 
  @SuppressWarnings ("squid:S00107")
  public long createBulletin(String title, String message, Long creatorId, Date created, Long lastModifierId, Date lastModified, Boolean important, Date importantEnds, Boolean archived) {
    Long id = getNextId("Bulletin");
    
    String bulletinSql = 
      "INSERT INTO " + 
      "  Bulletin (id, title, message, archived, creator_id, created, lastModifier_id, lastModified, important, importantEnds) " + 
      "VALUES " + 
      "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      
    executeSql(bulletinSql, id, title, message, archived, creatorId, created, lastModifierId, lastModified, important, importantEnds);
    
    String delfoiBulletinSql = 
      "INSERT INTO " + 
      "  DelfoiBulletin (id, delfoi_id) " + 
      "VALUES " + 
      "  (?, ?)";
      
    executeSql(delfoiBulletinSql, id, 1l);
      
    bulletinIds.add(id);
    
    return id;
  }

  private void deleteBulletin(Long bulletinId) {
    executeSql("DELETE FROM BulletinRead WHERE bulletin_id = ?", bulletinId);
    executeSql("DELETE FROM DelfoiBulletin WHERE id = ?", bulletinId);
    executeSql("DELETE FROM Bulletin WHERE id = ?", bulletinId);
  }
}
