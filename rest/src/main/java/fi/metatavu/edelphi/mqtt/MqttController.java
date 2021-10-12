package fi.metatavu.edelphi.mqtt;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import fi.metatavu.edelphi.rest.mqtt.MqttMessageEvent;
import fi.metatavu.edelphi.settings.MqttSettings;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

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

  @Inject
  private Event<MqttMessageEvent> messageEvent;

  private MqttSettings settings;

  /**
   * Post construct method
   */
  @PostConstruct
  public void init() {
    settings = settingsController.getMqttSettings();
  }
  
  /**
   * Publishes data as JSON string into given topic
   *
   * @param subtopic subtopic to deliver the message to.
   * @param data data to be send
   */
  public void publish(String subtopic, Object data) {
    messageEvent.fire(new MqttMessageEvent(subtopic, data));
  }

  /**
   * Event handler for publishing mqtt messages on transaction end
   *
   * @param event event
   */
  public void onMessageEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) MqttMessageEvent event) {
    try {
      MqttConnection.publish(settings, event.getSubtopic(), event.getData());
    } catch (JsonProcessingException | MqttException e) {
      logger.error("Failed to publish MQTT message", e);
    }
  }

}
