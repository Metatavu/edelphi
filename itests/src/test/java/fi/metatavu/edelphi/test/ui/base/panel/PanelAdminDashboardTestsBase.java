package fi.metatavu.edelphi.test.ui.base.panel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.metatavu.edelphi.test.mock.PanelMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class PanelAdminDashboardTestsBase extends AbstractUITest {
  
  
  private static final String QUERY_ACTIVITY_SELECTOR = "#panelAdminDashboardProcessLeftBlock .feature:nth-of-type(1)";
  private static final String QUERY_RESULTS_SELECTOR = "#panelAdminDashboardProcessLeftBlock .feature:nth-of-type(2)";
  private static final String COMPARE_REPORTS_SELECTOR = "#panelAdminDashboardProcessLeftBlock .feature:nth-of-type(3)";

  private static final String PANEL_STAMPS_SELECTOR = "#panelAdminDashboardProcessRightBlock .feature:nth-of-type(1)";
  private static final String PANEL_EXPERTS_SELECTOR = "#panelAdminDashboardProcessRightBlock .feature:nth-of-type(2)";
  private static final String SEND_EMAIL_SELECTOR = "#panelAdminDashboardProcessRightBlock .feature:nth-of-type(3)";
  private static final String PANEL_BULLETINS_SELECTOR = "#panelAdminDashboardProcessRightBlock .feature:nth-of-type(4)";
  
  private static final String INVITE_FEATURE_SELECTOR = "#panelAdminDashboardUsersBlockContent .feature:nth-of-type(1)";
  private static final String PANEL_USERS = "#panelAdminDashboardUsersBlockContent .feature:nth-of-type(2)";
  private static final String PANEL_USER_GROUPS = "#panelAdminDashboardUsersBlockContent .feature:nth-of-type(3)";

  private PanelMocker panelMocker;
  
  @Before
  public void before() {
    panelMocker = new PanelMocker();
  }
  
  @After
  public void after() {
    panelMocker.cleanup();
  }

  @Test
  public void testFeaturesBasic() {
    login(ADMIN_EMAIL);
    createTestPanel();
    
    updateUserSubscription(1l, "BASIC", toDate(2016, 1, 1), toDate(2250, 10, 10));
    waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
    
    assertQueryActivitySelectorNotAvailable();
    assertQueryResultsNotAvailable();
    assertReportComparisonNotAvailable();
    assertPanelStampsNotAvailable();
    assertPanelExpertsAvailable();
    assertSendEmailAvailable();
    assertManageBulletinsAvailable();
    assertInviteUsersAvailable();
    assertAdministerUsersAvailable();
    assertAdministerUserGroupsAvailable();
  }

  @Test
  public void testFeaturesPlus() {
    login(ADMIN_EMAIL);
    createTestPanel();
    
    updateUserSubscription(1l, "PLUS", toDate(2016, 1, 1), toDate(2250, 10, 10));
    waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
    
    assertQueryActivitySelectorAvailable();
    assertQueryResultsAvailable();
    assertReportComparisonAvailable();
    assertPanelStampsNotAvailable();
    assertPanelExpertsAvailable();
    assertSendEmailAvailable();
    assertManageBulletinsAvailable();
    assertInviteUsersAvailable();
    assertAdministerUsersAvailable();
    assertAdministerUserGroupsAvailable();
  }

  @Test
  public void testFeaturesPremium() {
    login(ADMIN_EMAIL);
    createTestPanel();
    
    updateUserSubscription(1l, "PREMIUM", toDate(2016, 1, 1), toDate(2250, 10, 10));
    waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
    
    assertQueryActivitySelectorAvailable();
    assertQueryResultsAvailable();
    assertReportComparisonAvailable();
    assertPanelStampsAvailable();
    assertPanelExpertsAvailable();
    assertSendEmailAvailable();
    assertManageBulletinsAvailable();
    assertInviteUsersAvailable();
    assertAdministerUsersAvailable();
    assertAdministerUserGroupsAvailable();
  }
  
  private void assertQueryActivitySelectorNotAvailable() {
    assertFeatureNotAvailable(QUERY_ACTIVITY_SELECTOR, "Query Activities");
  }

  private void assertQueryActivitySelectorAvailable() {
    assertFeatureAvailable(QUERY_ACTIVITY_SELECTOR, "Query Activities");
  }
  
  private void assertQueryResultsNotAvailable() {
    assertFeatureNotAvailable(QUERY_RESULTS_SELECTOR, "Query Results");
  }
  
  private void assertQueryResultsAvailable() {
    assertFeatureAvailable(QUERY_RESULTS_SELECTOR, "Query Results");
  }
  
  private void assertReportComparisonNotAvailable() {
    assertFeatureNotAvailable(COMPARE_REPORTS_SELECTOR, "Report Comparison");
  }
  
  private void assertReportComparisonAvailable() {
    assertFeatureAvailable(COMPARE_REPORTS_SELECTOR, "Report Comparison");
  }
  
  private void assertPanelStampsNotAvailable() {
    assertFeatureNotAvailable(PANEL_STAMPS_SELECTOR, "Panel's Stamps");
  }
  
  private void assertPanelStampsAvailable() {
    assertFeatureAvailable(PANEL_STAMPS_SELECTOR, "Panel's Stamps");
  }
  
  private void assertPanelExpertsAvailable() {
    assertFeatureAvailable(PANEL_EXPERTS_SELECTOR, "Panel's Experts");
  }
  
  private void assertSendEmailAvailable() {
    assertFeatureAvailable(SEND_EMAIL_SELECTOR, "Send Email");
  }
  
  private void assertManageBulletinsAvailable() {
    assertFeatureAvailable(PANEL_BULLETINS_SELECTOR, "Manage Bulletins");
  }
  
  private void assertInviteUsersAvailable() {
    assertFeatureAvailable(INVITE_FEATURE_SELECTOR, "Invite / Add Users");
  }
  
  private void assertAdministerUsersAvailable() {
    assertFeatureAvailable(PANEL_USERS, "Administer Users");
  }
  
  private void assertAdministerUserGroupsAvailable() {
    assertFeatureAvailable(PANEL_USER_GROUPS, "Administer Usergroups");
  }
  
  private void assertFeatureNotAvailable(String selector, String expectedTitle) {
    waitVisible(selector);
    assertText(String.format("%s a", selector), expectedTitle);
    assertClassPresent(selector, "featureNotAvailableOnSubscriptionLevel");
  }
  
  private void assertFeatureAvailable(String selector, String expectedTitle) {
    waitVisible(selector);
    assertText(String.format("%s a", selector), expectedTitle);
    assertClassNotPresent(selector, "featureNotAvailableOnSubscriptionLevel");
  }
  
  private void createTestPanel() {
    navigate("/");
    createPanel("test");
    panelMocker.addCreatedPanel("test");
  }
  
}
