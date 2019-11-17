package fi.metatavu.edelphi.rest.test.functional.settings;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utility class for retrieving functional test settings
 * 
 * @author Antti Leppä
 */
public class TestSettings {
  
  private TestSettings() {
    // Zero-argument constructor
  }

  /**
   * Returns host where the application being tested is running 
   * 
   * @return host where the application being tested is running
   */
  public static String getHost() {
    return System.getProperty("it.host");
  }  

  /**
   * Returns port where the application being tested is running 
   * 
   * @return port where the application being tested is running
   */
  public static Integer getPort() {
    return NumberUtils.createInteger(System.getProperty("it.port.http"));
  }
  
}
