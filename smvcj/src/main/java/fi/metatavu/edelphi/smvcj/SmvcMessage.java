package fi.metatavu.edelphi.smvcj;

import java.io.Serializable;

/** An error or information message shown to the user.
 *
 */
public class SmvcMessage implements Serializable {
  
  private static final long serialVersionUID = 2738756334339660713L;
  
  private Severity severity;
  private String message;
  
  /** Creates a new message.
   * 
   * @param severity The severity class of the message.
   * @param message The body of the message.
   */
  public SmvcMessage(Severity severity, String message) {
    this.setSeverity(severity);
    this.setMessage(message);
  }

  /** Sets the severity class of the message.
   * 
   * @param severity The new severity class of the message.
   */
  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  /** Returns the severity class of the message.
   * 
   * @return The severity class of the message.
   */
  public Severity getSeverity() {
    return severity;
  }

  /** Sets the body of the message.
   * 
   * @param message The body of the message.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /** Returns the body of the message.
   * 
   * @return the body of the message.
   */
  public String getMessage() {
    return message;
  }

}
