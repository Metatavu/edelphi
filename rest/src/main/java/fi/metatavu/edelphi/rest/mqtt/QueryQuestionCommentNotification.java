package fi.metatavu.edelphi.rest.mqtt;

/**
 * MQTT message for query question comment update
 * 
 * @author Antti Leppä
 */
public class QueryQuestionCommentNotification {

  private Type type;
  private Long panelId;
  private Long queryId;
  private Long pageId;
  private Long commentId;
  private Long parentCommentId;

  /**
   * Constructor
   */
  public QueryQuestionCommentNotification() {
    // Zero-argument constructor
  }

  /**
   * Constructor
   * 
   * @param type type
   * @param panelId panel id
   * @param queryId query id
   * @param pageId page id
   * @param commentId comment id
   */
  public QueryQuestionCommentNotification(Type type, Long panelId, Long queryId, Long pageId, Long commentId, Long parentCommentId) {
    super();
    this.type = type;
    this.panelId = panelId;
    this.queryId = queryId;
    this.pageId = pageId;
    this.commentId = commentId;
    this.parentCommentId = parentCommentId;
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

  public Long getCommentId() {
    return commentId;
  }

  public void setCommentId(Long commentId) {
    this.commentId = commentId;
  }
  
  public Long getParentCommentId() {
    return parentCommentId;
  }
  
  public void setParentCommentId(Long parentCommentId) {
    this.parentCommentId = parentCommentId;
  }

  public enum Type {

    CREATED,

    UPDATED,

    DELETED

  }

}
