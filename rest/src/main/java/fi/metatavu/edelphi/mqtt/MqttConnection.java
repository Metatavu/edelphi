package fi.metatavu.edelphi.mqtt;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.settings.MqttSettings;

/**
 * MQTT client connection
 * 
 * @author Antti Leppä
 */
public class MqttConnection {
  
  private static String PUBLISHER_ID = UUID.randomUUID().toString();
  private static IMqttClient CLIENT = null;
  
  /**
   * Publishes data as JSON string into given topic
   * 
   * @param settings MQTT settings
   * @param subtopic subtopic to deliver the message to.
   * @param data data to be send
   * @throws JsonProcessingException thrown when data serialization fails
   * @throws MqttException thrown when MQTT client construction fails
   */
  public static void publish(MqttSettings settings, String subtopic, Object data) throws JsonProcessingException, MqttException {
    ObjectMapper objectMapper = new ObjectMapper();
    publish(settings, subtopic, objectMapper.writeValueAsBytes(data), 1, false);
  }
  
  /**
   * Publishes message into given MQTT topic
   * 
   * @param settings MQTT settings
   * @param subtopic subtopic to deliver the message to.
   * @param payload the byte array to use as the payload
   * @param qos the Quality of Service to deliver the message at. Valid values are 0, 1 or 2.
   * @param retained whether or not this message should be retained by the server.
   * @throws MqttException thrown when MQTT client construction fails
   */
  private static void publish(MqttSettings settings, String subtopic, byte[] payload, int qos, boolean retained) throws MqttException {
    if (settings != null) {
      getClient(settings).publish(String.format("%s/%s", settings.getTopic(), subtopic), payload, qos, retained);
    }   
  }
  
  /**
   * Returns client instance
   * 
   * @param settings settings
   * @return client instance
   * @throws MqttException thrown when MQTT client construction fails
   */
  private static synchronized IMqttClient getClient(MqttSettings settings) throws MqttException {
    if (CLIENT == null) {
      CLIENT = new MqttClient(settings.getServerUrl(), PUBLISHER_ID);
      MqttConnectOptions options = new MqttConnectOptions();
      options.setAutomaticReconnect(true);
      options.setCleanSession(true);
      options.setConnectionTimeout(10);
      options.setUserName(settings.getUsername());
      options.setPassword(settings.getPassword() != null ? settings.getPassword().toCharArray() : null);
      CLIENT.connect(options);
    }
    
    return CLIENT;
  }
  
}
