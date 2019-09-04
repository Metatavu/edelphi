package fi.metatavu.edelphi.utils;

import java.util.HashMap;
import java.util.Set;

import fi.metatavu.edelphi.dao.system.SettingDAO;
import fi.metatavu.edelphi.dao.system.SettingKeyDAO;
import fi.metatavu.edelphi.domainmodel.system.Setting;
import fi.metatavu.edelphi.domainmodel.system.SettingKey;

public class SystemUtils {
  
  public static final int MAX_QUERY_PAGE_TITLE = 192;
  public static final int MAX_QUERY_FIELD_CAPTION = 192;
  
  private static final String SYSTEM_ENVIRONMENT = "system.environment";
  private static final String ENV_PRODUCTION = "production";
  private static final String ENV_DEVELOPMENT = "development";
  private static final String ENV_TEST = "test";
  
  private SystemUtils() {
  }
  
  public static boolean isProductionEnvironment() {
    return ENV_PRODUCTION.equals(getSettingValue(SYSTEM_ENVIRONMENT));
  }

  public static boolean isDevelopmentEnvironment() {
    return ENV_DEVELOPMENT.equals(getSettingValue(SYSTEM_ENVIRONMENT));
  }
  
  public static boolean isTestEnvironment() {
    return ENV_TEST.equals(getSettingValue(SYSTEM_ENVIRONMENT));
  }
  
  public static String getSettingValue(String name) {
    SettingKeyDAO settingKeyDAO = new SettingKeyDAO();
    SettingDAO settingDAO = new SettingDAO();
    
    SettingKey settingKey = settingKeyDAO.findByName(name);
    if (settingKey != null) {
      Setting setting = settingDAO.findByKey(settingKey);
      if (setting != null)
        return setting.getValue();
    }
    
    return null;
  }

  public static void startMethod(String name) {
    lastStart.put(name, System.currentTimeMillis());
  }
  
  public static void endMethod(String name) {
    Long end = System.currentTimeMillis();
    Long start = lastStart.get(name);
    Long time = end - start;
    Integer count = methodCount.get(name);
    if (count == null) {
      count = 1;
    }
    else {
      count++;
    }
    methodCount.put(name, count);
    Long cumulativeTime = methodMs.get(name);
    if (cumulativeTime == null) {
      cumulativeTime = time;
    }
    else {
      cumulativeTime += time;
    }
    methodMs.put(name, cumulativeTime);
  }
  
  public static void printSumary() {
    printSummary(true);
  }
  
  public static void printSummary(boolean reset) {
    Set<String> methods = methodMs.keySet();
    for (String method : methods) {
      System.out.print(method);
      System.out.print(" ");
      System.out.print(methodCount.get(method));
      System.out.print(" calls, ");
      System.out.print(methodMs.get(method));
      System.out.print("ms total ");
      System.out.print((methodMs.get(method) / methodCount.get(method)));
      System.out.println("ms avg");
    }
    if (reset) {
      lastStart.clear();
      methodMs.clear();
      methodCount.clear();
    }
  }
  
  private static HashMap<String, Long> lastStart = new HashMap<String,Long>();
  private static HashMap<String, Long> methodMs = new HashMap<String,Long>();
  private static HashMap<String, Integer> methodCount = new HashMap<String,Integer>();
  
}
 