package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.BeforeClass;
import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class LoginTestsBase extends AbstractUITest {
  
  private static final String USERNAME = "test.user";
  private static final String FIRST_NAME = "Test";
  private static final String LAST_NAME = "User";
  private static final String EMAIL = "test.user@example.com";
  private static final String PASSWORD = "qwe";
  
  @BeforeClass
  public static void beforeClass() {
    ensureKeycloakGoogleProvider();
  }
  
  @Test
  public void testIndexRegister() {
    try {
      navigate("/");
      waitAndAssertText(".register-link", "HERE");
      click(".register-link");
      waitAndType("#username", USERNAME);
      waitAndType("#firstName", FIRST_NAME);
      waitAndType("#lastName", LAST_NAME);
      waitAndType("#email", EMAIL);
      waitAndType("#password", PASSWORD);
      waitAndType("#password-confirm", PASSWORD);
      click(".btn-primary");
      
      waitAndAssertText("#GUI_indexProfilePanel .blockTitle h2", "PROFILE");
      waitAndAssertText(".headerUserName", String.format("%s %s", FIRST_NAME, LAST_NAME));
      
      waitAndAssertInputValue("[name='firstName']", FIRST_NAME);
      waitAndAssertInputValue("[name='lastName']", LAST_NAME);
      waitAndAssertInputValue("[name='email']", EMAIL);    
    } finally {
      deleteKeycloakUser(USERNAME);
    }
  }
  
  @Test
  public void testIndexLogin() {
    createKeycloakUser(FIRST_NAME, LAST_NAME, USERNAME, EMAIL, PASSWORD);
    try {
      navigate("/");
      waitAndAssertText(".login-link", "HERE");
      click(".login-link");
      loginKeycloak(USERNAME, PASSWORD);
      
      waitAndAssertText("#GUI_indexProfilePanel .blockTitle h2", "PROFILE");
      waitAndAssertText(".headerUserName", String.format("%s %s", FIRST_NAME, LAST_NAME));
      
      waitAndAssertInputValue("[name='firstName']", FIRST_NAME);
      waitAndAssertInputValue("[name='lastName']", LAST_NAME);
      waitAndAssertInputValue("[name='email']", EMAIL);    
    } finally {
      deleteKeycloakUser(USERNAME);
    }
  }
  
  @Test
  public void testIndexLoginGoole() {
    if (skipGoogleTests()) {
      return;
    }
    
    try {
      navigate("/");
      waitAndAssertText(".login-link", "HERE");
      click(".login-link");
      waitAndClick("#zocial-google");
      
      loginGoogle(getGoogleUserEmail(), getGoogleUserPassword());
      
      waitAndAssertText("#GUI_indexProfilePanel .blockTitle h2", "PROFILE");
      waitAndAssertText(".headerUserName", String.format("%s %s", getGoogleUserFirstName(), getGoogleUserLastName()));

      waitAndAssertInputValue("[name='firstName']", getGoogleUserFirstName());
      waitAndAssertInputValue("[name='lastName']", getGoogleUserLastName());
      waitAndAssertInputValue("[name='email']", getGoogleUserEmail());    
    } finally {
      deleteKeycloakUser(getGoogleUserEmail());
    }
  }

}
