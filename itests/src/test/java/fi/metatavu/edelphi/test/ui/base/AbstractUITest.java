package fi.metatavu.edelphi.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.IdentityProvidersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@SuppressWarnings ("squid:S2187")
public class AbstractUITest {
  
  public static final String ADMIN_EMAIL = "admin@example.com";
  private static final long TEST_AUTH_SOURCE_ID = 1l;

  private Logger logger = Logger.getLogger(AbstractUITest.class.getName());
  
  private WebDriver webDriver;
  private GreenMail greenMail = new GreenMail(new ServerSetup(getPortSmtp(), "localhost", ServerSetup.PROTOCOL_SMTP));

  @Rule
  public TestWatcher testWatcher = new TestBaseWatcher();
  
  @Rule
  public TestName testName = new TestName();

  @Before
  @SuppressWarnings ("squid:S106")
  public void printName() {
    System.out.println(String.format("> %s", testName.getMethodName()));
  }
  
  @Before
  public void startGreenMail() {
    getGreenMail().start(); 
  }
  
  @After
  public void stopGreenMail() {
    getGreenMail().stop(); 
  }
  
  public void navigate(String path) {
    webDriver.get(String.format("%s%s", getAppUrl(), path));
  }
  
  protected void login(String email) {
    navigate(String.format("/dologin.json?authSource=%d&username=%s", TEST_AUTH_SOURCE_ID, email));
  }
  
  protected void logout() {
    navigate("/logout.page");
  }

  protected void assertLoginScreen() {
    waitAndAssertText(".errorPageDescriptionContainer", "You need to be logged in to access the requested page.");
  }
  
  protected void createPanel(String name) {
    createPanel(name, null);
  }  
  
  protected void createPanel(String name, String description) {
    navigate("/");
    waitAndClick(".createPanelBlockCreatePanelLink");
    waitAndType("input[name='createPanel_panelName']", name);
    
    if (StringUtils.isNotBlank(description)) {
      waitAndType(".createPanel_panelDescription", description);
    }
    
    waitAndClick(".createPanel_donePageLink");
    waitNotVisible(".createPanelBlock_createPanelDialogOverlay");
  }

  protected void waitAndClick(String selector) {
    waitAndClick(selector, 0);
  }
  
  protected void waitAndClick(String selector, int ms) {
    waitVisible(selector);
    waitMs(ms);
    click(selector);
  }
  
  protected void waitAndClick(String[] selectors, int ms) {
    waitVisible(selectors);
    waitMs(ms);
    click(selectors);
  }

  @SuppressWarnings ("squid:S1166")
  protected int getElementWidth(String selector) {
    waitVisible(selector); 
    while (true) {
      try {
        List<WebElement> elements = findElements(selector);
        if (!elements.isEmpty()) {
          return getElementWidth(elements.get(0));
        }
      } catch (Exception e) {
        // Ignore
      }
    }
  }
  
  protected int getElementWidth(WebElement element) {
    return element.getSize().getWidth();
  }

  @SuppressWarnings ("squid:S1166")
  protected int getElementHeight(String selector) {
    waitVisible(selector); 
    while (true) {
      try {
        List<WebElement> elements = findElements(selector);
        if (!elements.isEmpty()) {
          return getElementHeight(elements.get(0));
        }
      } catch (Exception e) {
        // Ignore
      }
    }
  }
  
  protected int getElementHeight(WebElement element) {
    return element.getSize().getHeight();
  }
  
  protected void clickOffset(String selector, int offsetX, int offsetY) {
    clickOffset(findElements(selector).get(0), offsetX, offsetY);
  }
  
  protected void clickOffset(WebElement element, int offsetX, int offsetY) {
    Actions build = new Actions(webDriver);
    build
      .moveToElement(element, offsetX, offsetY)
      .click()
      .build()
      .perform();
  }
  
  protected void click(String... selector) {
    findElements(selector).get(0).click();
  }

  protected void waitAndType(String selector, String text) {
    waitAndType(selector, text, 0);
  }
  
  protected void waitAndType(String selector, String text, int waitMs) {
    waitPresent(selector);
    waitMs(waitMs);
    findElements(selector).get(0).sendKeys(text);
  }

  protected void waitAndType(String[] selectors, String text, int waitMs) {
    waitPresent(selectors);
    waitMs(waitMs);
    findElements(selectors).get(0).sendKeys(text);
  }
  
