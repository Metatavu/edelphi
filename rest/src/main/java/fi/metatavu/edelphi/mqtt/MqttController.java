package fi.metatavu.edelphi.mqtt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.metatavu.edelphi.settings.MqttSettings;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Controller for MQTT
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MqttController {
  
  @Inject
  private Logger logger;

  @Inject
  private SettingsController settingsController;
  
  /**
   * Publishes data as JSON string into given topic
   * 
   * @param settings MQTT settings
   * @param subtopic subtopic to deliver the message to.
   * @param data data to be send
   */
  public void publish(String subtopic, Object data) {
    try {
      MqttSettings mqttSettings = settingsController.getMqttSettings();
      if (mqttSettings != null) {
        MqttConnection.publish(mqttSettings, subtopic, data);
      }
    } catch (JsonProcessingException | MqttException e) {
      logger.error("Failed to publish MQTT message", e);
    }
  }

}
