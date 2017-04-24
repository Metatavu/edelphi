package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class ProfileTestsBase extends AbstractUITest {
  
  private static final String PROFILE_PAGE = "/profile.page";
  
  @Test
  public void testLoginRequired() {
    navigate(PROFILE_PAGE);
    assertLoginScreen();
  }
  
  @Test
  public void testSubscriptionLevelBasic() {
    updateUserSubscription(1l, "BASIC", toDate(2016, 1, 1), toDate(2016, 10, 10));
    login(ADMIN_EMAIL);
    navigate(PROFILE_PAGE);
    assertSubscriptionLevelTexts("Basic");
  }
  
  @Test
  public void testSubscriptionLevelPlus() {
    updateUserSubscription(1l, "PLUS", toDate(2016, 1, 1), toDate(2016, 10, 10));
    login(ADMIN_EMAIL);
    navigate(PROFILE_PAGE);
    assertSubscriptionLevelTexts("Plus");
  }

  @Test
  public void testSubscriptionLevelPremium() {
    updateUserSubscription(1l, "PREMIUM", toDate(2016, 1, 1), toDate(2016, 10, 10));
    login(ADMIN_EMAIL);
    navigate(PROFILE_PAGE);
    assertSubscriptionLevelTexts("Premium");
  }
  
  private void assertSubscriptionLevelTexts(String level) {
    waitAndAssertText(".profileSubscriptionLevelText", String.format("Your current subscription level is %s.", level));
    waitAndAssertText(".profileSubscriptionEnds", "your subscription ends Oct 10, 2016");
    /**
    TODO: Re-enable after payment service is enabled in production
    waitAndAssertText(".profileChangeSubscriptionText", "You may change or continue your subscription by clicking");
    waitAndAssertText(".profileChangeSubscriptionLink", "here");
    **/
  }
  
}
