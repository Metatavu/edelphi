package fi.metatavu.edelphi.settings;

/**
 * MQTT settings 
 * 
 * @author Antti Leppä
 */
public class MqttSettings {
  
  private String serverUrl;
  private String clientUrl;
  private String topic;
  private String wildcard;
  
  public String getServerUrl() {
    return serverUrl;
  }
  
  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }
  
  public String getClientUrl() {
    return clientUrl;
  }
  
  public void setClientUrl(String clientUrl) {
    this.clientUrl = clientUrl;
  }
  
  public String getTopic() {
    return topic;
  }
  
  public void setTopic(String topic) {
    this.topic = topic;
  }
  
  public String getWildcard() {
    return wildcard;
  }
  
  public void setWildcard(String wildcard) {
    this.wildcard = wildcard;
  }

}
