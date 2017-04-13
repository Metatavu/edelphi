package fi.metatavu.edelphi.test.ui.base.panel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import fi.metatavu.edelphi.test.mock.PanelMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class PanelInviteUsersTestsBase extends AbstractUITest {
  
  private static final String INVITE_INPUT_SELECTOR = "input.invite";
  private static final String INVITE_LINK_SELECTOR = "#panelAdminDashboardUsersBlockContent .panelAdminUserRow:nth-child(1) a";

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
  public void testInvite() {
    assertReceivedEmailCount(0);
    
    login(ADMIN_EMAIL);
    createTestPanel();
    
    waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
    waitVisible(INVITE_LINK_SELECTOR);
    assertText(INVITE_LINK_SELECTOR, "Invite / Add Users");
    click(INVITE_LINK_SELECTOR);
    waitAndClick(INVITE_INPUT_SELECTOR);
    waitAndType(INVITE_INPUT_SELECTOR, "test.user@example.com");
    waitVisible(".invitationAutoCompleteEntry");
    waitAndType(INVITE_INPUT_SELECTOR, Keys.ENTER);
    waitAndClick("input[name=sendInvitations]");
    
    waitReceivedEmailCount(1);
    assertReceivedEmailSubject(0, "Invitation to an eDelphi panel test");
    assertReceivedEmailContentStartsWith(0, "You have been invited to an eDelphi panel test");
  }
  
  private void createTestPanel() {
    navigate("/");
    createPanel("test");
    panelMocker.addCreatedPanel("test");
  }
  
}
