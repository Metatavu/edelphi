package fi.metatavu.edelphi.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.users.BulletinReadDAO;
import fi.metatavu.edelphi.domainmodel.base.Bulletin;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.users.User;

public class BulletinUtils {
  
  private BulletinUtils() {
  }
  
  public static boolean isBulletinRead(Bulletin bulletin, User user) {
    BulletinReadDAO bulletinReadDAO = new BulletinReadDAO();
    return bulletinReadDAO.countByBulletinAndUser(bulletin, user) > 0;
  }

  public static void markBulletinRead(Bulletin bulletin, User user) {
    BulletinReadDAO bulletinReadDAO = new BulletinReadDAO();
    bulletinReadDAO.create(bulletin, user, new Date());
  }
  
  public static boolean hasUnreadImportantBulletins(User user) {
    DelfoiBulletinDAO delfoiBulletinDAO = new DelfoiBulletinDAO();
    List<DelfoiBulletin> importantBulletins = delfoiBulletinDAO.listByImportantAndArchivedImportantEndsNullOrAfter(Boolean.FALSE, Boolean.TRUE, new Date());
    
    for (DelfoiBulletin importantBulletin : importantBulletins) {
      if (!isBulletinRead(importantBulletin, user)) {
        return true;
      }
    }
    
    return false;
  }

  public static List<DelfoiBulletin> getUnreadImportantBulletins(User user) {
    DelfoiBulletinDAO delfoiBulletinDAO = new DelfoiBulletinDAO();
    List<DelfoiBulletin> importantBulletins = delfoiBulletinDAO.listByImportantAndArchivedImportantEndsNullOrAfter(Boolean.FALSE, Boolean.TRUE, new Date());
    
    List<DelfoiBulletin> result = new ArrayList<>(importantBulletins.size());
    for (DelfoiBulletin importantBulletin : importantBulletins) {
      if (!isBulletinRead(importantBulletin, user)) {
        result.add(importantBulletin);
      }
    }
    
    return result;
  }
  
}
