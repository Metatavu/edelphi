package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;

/**
 * Interface to define common functionality to all JSON request handlers. 
 * 
 * @author antti.leppä
 */
public interface JSONRequestController extends RequestController {

  /**
   * Method to process the JSON request.
   * 
   * @param jsonRequestContext JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext);

}
