package fi.metatavu.edelphi.jsons;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.ActionedController;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public abstract class JSONController implements JSONRequestController, ActionedController {

  @Override
  public void beforeProcess(RequestContext requestContext) {
      
  }

  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    String actionAccessName = getAccessActionName() == null ? null : getAccessActionName().toString();
    DelfoiActionScope actionAccessScope = getAccessActionScope();
    
    if (actionAccessName != null) {
      switch(actionAccessScope) {
        case DELFOI:
          Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
          if (delfoi == null)
            throw new AccessDeniedException(requestContext.getRequest().getLocale());
          
          if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName)) {
            if (!requestContext.isLoggedIn()) {
              Messages messages = Messages.getInstance();
              Locale locale = requestContext.getRequest().getLocale();
              String url = requestContext.getReferer(true);
              String link = "<a href=\"" + url + "\">" + messages.getText(locale, "exception.1040.sessionTimeout.link") + "</a>";
              throw new SmvcRuntimeException(EdelfoiStatusCode.SESSION_TIMEOUT, messages.getText(locale, "exception.1040.sessionTimeout.text", new String[] { link }));
            }
            else {
              throw new AccessDeniedException(requestContext.getRequest().getLocale());
            }
          }
        break;
        
        case PANEL:
          Panel panel = RequestUtils.getPanel(requestContext);
          if (panel == null)
            throw new AccessDeniedException(requestContext.getRequest().getLocale());
            
          if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName)) {
            if (!requestContext.isLoggedIn()) {
              Messages messages = Messages.getInstance();
              Locale locale = requestContext.getRequest().getLocale();
              String url = requestContext.getReferer(true);
              String link = "<a href=\"" + url + "\">" + messages.getText(locale, "exception.1040.sessionTimeout.link") + "</a>";
              throw new SmvcRuntimeException(EdelfoiStatusCode.SESSION_TIMEOUT, messages.getText(locale, "exception.1040.sessionTimeout.text", new String[] { link }));
            }
            else {
              throw new AccessDeniedException(requestContext.getRequest().getLocale());
            }
          }
        break;
      }
    }
  }

  protected void authorizePanel(RequestContext requestContext, Panel panel, String actionAccessName) {
    if (panel == null)
      throw new IllegalStateException("JSONController panel action without panel");
      
    if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }

  protected void authorizeDelfoi(RequestContext requestContext, Delfoi delfoi, String actionAccessName) {
    if (delfoi == null)
      throw new IllegalStateException("JSONController Delfoi action without Delfoi");
    
    if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }
  
  protected void setAccessAction(DelfoiActionName accessActionName, DelfoiActionScope actionScope) {
    this.accessActionName = accessActionName;
    this.accessActionScope = actionScope;
  }
  
  public DelfoiActionName getAccessActionName() {
    return accessActionName;
  }
  
  public DelfoiActionScope getAccessActionScope() {
    return accessActionScope;
  }
  
  private DelfoiActionName accessActionName = null;
  private DelfoiActionScope accessActionScope;
}
