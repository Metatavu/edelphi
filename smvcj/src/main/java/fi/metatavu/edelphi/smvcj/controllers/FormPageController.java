package fi.metatavu.edelphi.smvcj.controllers;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

/** The base class for all page controllers containing regular (non-JSON) forms.
 * 
 *
 */
public abstract class FormPageController implements PageController {
  
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
  }

  public void process(PageRequestContext pageRequestContext) {
    if ("POST".equals(pageRequestContext.getRequest().getMethod())) {
      processSend(pageRequestContext);
    }
    else {
      processForm(pageRequestContext);
    }
  }
  
  public abstract void processForm(PageRequestContext requestContext);
  public abstract void processSend(PageRequestContext requestContext);
}