  private void waitMs(int ms) {
    if (ms > 0) { 
      try {
        Thread.sleep(ms);
      } catch (InterruptedException e) {
      }
    }
  }
  
  protected void waitAndType(String selector, Keys... keys) {
    waitPresent(selector);
    findElements(selector).get(0).sendKeys(keys);
  }
  
  protected void waitPresent(final String... selectors) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      
      @Override
      public Boolean apply(WebDriver arg0) {
        return !findElements(selectors).isEmpty();
      }
      
    });
    
  }
  
  protected void waitNotPresent(final String... selectors) {
    new WebDriverWait(webDriver, 60).until(new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver arg0) {
        return findElements(selectors).isEmpty();
      }
    });
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void waitVisible(final String... selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      
      @Override
      public Boolean apply(WebDriver arg0) {
        try {
          List<WebElement> elements = findElements(selector);
          if (elements.isEmpty()) {
            return false;
          }
          
          for (WebElement element : elements) {
            if (!element.isDisplayed()){
              return false;
            }
          }
          
          return true;
        } catch (Exception e) {
          return false;
        }
      }
      
    });
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void waitNotVisible(final String selector) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      
      @Override
      public Boolean apply(WebDriver arg0) {
        try {
          List<WebElement> elements = findElementsBySelector(selector);
          if (elements.isEmpty()) {
            return true;
          }
        
          for (WebElement element : elements) {
            if (element.isDisplayed()){
              return false;
            }
          }
        
          return true;
        } catch (Exception e) {
          return false;
        }
      }
      
    });
  }
  
  protected WebDriver createLocalDriver() {
    switch (getBrowser()) {
      case "chrome":
        return createChromeDriver(false);
      case "chrome-headless":
        return createChromeDriver(true);
      case "phantomjs":
        return createPhantomJsDriver();
      case "firefox":
        return createFirefoxDriver();
      default:
      break;
    }
    
    fail(String.format("Unknown browser %s", getBrowser()));
    
    return null;
  }

  protected LocalDate toLocalDate(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }
  
  protected Date toDate(int year, int month, int day) {
    return toDate(toLocalDate(year, month, day));
  }
  
  protected Date toDate(ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }
  
  protected Date toDate(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
  }
  
  protected Date toDate(LocalDate localDate, ZoneId zoneId) {
    return Date.from(localDate.atStartOfDay(zoneId).toInstant());
  }
  
  protected Date toDate(LocalDate localDate) {
    return toDate(localDate, ZoneId.systemDefault());
  }
  
  protected WebDriver createChromeDriver(boolean headless) {
    ChromeOptions options = new ChromeOptions();
    
    if (headless) {
      options.addArguments("--headless", "--disable-gpu", "window-size=1280,1024");
    } 
    
    ChromeDriver driver = new ChromeDriver(options);
    
    if (!headless) {
      driver.manage().window().setSize(new Dimension(1280, 1024));
    }
    
    return driver;
  }

  protected WebDriver createFirefoxDriver() {
    return new FirefoxDriver();
  }
  
  protected WebDriver createPhantomJsDriver() {
    DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
    
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, (new File(".phantomjs/bin/phantomjs")).getAbsolutePath());
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=true", "--webdriver-loglevel=NONE", "--load-images=false" } );
    PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
    driver.manage().window().setSize(new Dimension(1280, 1024));
    return driver;
  }
  
  protected WebDriver getWebDriver() {
    return this.webDriver;
  }
  
  protected void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  protected String getAppUrl() {
    return String.format("%s%s:%d", "http://", getHost(), getPortHttp());
  }

  protected String getSeleniumVersion() {
    return System.getProperty("it.selenium.version");
  }
  
  protected String getPlatform() {
    return System.getProperty("it.platform");
  }

  protected String getBrowserVersion() {
    return System.getProperty("it.browser.version");
  }

  protected String getBrowser() {
    return System.getProperty("it.browser");
  }

  protected String getHost() {
    return System.getProperty("it.host");
  }
  
  protected int getPortHttp() {
    return Integer.parseInt(System.getProperty("it.port.http"));
  }
  
  protected int getPortSmtp() {
    return Integer.parseInt(System.getProperty("it.port.smtp"));
  }
  
  protected void waitAndAssertText(String selector, String text) {
    waitAndAssertText(selector, text, true, true);
  }

  protected void assertText(String selector, String text) {
    assertText(selector, text, true, true);
  }
  
  protected void waitAndAssertText(String selector, String text, boolean trim, boolean ignoreCase) {
    waitPresent(selector);
    assertText(selector, text, trim, ignoreCase);
  }
  
  protected void waitAndAssertInputValue(String selector, String value) {
    waitPresent(selector);
    assertInputValue(selector, value);
  }
  
  protected void assertInputValue(String selector, String value) {
    try {
      WebElement element = findElement(selector);
      assertNotNull(element);
      assertEquals(value, element.getAttribute("value"));
    } catch (StaleElementReferenceException serf) {
      assertInputValue(selector, value);
    }
  }

  protected void assertText(String selector, String text, boolean trim, boolean ignoreCase) {
    try {
      WebElement element = findElement(selector);
      assertNotNull(element);
      
      String elementText = element.getText();
      
      if (trim) {
        elementText = StringUtils.trim(elementText);
      }
      
      if (ignoreCase) {
        assertEquals(StringUtils.lowerCase(trim ? StringUtils.trim(text) : text), StringUtils.lowerCase(elementText));
      } else {
        assertEquals(trim ? StringUtils.trim(text) : text, elementText);
      }
    } catch (StaleElementReferenceException serf) {
      assertText(selector, text, trim, ignoreCase);
    }
  }
  
  protected WebElement findElement(String selector) {
    List<WebElement> elements = findElements(selector);
    if (!elements.isEmpty()) {
      return elements.get(0);
    }
    
    return null;
  }
  
  protected void assertNotPresent(final String... selectors) {
    for (String selector : selectors) {
      List<WebElement> elements = findElements(selector);
      assertTrue(String.format("Found %d elements with selector %s", elements.size(), selector), elements.isEmpty());
    }
  }
  
  
  protected void assertCount(String selector, int expected) {
    assertEquals(expected, findElements(selector).size());
  }


  protected void assertClassNotPresent(String selector, String className) {
    WebElement element = findElement(selector);
    assertNotNull(element);
    String[] classes = StringUtils.split(element.getAttribute("class"));
    assertFalse(String.format("%s contains css class %s", selector, className), ArrayUtils.contains(classes, className));
  }

  protected void assertClassPresent(String selector, String className) {
    WebElement element = findElement(selector);
    assertNotNull(element);
    String[] classes = StringUtils.split(element.getAttribute("class"));
    assertTrue(String.format("%s does not contain css class %s", selector, className), ArrayUtils.contains(classes, className));
  }

  protected void assertInputEnabled(String selector) {
    WebElement element = findElement(selector);
    assertNotNull(element);
    assertTrue(element.isEnabled());
  }

  protected void assertInputDisabled(String selector) {
    WebElement element = findElement(selector);
    assertNotNull(element);
    assertFalse(element.isEnabled());
  }

  protected void assertVisible(final String... selectors) {
    for (String selector : selectors) {
      List<WebElement> elements = findElements(selector);
      assertTrue(String.format("Could not find elements with %s", StringUtils.join(selectors, ",")), !elements.isEmpty());
      boolean visible = false;
      
      for (WebElement element : elements) {
        if (element.isDisplayed()) {
          visible = true;
          break;
        }
      }
      
      assertTrue(String.format("No visible elements for selector %s", StringUtils.join(selectors, ",")), visible);
      
    }
  }
  
  protected List<WebElement> findElements(String... selectors) {
    List<WebElement> result = new ArrayList<>();

    for (String selector : selectors) {
      result.addAll(findElementsBySelector(selector));
    }
    
    return result;
  }
  
  @SuppressWarnings ("squid:S1166")
  private List<WebElement> findElementsBySelector(String selector) {
    try {
      return ((FindsByCssSelector) getWebDriver()).findElementsByCssSelector(selector);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }
  
  
  @SuppressWarnings ("squid:S1166")
  protected List<WebElement> findElements(String selector) {
    try {
      return ((FindsByCssSelector) getWebDriver()).findElementsByCssSelector(selector);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }
  
  protected GreenMail getGreenMail() {
    return greenMail;
  }

  protected void waitReceivedEmailCount(final int expect) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      
      @Override
      public Boolean apply(WebDriver arg0) {
        int messageCount = getGreenMail().getReceivedMessages().length;
        return messageCount == expect;
      }
      
    });
  }

  protected void assertReceivedEmailCount(int expected) {
    assertEquals(expected, getGreenMail().getReceivedMessages().length);
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void assertReceivedEmailSubject(int index, String expected) {
    try {
      assertEquals(expected, getGreenMail().getReceivedMessages()[index].getSubject());
    } catch (MessagingException e) {
      fail(e.getMessage());
    }
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void assertReceivedEmailContent(int index, String expected) {
    assertEquals(expected, getReceivedMailContent(index));
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void assertReceivedEmailContentStartsWith(int index, String expected) {
    assertTrue(StringUtils.startsWith(getReceivedMailContent(index), expected));
  }
  
  protected String getReceivedMailContent(int index) {
    try {
      return String.valueOf(getGreenMail().getReceivedMessages()[index].getContent());
    } catch (MessagingException | IOException e) {
      fail(e.getMessage());
      return null;
    }
  }
  
  protected void waitUrlMatches(final String regex) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.getCurrentUrl().matches(regex);
      }
    });
  }

  protected void loginKeycloak(String username, String password) {
    waitAndType("#username", username);
    waitAndType("#password", password);
    click(".btn-primary");
  }

  protected void loginGoogle(String email, String password) {
    waitAndType(new String[] { "#identifierId", "input[type='email']" }, email, 300);
    waitAndClick(new String[] { "#identifierNext", "#next" }, 300);
    waitAndType(new String[] { "#password input[name='password']", "#Passwd" }, password, 300);
    waitAndClick(new String[] { "#passwordNext", "#signIn" }, 300);
    
    waitVisible(".headerUserName", "#submit_approve_access", "#linkAccount");
    
    while (!findElements("#submit_approve_access").isEmpty()) {
      findElement("#submit_approve_access").click();
      waitMs(300);
      waitVisible(".headerUserName", "#submit_approve_access", "#linkAccount");
    }
  }
  
  protected void linkKeycloakAccount(String username, String password) {
    waitAndClick("#linkAccount");
    waitVisible("#kc-login");
    waitAndType("#password", password);
    waitAndClick("#kc-login");
  }

  protected static void ensureKeycloakGoogleProvider() {
    Keycloak client = getKeycloakClient();
    
    RealmResource realmResource = client.realm("edelphi");
    IdentityProvidersResource identityProvidersResource = realmResource.identityProviders();
    
    List<IdentityProviderRepresentation> providerRepresentations = identityProvidersResource.findAll();
    for (int i = 0; i < providerRepresentations.size(); i++) {
      if ("google".equals(providerRepresentations.get(i).getProviderId())) {
        return;
      }
    }
    
    String apiKey = System.getenv("GOOGLE_API_KEY");
    String apiSecret = System.getenv("GOOGLE_API_SECRET"); 
    
    Map<String, String> config = new HashMap<>();
    config.put("defaultScope", "openid profile email https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/drive.file");
    config.put("clientId", apiKey);
    config.put("disableUserInfo", "");
    config.put("userIp", "");
    config.put("clientSecret", apiSecret);
      
    IdentityProviderRepresentation providerRepresentation = new IdentityProviderRepresentation();
    providerRepresentation.setAlias("google");
    providerRepresentation.setProviderId("google");
    providerRepresentation.setEnabled(true);
    providerRepresentation.setTrustEmail(true);
    providerRepresentation.setStoreToken(true);
    providerRepresentation.setAddReadTokenRoleOnCreate(true);
    providerRepresentation.setConfig(config);
    
    identityProvidersResource.create(providerRepresentation);
  }
  
  protected static void ensureKeycloakRealmAdmin() {
    Keycloak client = getKeycloakClient();
    
    RealmResource realmResource = client.realm("edelphi");
    UsersResource usersResource = realmResource.users();
    List<UserRepresentation> users = usersResource.search("admin");
    if (users.isEmpty()) {
      createKeycloakUser("Realm", "Admin", "admin", "admin@example.com", "admin");
      users = usersResource.search("admin");
      assignClientRole(users.get(0), "realm-management", "manage-users");
    }
  }
  
  protected static void assignClientRole(UserRepresentation userRepresentation, String clientId, String roleName) {
    Keycloak keycloakClient = getKeycloakClient();
    
    RealmResource realmResource = keycloakClient.realm("edelphi");
    UsersResource usersResource = realmResource.users();
    ClientsResource clientsResource = realmResource.clients();
    
    List<ClientRepresentation> clientRepresentations = clientsResource.findByClientId(clientId);
    ClientRepresentation clientRepresentation = clientRepresentations.isEmpty() ? null : clientRepresentations.get(0);
    RoleMappingResource roleMappingResource = usersResource.get(userRepresentation.getId()).roles();
    RoleScopeResource clientRoleScopeResource = roleMappingResource.clientLevel(clientRepresentation.getId());
    List<RoleRepresentation> availableRoleRepresentations = clientRoleScopeResource.listAvailable();
    RoleRepresentation roleRepresentation = null;
    
    for (RoleRepresentation availableRoleRepresentation : availableRoleRepresentations) {
      if (roleName.equals(availableRoleRepresentation.getName())) {
        roleRepresentation = availableRoleRepresentation;
        break;
      }
    }
    
    clientRoleScopeResource.add(Arrays.asList(roleRepresentation));
  }
  
  protected static void createKeycloakUser(String firstName, String lastName, String username, String email, String password) {
    Keycloak client = getKeycloakClient();
    
    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
    credentialRepresentation.setValue(password);
    credentialRepresentation.setTemporary(false);
    
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(username);
    userRepresentation.setFirstName(firstName);
    userRepresentation.setLastName(lastName);
    userRepresentation.setCredentials(Arrays.asList(credentialRepresentation));
    userRepresentation.setEnabled(true);
    userRepresentation.setEmail(email);
    
    RealmResource realmResource = client.realm("edelphi");
    UsersResource usersResource = realmResource.users();
    
    Response response = usersResource.create(userRepresentation);
    
    assertTrue(response.getStatus() >= 200 && response.getStatus() <= 299);
  }
  
  protected void deleteKeycloakUser(String username) {
    Keycloak client = getKeycloakClient();
    RealmResource realmResource = client.realm("edelphi");
    UsersResource usersResource = realmResource.users();
    List<UserRepresentation> users = usersResource.search(username);
    if (!users.isEmpty()) {
      String keycloakUserId = users.get(0).getId();
      usersResource.delete(keycloakUserId);
      deleteUsersByUserIdentification(keycloakUserId, 2l);
    }
  }

  private void deleteUsersByUserIdentification(String externalId, Long authSourceId) {
    executeUpdate("UPDATE User SET nickname = 'DELETE', defaultEmail_id = NULL WHERE id in (SELECT user_id FROM UserIdentification WHERE externalId = ? AND authSource_id = ?)", externalId, authSourceId);
    executeUpdate("UPDATE Panel SET creator_id = 1, lastModifier_id = 1 WHERE creator_id in (SELECT id FROM User WHERE nickname = 'DELETE') OR lastModifier_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("UPDATE PanelStamp SET creator_id = 1, lastModifier_id = 1 WHERE creator_id in (SELECT id FROM User WHERE nickname = 'DELETE') OR lastModifier_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("UPDATE Resource SET creator_id = 1, lastModifier_id = 1 WHERE creator_id in (SELECT id FROM User WHERE nickname = 'DELETE') OR lastModifier_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM UserEmail WHERE user_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM UserIdentification WHERE externalId = ? AND authSource_id = ?", externalId, authSourceId);
    executeUpdate("DELETE FROM BulletinRead WHERE user_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM UserSetting WHERE user_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM PanelUser WHERE user_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM DelfoiUser WHERE user_id in (SELECT id FROM User WHERE nickname = 'DELETE')");
    executeUpdate("DELETE FROM User WHERE nickname = 'DELETE'");
  }

  private static Keycloak getKeycloakClient() {
    return KeycloakBuilder.builder()
      .serverUrl("http://localhost:8380/auth")
      .realm("master")
      .username("admin")
      .password("admin")
      .clientId("admin-cli")
      .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(1).build())
      .build();
  }
  
  protected void savePageSource() throws IOException {
    savePageSource("target", String.format("%s_%s.html", testName.getMethodName(), System.currentTimeMillis()));
  }
  
  protected void takeScreenshot() throws IOException {
    takeScreenshot("target", String.format("%s_%s.png", testName.getMethodName(), System.currentTimeMillis()));
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void takeScreenshot(String parentDirectory, String filename) {
    try {
      File file = new File(parentDirectory, filename);
      if (file.createNewFile()) {
        takeScreenshot(file);
        return;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void savePageSource(String parentDirectory, String filename) {
    try {
      File file = new File(parentDirectory, filename);
      if (file.createNewFile()) {
        savePageSource(file);
        return;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void takeScreenshot(File file) throws IOException {
    TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
    
    FileOutputStream fileOuputStream = new FileOutputStream(file);
    try {
     fileOuputStream.write(takesScreenshot.getScreenshotAs(OutputType.BYTES));
    } finally {
      fileOuputStream.flush();
      fileOuputStream.close();
    }
  }
  
  protected void savePageSource(File file) throws IOException {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(webDriver.getPageSource());
    };
    
    System.out.println(String.format("Saved html as %s", file.getAbsolutePath()));
  }

  protected void acceptPaytrailPayment(Double expectedAmount) {
    String paymentAmount = String.format(Locale.GERMAN, " %.2f \u20AC", expectedAmount);
    waitAndAssertText("#payment-amount", paymentAmount, true, true);
    
    waitAndClick("input[value=\"Osuuspankki\"]");
    waitPresent("*[name='ktunn']");

    waitAndType("*[name='id']", "123456");
    waitAndType("*[name='pw']", "7890");
    waitAndClick("*[name='ktunn']");

    waitAndType("*[name='avainluku']", "1234");
    waitAndClick("*[name='avainl']");
    waitAndClick("#Toiminto");
  }
  
  protected void cancelPaytrailPayment(Double expectedAmount) {
    String paymentAmount = String.format(Locale.GERMAN, " %.2f \u20AC", expectedAmount);
    waitAndAssertText("#payment-amount", paymentAmount, true, true);
    
    waitAndClick("input[value=\"Osuuspankki\"]");
    waitPresent("*[name='ktunn']");

    waitAndType("*[name='id']", "123456");
    waitAndType("*[name='pw']", "7890");
    waitAndClick("*[name='ktunn']");

    waitAndType("*[name='avainluku']", "1234");
    waitAndClick("*[name='avainl']");
    waitAndClick("*[name='Lopeta']");
    waitAndClick(".PalveluSisalto a");
  }
  
  protected void updateUserSubscription(Long userId, String subscriptionLevel, Date subscriptionStarted, Date subscriptionEnds) {
    String sql =
       "UPDATE " +
       "   User " +
       "SET " +
       "  subscriptionLevel = ?, " +
       "  subscriptionStarted = ?, " +
       "  subscriptionEnds = ? " +
       "WHERE " +
       "  id = ? ";
    
    executeUpdate(sql, subscriptionLevel, subscriptionStarted, subscriptionEnds, userId);
  }
  
  protected void updateUserPlan(Long userId, Long planId) {
    String sql =
        "UPDATE " +
        "   User " +
        "SET " +
        "  plan_id = ? " +
        "WHERE " +
        "  id = ? ";
     
     executeUpdate(sql, planId, userId);
  }
  
  protected void deleteOrderHistories() {
    executeUpdate("DELETE FROM OrderHistory");
  }
  
  private void executeUpdate(String sql, Object... params) {
    try (Connection connection = getConnection()) {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        for (int i = 0, l = params.length; i < l; i++) {
          statement.setObject(i + 1, params[i]);
        }

        statement.execute();
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to execute update", e);
      fail(e.getMessage());
    }
  }

  protected String getGoogleUserEmail() {
    return System.getenv("GOOGLE_USER_EMAIL");
  }

  protected String getGoogleUserPassword() {
    return System.getenv("GOOGLE_USER_PASSWORD");
  }

  protected String getGoogleUserFirstName() {
    return System.getenv("GOOGLE_USER_FIRST_NAME");
  }

  protected String getGoogleUserLastName() {
    return System.getenv("GOOGLE_USER_LAST_NAME");
  }

  protected boolean getCI() {
    return StringUtils.equals(System.getProperty("ci"), "true");
  }
  
  protected Connection getConnection() {
    String username = System.getProperty("it.jdbc.username");
    String password = System.getProperty("it.jdbc.password");
    String url = System.getProperty("it.jdbc.url");
    try {
      Class.forName(System.getProperty("it.jdbc.driver")).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      logger.log(Level.SEVERE, "Failed to load JDBC driver", e);
      fail(e.getMessage());
    }

    try {
      return DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "Failed to get connection", e);
      fail(e.getMessage());
    }
    
    return null;
  }
  
  private class TestBaseWatcher extends TestWatcher {
    
    @Override
    protected void failed(Throwable e, Description description) {
      try {
        savePageSource();
        takeScreenshot();
      } catch (WebDriverException | IOException e1) {
        logger.log(Level.SEVERE, "Screenshot failed", e1);
      }
    }
    
    @Override
    @SuppressWarnings ("squid:S1166")
    protected void finished(Description description) {
      try {
        getWebDriver().quit();
      } catch (Exception e) {
        // Ignore exceptions
      }
    }
    
  }
  
}
