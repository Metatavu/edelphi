package fi.metatavu.edelphi.invitations.batch;

/**
 * Exception for panel invitation errors
 * 
 * @author Antti Leppä
 */
public class PanelInvitationException extends Exception {
  
  private static final long serialVersionUID = -9056972068943069811L;

  /**
   * Constructor
   * 
   * @param message message
   */
  public PanelInvitationException(String message) {
    super(message);
  }

  /**
   * Constructor
   * 
   * @param cause cause
   */
  public PanelInvitationException(Throwable cause) {
    super(cause);
  }

}
