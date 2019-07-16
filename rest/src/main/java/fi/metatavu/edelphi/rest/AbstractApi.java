package fi.metatavu.edelphi.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.rest.model.ErrorResponse;
import fi.metatavu.edelphi.users.UserController;

/**
 * Abstract base class for all API services
 * 
 * @author Antti Leppä
 */
public abstract class AbstractApi {
  
  protected static final String NOT_FOUND_MESSAGE = "Not found";
  protected static final String GONE_MESSAGE = "Gone";

  @Inject
  private Logger logger;

  @Inject
  private UserController userController;
  
  /**
   * Returns whether system is running in test mode
   * 
   * @return whether system is running in test mode
   */
  protected boolean inTestMode() {
    return true; // TODO
  }
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @return response
   */
  protected Response createOk(Object entity) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs not found response
   * 
   * @return response
   */
  protected Response createNotFound() {
    return createNotFound(NOT_FOUND_MESSAGE);
  }
  
  /**
   * Constructs not found response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotFound(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.NOT_FOUND)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs not found response
   * 
   * @return response
   */
  protected Response createGone() {
    return createNotFound(NOT_FOUND_MESSAGE);
  }
  
  /**
   * Constructs not found response
   * 
   * @param message message
   * @return response
   */
  protected Response createGone(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.GONE)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs forbidden response
   * 
   * @param message message
   * @return response
   */
  protected Response createForbidden(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.FORBIDDEN)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs bad request response
   * 
   * @param message message
   * @return response
   */
  protected Response createBadRequest(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.BAD_REQUEST)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs internal server error response
   * 
   * @param message message
   * @return response
   */
  protected Response createInternalServerError(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(entity)
      .build();
  }
  
  /**
   * Constructs not implemented response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotImplemented(String message) {
    ErrorResponse entity = new ErrorResponse();
    entity.setMessage(message);
    return Response
      .status(501)
      .entity(entity)
      .build();
  }
  
  
  /**
   * Constructs ok response
   * 
   * @param entity payload
   * @param totalHits total hits
   * @return response
   */
  protected Response createOk(Object entity, Long totalHits) {
    return Response
      .status(Response.Status.OK)
      .entity(entity)
      .header("Total-Results", totalHits)
      .build();
  }
  
  /**
   * Constructs no content response
   * 
   * @return response
   */
  protected Response createNoContent() {
    return Response
      .status(Response.Status.NO_CONTENT)
      .build();
  }
  
  /**
   * Creates streamed response from byte array
   * 
   * @param data data
   * @param type content type
   * @return Response
   */
  protected Response streamResponse(byte[] data, String type) {
    try (InputStream byteStream = new ByteArrayInputStream(data)) {
      return streamResponse(type, byteStream, data.length);
    } catch (IOException e) {
      logger.error("Failed to stream data to client", e);
      return createInternalServerError("Failed to stream data to client");
    }
  }
  
  /**
   * Creates streamed response from input stream
   * 
   * @param inputStream data
   * @param type content type
   * @param contentLength content length
   * @return Response
   */
  protected Response streamResponse(String type, InputStream inputStream, int contentLength) {
    return Response.ok(new StreamingOutputImpl(inputStream), type)
      .header("Content-Length", contentLength)
      .build();
  }
  
  /**
   * Returns logged user id
   * 
   * @return logged user id
   */
  protected UUID getLoggedUserId() {
    HttpServletRequest httpServletRequest = getHttpServletRequest();
    String remoteUser = httpServletRequest.getRemoteUser();
    if (remoteUser == null) {
      return null;
    }
    
    return UUID.fromString(remoteUser);
  }
  
  /**
   * Returns logged user
   * 
   * @return logged user or null if user is not logged in
   */
  protected User getLoggedUser() {
    UUID userId = getLoggedUserId();
    if (userId == null) {
      return null;
    }
    
    return userController.findUserByKeycloakId(userId);
  }
  
  /**
   * Returns request locale
   * 
   * @return request locale
   */
  protected Locale getLocale() {
    return getHttpServletRequest().getLocale();
  }
  
  /**
   * Return current HttpServletRequest
   * 
   * @return current http servlet request
   */
  protected HttpServletRequest getHttpServletRequest() {
    return ResteasyProviderFactory.getContextData(HttpServletRequest.class);
  }
  
  /**
   * Returns service base URL 
   * @return service base URL
   */
  protected String getBaseUrl() {
    HttpServletRequest request = getHttpServletRequest();
    String currentURL = request.getRequestURL().toString();
    String pathInfo = request.getRequestURI();
    return currentURL.substring(0, currentURL.length() - pathInfo.length()) + request.getContextPath();
  }
  
}
