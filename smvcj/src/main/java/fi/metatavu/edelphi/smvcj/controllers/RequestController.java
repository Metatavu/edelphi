package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;

/** 
 * The interface implemented by all request controllers.
 */
public interface RequestController {

  void beforeProcess(RequestContext requestContext);

  void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException;

  /**
   * Returns true if the request should be processed synchronously.
   *
   * @return true if the request should be processed synchronously
   */
  boolean isSynchronous();

}
