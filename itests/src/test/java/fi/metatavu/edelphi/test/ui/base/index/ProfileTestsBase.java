package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class ProfileTestsBase extends AbstractUITest {
  
  @Test
  public void testLoginRequired() {
    navigate("/profile.page");
    assertLoginScreen();
  }
  
  @Test
  public void testSubscriptionLevelNone() {
    login("admin@example.com");
    navigate("/profile.page");
    waitAndAssertText("#profileSubscriptionLevelBlockContent p:first-child", "You do not have an active subscription");
  }
  
}
