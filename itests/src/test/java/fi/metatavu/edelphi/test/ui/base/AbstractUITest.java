package fi.metatavu.edelphi.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

public class AbstractUITest {
  
  private Logger logger = Logger.getLogger(AbstractUITest.class.getName());
  
  private WebDriver webDriver;
  
  @Rule
  public TestWatcher testWatcher = new TestBaseWatcher();
  
  public void navigate(String path) {
    webDriver.get(String.format("%s%s", getAppUrl(), path));
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

  protected WebDriver createChromeDriver() {
    return new ChromeDriver();
  }

  protected WebDriver createFirefoxDriver() {
    return new FirefoxDriver();
  }
  
  protected WebDriver createPhantomJsDriver() {
    DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, ".phantomjs/bin/phantomjs");
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=true", "--webdriver-loglevel=NONE", "--load-images=false" } );
    PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
    driver.manage().window().setSize(new Dimension(1024, 768));
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
  }
  
  protected WebElement findElement(String selector) {
    List<WebElement> elements = findElements(selector);
    if (!elements.isEmpty()) {
      return elements.get(0);
    }
    
    return null;
  }
  
  protected void waitPresent(final String... selectors) {
    Predicate<WebDriver> untilPredicate = driver -> !findElements(selectors).isEmpty();
    new WebDriverWait(getWebDriver(), 60).until(untilPredicate);
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
  
  protected void takeScreenshot() throws IOException {
    takeScreenshot(new File("target", UUID.randomUUID().toString() + ".png"));
  }
  
  protected void takeScreenshot(File file) throws IOException {
    TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
    
    if (getCI()) {
      String imageData = toDataUrl("image/png", takesScreenshot.getScreenshotAs(OutputType.BYTES));
      logger.warning(String.format("Screenshot: %s: %s", file.getName(), imageData));
    } else {    
    FileOutputStream fileOuputStream = new FileOutputStream(file);
      try {
       fileOuputStream.write(takesScreenshot.getScreenshotAs(OutputType.BYTES));
      } finally {
        fileOuputStream.flush();
        fileOuputStream.close();
      }
    }
  }
  
  private String toDataUrl(String contentType, byte[] data) {
    return String.format("data:%s;base64,%s", contentType, Base64.getEncoder().encodeToString(data));
  }
  
  protected long createFolder(String name, String urlName, Long parentFolderId) {
    String description = null;
    boolean visible = true;
    boolean archived = false;
    Date created = new Date();
    Long lastModifierId = 1l;
    Date lastModified = new Date();
    return createFolder(name, urlName, description, parentFolderId, visible, archived, created, lastModifierId, lastModified);
  }
  
  @SuppressWarnings ("squid:S00107")
  protected long createFolder(String name, String urlName, String description, Long parentFolderId, boolean visible, boolean archived, Date created, Long lastModifierId, Date lastModified) {
    long id = createResource(name, urlName, description, parentFolderId, "FOLDER", visible, archived, created, lastModifierId, lastModified);
    executeInsert("insert into Folder (id) values (?)", id);
    return id;
  }
  
  @SuppressWarnings ("squid:S00107")
  private long createResource(String name, String urlName, String description, Long parentFolderId, String type, boolean visible, boolean archived, Date created, Long lastModifierId, Date lastModified) {
    String sql = 
        "insert into " +
        "  Resource (name, urlName, description, parentFolder_id, type, visible, archived, created, lastModifier_id, lastModified) " +
        "values " +
        "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return executeInsert(sql, name, urlName, description, parentFolderId, type, visible, archived, lastModifierId, lastModified);
  }
  
  private long executeInsert(String sql, Object... params) {
    try (Connection connection = getConnection()) {
      connection.setAutoCommit(true);
      PreparedStatement statement = connection.prepareStatement(sql);
      try {
        for (int i = 0, l = params.length; i < l; i++) {
          statement.setObject(i + 1, params[i]);
        }

        statement.execute();
        
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          return getGeneratedKey(generatedKeys);
        }
      } finally {
        statement.close();
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to execute insert", e);
      fail(e.getMessage());
    }
    
    return -1;
  }
  
  protected boolean getCI() {
    return StringUtils.equals(System.getProperty("ci"), "true");
  }
  
  private Connection getConnection() {
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
  
  private long getGeneratedKey(ResultSet generatedKeys) throws SQLException {
    if (generatedKeys.next()) {
      return generatedKeys.getLong(1);
    }
    
    return -1;
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
