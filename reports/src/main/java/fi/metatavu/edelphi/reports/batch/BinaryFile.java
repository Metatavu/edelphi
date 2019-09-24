package fi.metatavu.edelphi.reports.batch;

/**
 * Binary file for batch operations
 * 
 * @author Antti Leppä
 */
public class BinaryFile {

  private String name;
  private byte[] data;
  private String contentType;
  
  /**
   * Constuctor
   */
  public BinaryFile() {
    // Zero argument constructor
  }

  /**
   * Constructor 
   * 
   * @param name file name
   * @param data file data
   */
  public BinaryFile(String name, String contentType, byte[] data) {
    super();
    this.name = name;
    this.contentType = contentType;
    this.data = data;
  }
  
  /**
   * Returns file content type
   * 
   * @return file content type
   */
  public String getContentType() {
    return contentType;
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
   * Returns file name
   * 
   * @return file name
   */
  public String getName() {
    return name;
  }

}
