package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;

/** The base class for page request controllers.
 * 
 */
public interface PageController extends RequestController {
  
  public void process(PageRequestContext pageRequestContext);

}
