package fi.metatavu.edelphi.reports.image;

/**
 * Rendered chart data 
 * 
 * @author Antti Leppä
 */
public class ChartData {

  private String contentType;
  private byte[] data;
  private String title;

  /**
   * Constructor
   * 
   * @param contentType content type
   * @param data data
   * @param title title
   */
  public ChartData(String contentType, byte[] data, String title) {
    this.contentType = contentType;
    this.data = data;
    this.title = title;
  }
  
  /**
   * Returns content type
   * 
   * @return content type
   */
  public String getContentType() {
    return contentType;
  }
  
  /**
   * Sets content type
   * 
   * @param contentType content type
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  
  /**
   * Returns data
   * 
   * @return data
   */
  public byte[] getData() {
    return data;
  }
  
  /**
   * Sets data
   * 
   * @param data data
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  /**
   * Returns title
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets title
   *
   * @param title title
   */
    public void setTitle(String title) {
      this.title = title;
    }

}
