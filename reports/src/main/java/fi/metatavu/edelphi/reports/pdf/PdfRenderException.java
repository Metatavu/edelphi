package fi.metatavu.edelphi.reports.pdf;

/**
 * An exception for PDF renderer
 * 
 * @author Antti Leppä
 */
public class PdfRenderException extends Exception {

  private static final long serialVersionUID = 5306835263427986129L;

  public PdfRenderException(String message, Throwable original) {
    super(message, original);
  }
  
  public PdfRenderException(String message) {
    super(message);
  }
  
}