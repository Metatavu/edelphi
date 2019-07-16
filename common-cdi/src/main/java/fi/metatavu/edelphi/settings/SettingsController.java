package fi.metatavu.edelphi.settings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    MqttSettings settings = new MqttSettings();
    settings.setServerUrl(getSettingValue("mqtt.serverUrl"));
    settings.setClientUrl(getSettingValue("mqtt.clientUrl"));    
    settings.setTopic(getSettingValue("mqtt.topic"));
    settings.setWildcard(getSettingValue("mqtt.wildcard"));
    settings.setUsername(getSettingValue("mqtt.username"));
    settings.setPassword(getSettingValue("mqtt.password"));
    return settings;
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
