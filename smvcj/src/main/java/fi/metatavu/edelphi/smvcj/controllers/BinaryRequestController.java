package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;

/** Controllers responding to binary requests implement this interface.
 * 
 */
public interface BinaryRequestController extends RequestController {
  
  public void process(BinaryRequestContext binaryRequestContext);

}
