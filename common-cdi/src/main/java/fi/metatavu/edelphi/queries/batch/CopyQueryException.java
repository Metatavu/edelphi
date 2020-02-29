package fi.metatavu.edelphi.queries.batch;

/**
 * Exception for copy query errors
 * 
 * @author Antti Leppä
 */
public class CopyQueryException extends Exception {
  
  private static final long serialVersionUID = -6379428861543047693L;

  /**
   * Constructor
   * 
   * @param message message
   */
  public CopyQueryException(String message) {
    super(message);
  }

}
