package fi.metatavu.edelphi.reports.charts;

/**
 * Scatter value
 * 
 * @author Antti Leppä
 */
public class ScatterValue {

  private double x;
  private double y;

  /**
   * Constructor
   * 
   * @param x x
   * @param y y
   */
  public ScatterValue(double x, double y) {
    this.x = x;
    this.y = y;
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

  /**
   * Sets x
   * 
   * @param x x
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets y
   * 
   * @param y y
   */
  public void setY(double y) {
    this.y = y;
  }

}
