package fi.metatavu.edelphi.settings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.system.SettingDAO;
import fi.metatavu.edelphi.dao.system.SettingKeyDAO;
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
  private SettingKeyDAO settingKeyDAO; 
  
  @Inject
  private SettingDAO settingDAO; 
  
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
