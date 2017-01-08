package fi.metatavu.edelphi.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
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
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
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

  protected void assertLoginScreen() {
    waitAndAssertText(".errorPageDescriptionContainer", "You need to be logged in to access the requested page.");
  }
  
  protected void createPanel(String name) {
    createPanel(name, null);
  }  
  
  protected void createPanel(String name, String description) {
    navigate("/");
    waitAndClick(".createPanelBlockCreatePanelLink");
    waitAndClick(".createPanel_panelTypesContainer .createPanel_panelType:nth-child(1) .createPanel_panelTypeName");
    waitAndType("input[name='createPanel_panelName']", name);
    
    if (StringUtils.isNotBlank(description)) {
      waitAndType(".createPanel_panelDescription", description);
    }
    
    waitAndClick(".createPanel_donePageLink");
    waitNotVisible(".createPanelBlock_createPanelDialogOverlay");
  }
  
  protected void waitAndClick(String selector) {
    waitVisible(selector);
    click(selector);
  }
  
  @SuppressWarnings ("squid:S1166")
  protected int getElementWidth(String selector) {
    waitVisible(selector); 
    while (true) {
      try {
        List<WebElement> elements = findElements(selector);
        if (!elements.isEmpty()) {
          return elements.get(0).getSize().getWidth();
        }
      } catch (Exception e) {
        // Ignore
      }
    }
  }
  
  protected void clickOffset(String selector, int offsetX, int offsetY) {
    Actions build = new Actions(webDriver);
    build
      .moveToElement(findElements(selector).get(0), offsetX, offsetY)
      .click()
      .build()
      .perform();
  }
  
  protected void click(String selector) {
    findElements(selector).get(0).click();
  }

  protected void waitAndType(String selector, String text) {
    waitPresent(selector);
    findElements(selector).get(0).sendKeys(text);
  }
  
  protected void waitAndType(String selector, Keys... keys) {
    waitPresent(selector);
    findElements(selector).get(0).sendKeys(keys);
  }
  
  protected void waitPresent(final String... selectors) {
    Predicate<WebDriver> untilPredicate = driver -> !findElements(selectors).isEmpty();
    new WebDriverWait(getWebDriver(), 60).until(untilPredicate);
  }
  
  protected void waitNotPresent(final String... selectors) {
    Predicate<WebDriver> untilPredicate = driver -> findElements(selectors).isEmpty();
    new WebDriverWait(webDriver, 60).until(untilPredicate);
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void waitVisible(final String selector) {
    Predicate<WebDriver> untilPredicate = driver -> {
      try {
        List<WebElement> elements = findElementsBySelector(selector);
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
    };
      
    new WebDriverWait(getWebDriver(), 60).until(untilPredicate);
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void waitNotVisible(final String selector) {
    Predicate<WebDriver> untilPredicate = driver -> {
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
    };
      
    new WebDriverWait(getWebDriver(), 60).until(untilPredicate);
  }
  
  protected WebDriver createLocalDriver() {
    switch (getBrowser()) {
      case "chrome":
        return createChromeDriver();
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
  
  protected WebDriver createChromeDriver() {
    ChromeDriver driver = new ChromeDriver();
    driver.manage().window().setSize(new Dimension(1280, 1024));
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
    Predicate<WebDriver> untilPredicate = driver -> {
      int messageCount = getGreenMail().getReceivedMessages().length;
      return messageCount == expect;
    };
      
    new WebDriverWait(getWebDriver(), 60).until(untilPredicate);
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
    try {
      assertEquals(expected, getGreenMail().getReceivedMessages()[index].getContent());
    } catch (MessagingException | IOException e) {
      fail(e.getMessage());
    }
  }
  
  @SuppressWarnings ("squid:S1166")
  protected void assertReceivedEmailContentStartsWith(int index, String expected) {
    try {
      assertTrue(StringUtils.startsWith(String.valueOf(getGreenMail().getReceivedMessages()[index].getContent()), expected));
    } catch (MessagingException | IOException e) {
      fail(e.getMessage());
    }
  }
  
  protected void waitUrlMatches(final String regex) {
    new WebDriverWait(getWebDriver(), 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.getCurrentUrl().matches(regex);
      }
    });
  }
  
  protected void takeScreenshot() throws IOException {
    if (getCI()) {
      dumpScreenShot();
    } else {
      takeScreenshot("itests/target", UUID.randomUUID().toString() + ".png"); 
    }
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
      // Failed to write screenshot
    }

    dumpScreenShot();
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
  
  private void dumpScreenShot() {
    dumpScreenShot((TakesScreenshot) webDriver);
  }
  
  private void dumpScreenShot(TakesScreenshot takesScreenshot) {
    String imageData = toDataUrl("image/png", takesScreenshot.getScreenshotAs(OutputType.BYTES));
    logger.warning(String.format("Screenshot: %s", imageData));
  }
  
  private String toDataUrl(String contentType, byte[] data) {
    return String.format("data:%s;base64,%s", contentType, Base64.getEncoder().encodeToString(data));
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
