package fi.metatavu.edelphi.utils;

import java.util.Date;

import fi.metatavu.edelphi.dao.users.BulletinReadDAO;
import fi.metatavu.edelphi.domainmodel.base.Bulletin;
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
  
}
