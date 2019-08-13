package fi.metatavu.edelphi.queries;

/**
 * Answer data implementation for Live 2d queries
 * 
 * @author Antti Leppä
 */
public class QueryQuestionLive2dAnswerData implements QueryQuestionAnswerData {

  private Double x;
  private Double y;

  /**
   * Constructor 
   * 
   * @param x x
   * @param y y
   */
  public QueryQuestionLive2dAnswerData(Double x, Double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public Double getX() {
    return x;
  }
  
  public void setX(Double x) {
    this.x = x;
  }
  
  public Double getY() {
    return y;
  }
  
  public void setY(Double y) {
    this.y = y;
  }

}
