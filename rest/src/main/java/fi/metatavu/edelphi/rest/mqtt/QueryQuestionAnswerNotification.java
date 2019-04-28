package fi.metatavu.edelphi.rest.mqtt;

/**
 * MQTT message for query question answer update
 * 
 * @author Antti Leppä
 */
public class QueryQuestionAnswerNotification {

  private Type type;
  private Long panelId;
  private Long queryId;
  private Long pageId;
  private String answerId;

  /**
   * Constructor
   */
  public QueryQuestionAnswerNotification() {
    // Zero-argument constructor
  }

  /**
   * Constructor
   * 
   * @param type type
   * @param panelId panel id
   * @param queryId query id
   * @param pageId page id
   * @param answerId answer id
   */
  public QueryQuestionAnswerNotification(Type type, Long panelId, Long queryId, Long pageId, String answerId) {
    super();
    this.type = type;
    this.panelId = panelId;
    this.queryId = queryId;
    this.pageId = pageId;
    this.answerId = answerId;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Long getPanelId() {
    return panelId;
  }

  public void setPanelId(Long panelId) {
    this.panelId = panelId;
  }

  public Long getQueryId() {
    return queryId;
  }

  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

  public Long getPageId() {
    return pageId;
  }

  public void setPageId(Long pageId) {
    this.pageId = pageId;
  }
  
  public String getAnswerId() {
    return answerId;
  }
  
  public void setAnswerId(String answerId) {
    this.answerId = answerId;
  }
  
  public enum Type {

    UPDATED

  }

}
