package fi.metatavu.edelphi.test.ui.local.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

public class Mockery {
  
  public static void main(String[] args) {
    startThem(1, 10);
  }
  
  private static void startThem(int userFrom, int userTo) {
    for (int i = 1, l = 20; i < l; i++) {
      RunMock runMock = new RunMock(i);
      
      new Thread(runMock).start();
    }
  }
  
  private static class RunMock implements Runnable {
    
    public RunMock(int user) {
      this.user = user;
    }

    @Override
    public void run() {
      new Mockery()
        .doAnswer(user);
    }
    
    private int user;
  }

  private void doAnswer(int user) {
    String username = String.format("live%d@example.com", user);
    String password = "q";
    
    RemoteWebDriver webDriver = createChromeDriver();//createPhantomJsDriver();
    try {
      webDriver.manage().window().maximize();
      webDriver.manage().window().setPosition(new Point(user * 20, user * 20));
      
      int answers = 0;
      
      webDriver.get("http://dev.edelphi.fi/live/kysely");
      waitAndType(webDriver, "input[name='username']", username);
      waitAndType(webDriver, "input[name='password']", password);
      click(webDriver, "input[name='login']");

      while (answers < 50) {
        webDriver.get("http://dev.edelphi.fi/live/kysely");

        for (int i = 0, l = 4; i < l; i++) {
          boolean lastPage = i == l - 1;
          
          answerPage(webDriver, lastPage ? "finish" : "next");
//          if (!lastPage) {
            waitNotPresent(webDriver, ".modalPopupGlassPane");
//          }
          
          try {
            Thread.sleep((long) ((300 * Math.random()) + 300));
          } catch (InterruptedException e) {
          }
          
          answers++;
        }
      }
    } finally {
      webDriver.close();
    }
  }
  
  protected RemoteWebDriver createChromeDriver() {
    return new ChromeDriver();
  }

  protected RemoteWebDriver createFirefoxDriver() {
    return new FirefoxDriver();
  }
  
  protected RemoteWebDriver createPhantomJsDriver() {
    DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
    
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, (new File("/Users/anttileppa/git/edelphi/itests/.phantomjs/bin/phantomjs")).getAbsolutePath());
    desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=true", "--webdriver-loglevel=NONE", "--load-images=false" } );
    PhantomJSDriver driver = new PhantomJSDriver(desiredCapabilities);
    driver.manage().window().setSize(new Dimension(1024, 768));
    return driver;
  }
  
  private void answerPage(RemoteWebDriver webDriver, String buttonName) {
    clickSlider(webDriver, ".queryQuestionContainer:nth-child(4) .queryScaleSliderTrack", (int) Math.round(Math.random() * 7));
    clickSlider(webDriver, ".queryQuestionContainer:nth-child(5) .queryScaleSliderTrack", (int) Math.round(Math.random() * 7));
  
    waitAndClick(webDriver, String.format("input[name='%s']", buttonName));
  }
  
  private void waitAndClick(RemoteWebDriver webDriver, String selector) {
    waitVisible(webDriver, selector);
    click(webDriver, selector);
  }

  private void clickSlider(RemoteWebDriver webDriver, String selector, int value) {
    int width = getElementWidth(webDriver, selector);
    clickOffset(webDriver, selector, width / 7 * value, 0);
  }
  
  private int getElementWidth(RemoteWebDriver webDriver, String selector) {
    waitVisible(webDriver, selector); 
    while (true) {
      try {
        List<WebElement> elements = findElements(webDriver, selector);
        if (!elements.isEmpty()) {
          return elements.get(0).getSize().getWidth();
        }
      } catch (Exception e) {
        
      }
    }
  }
  
  private void clickOffset(RemoteWebDriver webDriver, String selector, int offsetX, int offsetY) {
    Actions build = new Actions(webDriver);
    build
      .moveToElement(findElements(webDriver, selector).get(0), offsetX, offsetY)
      .click()
      .build()
      .perform();
  }
  
  private void click(RemoteWebDriver webDriver, String selector) {
    findElements(webDriver, selector).get(0).click();
  }

  protected void waitAndType(RemoteWebDriver webDriver, String selector, String text) {
    waitPresent(webDriver, selector);
    findElements(webDriver, selector).get(0).sendKeys(text);
  }
  
  protected void waitPresent(RemoteWebDriver webDriver, final String... selectors) {
    Predicate<WebDriver> untilPredicate = driver -> !findElements(webDriver, selectors).isEmpty();
    new WebDriverWait(webDriver, 60).until(untilPredicate);
  }
  
  protected void waitNotPresent(RemoteWebDriver webDriver, final String... selectors) {
    Predicate<WebDriver> untilPredicate = driver -> findElements(webDriver, selectors).isEmpty();
    new WebDriverWait(webDriver, 60).until(untilPredicate);
  }
  
  protected void waitVisible(RemoteWebDriver webDriver, final String selector) {
    new WebDriverWait(webDriver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = findElementsBySelector(webDriver, selector);
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
  
  protected void waitNotVisible(RemoteWebDriver webDriver, final String selector) {
    new WebDriverWait(webDriver, 60).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          List<WebElement> elements = findElementsBySelector(webDriver, selector);
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
  
  protected List<WebElement> findElements(RemoteWebDriver webDriver, String... selectors) {
    List<WebElement> result = new ArrayList<>();

    for (String selector : selectors) {
      result.addAll(findElementsBySelector(webDriver, selector));
    }
    
    return result;
  }
  
  @SuppressWarnings ("squid:S1166")
  private List<WebElement> findElementsBySelector(RemoteWebDriver webDriver, String selector) {
    try {
      return ((FindsByCssSelector) webDriver).findElementsByCssSelector(selector);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }
  
}
