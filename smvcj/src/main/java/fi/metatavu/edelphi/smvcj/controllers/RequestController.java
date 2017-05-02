package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;

/** 
 * The interface implemented by all request controllers.
 */
public interface RequestController {

  public void beforeProcess(RequestContext requestContext);

  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException;

}
