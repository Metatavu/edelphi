package fi.metatavu.edelphi.reports;

/**
 * Exception for reports
 * 
 * @author Antti Leppä
 */
public class ReportException extends Exception {

  private static final long serialVersionUID = -3627918134438941303L;

  public ReportException(Exception cause) {
    super(cause);
  }
  
  public ReportException(String message) {
    super(message);
  }
  
}
