package fi.metatavu.edelphi.settings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.metatavu.edelphi.dao.system.SettingDAO;
import fi.metatavu.edelphi.dao.system.SettingKeyDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.system.Setting;
import fi.metatavu.edelphi.domainmodel.system.SettingKey;

/**
 * Controller for settings
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SettingsController {

  private static final long DELFOI_ID = 1l;
  
  @Inject
  private DelfoiDAO delfoiDAO;
  
  @Inject
  private SettingKeyDAO settingKeyDAO; 
  
  @Inject
  private SettingDAO settingDAO; 

  @Inject
  private DelfoiDefaultsDAO delfoiDefaultsDAO;
  
  /**
   * Returns used theme version
   * 
   * @return used theme version
   */
  public String getThemeVersion() {
    return "v2";
  }
  
  /**
   * Returns theme folder URL
   * 
   * @return theme folder URL
   */
  public String getThemeUrl() {
    return String.format("%s/assets/edelphi/_themes/%s", getCdnUrl(), getThemeVersion());
  }
    
  /**
   * Returns content delivery network URL address 
   * 
   * @return content delivery network URL address 
   */
  public String getCdnUrl() {
    String result = System.getenv("CDN_URL");
    if (StringUtils.isNotBlank(result)) {
      return result;
    }
    
    return "https://cdn.metatavu.io";
  }
  
  /**
   * Returns MQTT settings
   * 
   * @return MQTT settings
   */
  public MqttSettings getMqttSettings() {
    String serverUrl = getSettingValue("mqtt.serverUrl");
    if (StringUtils.isBlank(serverUrl)) {
      return null;
    }
    
    MqttSettings settings = new MqttSettings();
    settings.setServerUrl(serverUrl);
    settings.setClientUrl(getSettingValue("mqtt.clientUrl"));    
    settings.setTopic(getSettingValue("mqtt.topic"));
    settings.setWildcard(getSettingValue("mqtt.wildcard"));
    settings.setUsername(getSettingValue("mqtt.username"));
    settings.setPassword(getSettingValue("mqtt.password"));
    return settings;
  }
  
  /**
   * Returns internal authorization hash value
   * 
   * @return internal authorization hash value
   */
  public String getInternalAuthorizationHash() {
    return getSettingValue("system.internalAuthorizationHash");
  }
  
  /**
   * Returns delfoi
   * 
   * @return delfoi
   */
  public Delfoi getDelfoi() {
    return delfoiDAO.findById(getDelfoiId());
  }

  /**
   * Finds defaults by delfoi
   * 
   * @param delfoi delfoi
   * @return defaults
   */
  public DelfoiDefaults getDefaults(Delfoi delfoi) {
    return delfoiDefaultsDAO.findByDelfoi(delfoi);
  }
  
  /**
   * Returns delfoi id
   * 
   * @return delfoi id
   */
  public long getDelfoiId() {
    return DELFOI_ID;
  }
  
  /**
   * Returns whether system is running in test mode
   * 
   * @return whether system is running in test mode
   */
  public boolean isInTestMode() {
    return "TEST".equals(getRunMode());
  }

  /**
   * Returns system's current run mode
   * 
   * @return system's current run mode
   */
  public String getRunMode() {
    String result = System.getProperty("runmode");
    if (StringUtils.isNotBlank(result)) {
      return result;
    }
    
    return System.getenv("runmode");
  }
  
  /**
   * Returns system email from address
   * 
   * @return system email from address
   */
  public String getEmailFromAddress() {
    String result = System.getenv("EMAIL_FROM_ADDRESS");
    if (StringUtils.isNotBlank(result)) {
      return result;
    }
    
    return "noreply@edelphi.org";
  }
  
  /**
   * Returns a setting value for given key
   * 
   * @param key key
   * @return setting value
   */
  private String getSettingValue(String key) {
    SettingKey settingKey = settingKeyDAO.findByName(key);
    if (settingKey == null) {
      return null;
    }
    
    Setting setting = settingDAO.findByKey(settingKey);
    return setting != null ? setting.getValue() : null;
  }
}
