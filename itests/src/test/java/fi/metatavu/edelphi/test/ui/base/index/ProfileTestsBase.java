package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class ProfileTestsBase extends AbstractUITest {
  
  private static final String ADMIN_EMAIL = "admin@example.com";
  private static final String PROFILE_PAGE = "/profile.page";

  @Test
  public void testLoginRequired() {
    navigate(PROFILE_PAGE);
    assertLoginScreen();
  }
  
  @Test
  public void testSubscriptionLevelNone() {
    login(ADMIN_EMAIL);
    navigate(PROFILE_PAGE);
    waitAndAssertText("#profileSubscriptionLevelBlockContent p:first-child", "You do not have an active subscription");
  }
  
  @Test
  public void testSubscriptionLevelEdu() {
    updateUserSubscription(1l, "EDU", toDate(2016, 1, 1), toDate(2016, 10, 10));
    login(ADMIN_EMAIL);
    navigate(PROFILE_PAGE);
    waitAndAssertText(".profileSubscriptionLeveText", String.format("Your current subscription level is \"%s\".", "educational"));
    waitAndAssertText(".profileSubscriptionFeaturesLabel", "Your subscription level allows you to:");
    waitAndAssertText(".profileSubscriptionPanels", String.format("Be a manager in %d active panel (currently you are a manager in %d panels)", 1, 0));
    waitAndAssertText(".profileSubscriptionPanelists", String.format("Have %d panelists in each panel", 25));
    waitAndAssertText(".profileSubscriptionEnds", "your subscription ends Oct 10, 2016");
    waitAndAssertText(".profileChangeSubscriptionText", "You may change or continue your subscription by clicking");
    waitAndAssertText(".profileChangeSubscriptionLink", "here"); 
  }
  
}
