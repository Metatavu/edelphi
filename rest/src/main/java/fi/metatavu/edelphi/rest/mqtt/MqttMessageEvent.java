package fi.metatavu.edelphi.rest.mqtt;

/**
 * CDI event class used for sending MQTT messages
 */
public class MqttMessageEvent {

  private String subtopic;
  private Object data;

  /**
   * Constructor
   *
   * @param subtopic MQTT subtopic
   * @param data message data
   */
  public MqttMessageEvent(String subtopic, Object data) {
    this.subtopic = subtopic;
    this.data = data;
  }

  /**
   * Returns message data
   *
   * @return message data
   */
  public Object getData() {
    return data;
  }

  /**
   * Returns MQTT subtopic
   *
   * @return MQTT subtopic
   */
  public String getSubtopic() {
    return subtopic;
  }

}
