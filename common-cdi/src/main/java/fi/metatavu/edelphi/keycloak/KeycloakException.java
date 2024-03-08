package fi.metatavu.edelphi.keycloak;

/**
 * Exception thrown about Keycloak communication errors
 * 
 * @author Antti Leppä
 */
public class KeycloakException extends Exception {
  
  private static final long serialVersionUID = 2385684017418729932L;

  public KeycloakException(Exception cause) {
    super(cause);
  }

  public KeycloakException(String message) {
    super(message);
  }

}
