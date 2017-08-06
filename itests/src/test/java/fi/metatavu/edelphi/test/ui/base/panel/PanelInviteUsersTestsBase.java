package fi.metatavu.edelphi.test.ui.base.panel;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.metatavu.edelphi.test.mock.PanelMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class PanelInviteUsersTestsBase extends AbstractUITest {
  
  private static final String INVITE_INPUT_SELECTOR = "input.invite";
  private static final String INVITE_LINK_SELECTOR = "#panelAdminDashboardUsersBlockContent .panelAdminUserRow:nth-child(1) a";

  private PanelMocker panelMocker;
  
  @BeforeClass
  public static void beforeClass() {
    ensureKeycloakRealmAdmin();
  }
  
  @Before
  public void before() {
    panelMocker = new PanelMocker();
  }
  
  @After
  public void after() {
    panelMocker.cleanup();
  }
  
  @Test
  public void testInvite() {
    try {
      assertReceivedEmailCount(0);
      
      login(ADMIN_EMAIL);
      createTestPanel();
      
      navigate(String.format("/panel/admin/dashboard.page?panelId=%s", panelMocker.findPanelByName("test")));
      
      waitVisible(INVITE_LINK_SELECTOR);
      assertText(INVITE_LINK_SELECTOR, "Invite / Add Users");
      click(INVITE_LINK_SELECTOR);
      waitAndClick(INVITE_INPUT_SELECTOR);
      waitAndType(INVITE_INPUT_SELECTOR, "test.user@example.com");
      waitAndClick(".invitationAutoCompleteEntry");
      waitVisible(".invitationEntryEmailContainer");
      waitAndClick("input[name=sendInvitations]", 50);
      
      waitReceivedEmailCount(1);
      assertReceivedEmailSubject(0, "Invitation to an eDelphi panel test");
      assertReceivedEmailContentStartsWith(0, "You have been invited to an eDelphi panel test");
      String joinLink = extractJoinLink(getReceivedMailContent(0));
      
      logout();
        
      getWebDriver().get(joinLink);
      waitAndAssertText(".GUI_panel_header_text_panelname", "- test");
      waitAndAssertText(".headerWelcomeText", "Welcome test.user@example.com");
    } finally {
      deleteKeycloakUser("test.user@example.com");
    }
  }
  
  @Test
  public void testDirectAdd() {
    assertReceivedEmailCount(0);
    
    try {
      login(ADMIN_EMAIL);
      createTestPanel();
      
      // Directly add user
      
      navigate(String.format("/panel/admin/dashboard.page?panelId=%s", panelMocker.findPanelByName("test")));
      
      waitVisible(INVITE_LINK_SELECTOR);
      assertText(INVITE_LINK_SELECTOR, "Invite / Add Users");
      click(INVITE_LINK_SELECTOR);
      waitAndClick(INVITE_INPUT_SELECTOR);
      waitAndType(INVITE_INPUT_SELECTOR, "test.user@example.com");
      waitAndClick(".invitationAutoCompleteEntry");
      waitVisible(".invitationEntryEmailContainer");
      waitAndClick("#addUsers1");
      waitAndClick("input[name=sendInvitations]", 50);
      waitPresent(".eventQueueWarningItem");
      String queueItemText = findElement(".eventQueueWarningItem").getText();
      String password = StringUtils.substringAfter(queueItemText, "The password is ");
      logout();
      
      // Attempt to nagivate into panel
      
      navigate("/test");
      
      // Login with created credentials
      
      waitAndClick(".login-link");
      loginKeycloak("test.user@example.com", password);
      
      // Fill missing details in profile view
      
      waitAndAssertText("#GUI_indexProfilePanel .blockTitle h2", "PROFILE");
      
      waitAndType("[name='firstName']", "Test");
      waitAndType("[name='lastName']", "User");
      waitAndClick("[name='updateProfileButton']");
      waitVisible(".eventQueueSuccessItem");
      
      // Navigate again
      
      navigate("/test");
      
      // Enter panel
      
      waitAndAssertText(".GUI_panel_header_text_panelname", "- test");
      waitAndAssertText(".headerWelcomeText", "Welcome test.user@example.com");
      
      assertReceivedEmailCount(0);
    } finally {
      deleteKeycloakUser("test.user@example.com");
    }
  }
  
  private String extractJoinLink(String mailContent) {
    String prefix = "http:";
    String postfix = "join=1";
    return String.format("%s%s%s", prefix, StringUtils.substringBetween(getReceivedMailContent(0), prefix, postfix), postfix);
  }
  
  private void createTestPanel() {
    navigate("/");
    createPanel("test");
    panelMocker.addCreatedPanel("test");
  }
  
}
