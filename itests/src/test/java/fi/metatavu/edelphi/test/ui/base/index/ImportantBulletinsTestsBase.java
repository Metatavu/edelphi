package fi.metatavu.edelphi.test.ui.base.index;

import java.time.OffsetDateTime;
import java.util.Date;

import org.junit.Test;

import fi.metatavu.edelphi.test.mock.DelfoiBulletinMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class ImportantBulletinsTestsBase extends AbstractUITest {
  
  private static final String BULLETIN_DIALOG_SELECTOR = ".important-bulletin-dialog";
  
  private DelfoiBulletinMocker delfoiBulletinMocker = new DelfoiBulletinMocker();
  
  @Test
  public void testImportantBulletinPopup() {
    Date created = toDate(2017, 5, 3);
    
    delfoiBulletinMocker.createBulletin("Normal bulletin", "<p>Normal bulletin content</p>", 1l, created, Boolean.FALSE, null);
    
    // Normal bulletin should not be visible
    
    login(ADMIN_EMAIL);
    navigate("/");
    assertNotPresent(BULLETIN_DIALOG_SELECTOR);
    logout();
    
    delfoiBulletinMocker.createBulletin("Important bulletin", "<p>Important bulletin content</p>", 1l, created, Boolean.TRUE, null);

    // Important bulletin should be shown on login
    
    login(ADMIN_EMAIL);
    navigate("/");
        
    waitPresent(BULLETIN_DIALOG_SELECTOR);
    assertCount(".important-bulletin-dialog .bulletin", 1);
    assertText(".important-bulletin-dialog .bulletin-title", "Important bulletin");
    assertText(".important-bulletin-dialog .bulletin-date", "Published 5/3/17 12:00 a.m.");
    assertText(".important-bulletin-dialog .bulletin-message p", "Important bulletin content");
   
    // Bulletin already shown, so should not be present anymore
    
    logout();
    login(ADMIN_EMAIL);
    navigate("/");
    assertNotPresent(BULLETIN_DIALOG_SELECTOR);
  }
  
  @Test
  public void testImportantBulletinExpired() {
    Date created = toDate(2017, 5, 3);
    Date weekFromNow = Date.from(OffsetDateTime.now().plusWeeks(1).toInstant());
    Date weekAgo = Date.from(OffsetDateTime.now().minusWeeks(1).toInstant());
    
    delfoiBulletinMocker.createBulletin("Normal bulletin", "<p>Normal bulletin content</p>", 1l, created, Boolean.FALSE, null);
    delfoiBulletinMocker.createBulletin("Expiring bulletin", "<p>Expiring bulletin content</p>", 1l, created, Boolean.TRUE, weekFromNow);
    delfoiBulletinMocker.createBulletin("Expired bulletin", "<p>Expired bulletin content</p>", 1l, created, Boolean.TRUE, weekAgo);
    
    // Only non expired important bulletin should be visible

    login(ADMIN_EMAIL);
    navigate("/");

    waitPresent(BULLETIN_DIALOG_SELECTOR);
    assertCount(".important-bulletin-dialog .bulletin", 1);
    assertText(".important-bulletin-dialog .bulletin-title", "Expiring bulletin");
    assertText(".important-bulletin-dialog .bulletin-date", "Published 5/3/17 12:00 a.m.");
    assertText(".important-bulletin-dialog .bulletin-message p", "Expiring bulletin content");
  }

}
