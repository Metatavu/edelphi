package fi.metatavu.edelphi.test.mock;

import java.util.Map;

public class QueryPageMock {
  
  private Boolean visible;
  private String title;
  private String type;
  private Map<String, String> settings;
  private Map<String, SettingType> settingTypes;
  
  public Boolean getVisible() {
    return visible;
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public Map<String, String> getSettings() {
    return settings;
  }
  
  public void setSettings(Map<String, String> settings) {
    this.settings = settings;
  }
  
  public Map<String, SettingType> getSettingTypes() {
    return settingTypes;
  }
  
  public void setSettingTypes(Map<String, SettingType> settingTypes) {
    this.settingTypes = settingTypes;
  }
  
  public enum SettingType {
    
    TEXT,
    
    QUERY_PAGE_IDS_BY_TYPE
    
  }
  
}
