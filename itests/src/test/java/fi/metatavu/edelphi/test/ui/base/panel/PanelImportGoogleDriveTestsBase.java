package fi.metatavu.edelphi.test.ui.base.panel;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.metatavu.edelphi.test.mock.PanelMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class PanelImportGoogleDriveTestsBase extends AbstractUITest {

  private PanelMocker panelMocker;

  @BeforeClass
  public static void beforeClass() {
    ensureKeycloakGoogleProvider();
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
  public void testLoginInternal() {
    createKeycloakUser(getGoogleUserFirstName(), getGoogleUserLastName(), getGoogleUserEmail(), getGoogleUserEmail(), getGoogleUserPassword());
    try {
      navigate("/");
      waitAndAssertText(".login-link", "HERE");
      click(".login-link");
      
      loginKeycloak(getGoogleUserEmail(), getGoogleUserPassword());
      createTestPanel();
      waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
      waitAndClick(".GDOCSIMPORT a");
      
      // When user is not yet logged in with Google, application should redirect user to
      // login with Google account

      loginGoogle(getGoogleUserEmail(), getGoogleUserPassword());
      linkKeycloakAccount(getGoogleUserEmail(), getGoogleUserPassword());
      
      waitPresent(".materialsBlock");
      assertCount(".materialsBlock .materialRow", 0);
      
      waitAndClick(".panelAdminMaterialImportGDocsEntry:nth-child(1) .panelAdminMaterialImportGDocsEntryCheck");
      waitAndClick(".formSubmitContainer input");
      waitPresent(".materialsBlock .materialRow .panelGenericTitle a");
      assertCount(".materialsBlock .materialRow", 1);
      assertText(".materialsBlock .materialRow .panelGenericTitle a", "A Good Document for Testing");
    } finally {
      deleteKeycloakUser(getGoogleUserEmail());
    }
  }
  
  @Test
  public void testLoginGoogle() {
    navigate("/");
    waitAndAssertText(".login-link", "HERE");
    click(".login-link");
    waitAndClick("#zocial-google");
    
    loginGoogle(getGoogleUserEmail(), getGoogleUserPassword());
    try {
      waitVisible(".headerUserName");
      
      createTestPanel();
      waitAndClick(".panel .GUI_navigation .menu .menuItem:nth-child(2) a");
      waitAndClick(".GDOCSIMPORT a");
      waitPresent(".materialsBlock");
      assertCount(".materialsBlock .materialRow", 0);
      
      waitAndClick(".panelAdminMaterialImportGDocsEntry:nth-child(1) .panelAdminMaterialImportGDocsEntryCheck");
      waitAndClick(".formSubmitContainer input");
      waitPresent(".materialsBlock .materialRow .panelGenericTitle a");
      assertCount(".materialsBlock .materialRow", 1);
      assertText(".materialsBlock .materialRow .panelGenericTitle a", "A Good Document for Testing");
    } finally {
      deleteKeycloakUser(getGoogleUserEmail());
    }
  }

  private void createTestPanel() {
    navigate("/");
    createPanel("test");
    panelMocker.addCreatedPanel("test");
  }
  
}
