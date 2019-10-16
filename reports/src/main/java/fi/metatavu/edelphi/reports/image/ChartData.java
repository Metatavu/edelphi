package fi.metatavu.edelphi.reports.image;

/**
 * Rendered chart data 
 * 
 * @author Antti Leppä
 */
public class ChartData {

  private String contentType;
  private byte[] data;
  
  /**
   * Constructor
   * 
   * @param contentType content type
   * @param data data
   */
  public ChartData(String contentType, byte[] data) {
    this.contentType = contentType;
    this.data = data;
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
  
}
