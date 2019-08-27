package fi.metatavu.edelphi.reports.charts;

/**
 * Scatter value
 * 
 * @author Antti Leppä
 */
public class ScatterValue {

  private Long replyId;
  private double x;
  private double y;

  /**
   * Constructor
   * 
   * @param x x
   * @param y y
   */
  public ScatterValue(Long replyId, double x, double y) {
    this.replyId = replyId;
    this.x = x;
    this.y = y;
  }
  
  /**
   * Returns reply id
   * 
   * @return reply id
   */
  public Long getReplyId() {
    return replyId;
  }

  /**
   * Returns x
   * 
   * @return x
   */
  public double getX() {
    return x;
  }

  /**
   * Returns y
   * 
   * @return y
   */
  public double getY() {
    return y;
  }

}
