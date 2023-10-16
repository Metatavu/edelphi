package fi.metatavu.edelphi.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.ActionedController;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.FeatureNotAvailableOnSubscriptionLevelException;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.SmvcMessage;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SessionUtils;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public abstract class PageController implements fi.metatavu.edelphi.smvcj.controllers.PageController, ActionedController {

  private DelfoiActionName accessActionName = null;
  private DelfoiActionScope accessActionScope;
  
  /**
   * Returns a feature this page belongs to
   * 
   * @return a feature this page belongs to
   */
  public abstract Feature getFeature();

  @Override
  public void beforeProcess(RequestContext requestContext) {
    if (requestContext.isLoggedIn()) {
      boolean hasImportantBulletins = SessionUtils.hasImportantBulletins(requestContext.getRequest().getSession(false));
      requestContext.getRequest().setAttribute("hasImportantBulletins", hasImportantBulletins);
    }
    
  }

  @Override
  public void authorize(RequestContext requestContext) {
    String actionAccessName = getAccessActionName() == null ? null : getAccessActionName().toString();
    DelfoiActionScope actionAccessScope = getAccessActionScope();
    
    if (actionAccessName != null) {
      switch(actionAccessScope) {
        case DELFOI:
          authorizeDelfoi(requestContext, actionAccessName);
        break;
        case PANEL:
          authorizePanel(requestContext, actionAccessName);
        break;
        default:
        break;
      }
    }
    
    if (requestContext.isLoggedIn()) {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.findById(requestContext.getLoggedUserId());
      if (user != null) {
        Feature feature = getFeature();
        
        if (!SubscriptionLevelUtils.isFeatureEnabled(user.getSubscriptionLevel(), feature)) {
          SubscriptionLevel minimumSubscriptionLevel = SubscriptionLevelUtils.getMinimumLevelFor(feature);
          throw new FeatureNotAvailableOnSubscriptionLevelException(requestContext.getRequest().getLocale(), user.getSubscriptionLevel(), minimumSubscriptionLevel); 
        }
      }
    }
    
  }

  private void authorizePanel(RequestContext requestContext, String actionAccessName) {
    Panel panel = RequestUtils.getPanel(requestContext);
    if (panel == null)
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
      
    if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName)) {
      if (!requestContext.isLoggedIn()) {
        throw new LoginRequiredException(RequestUtils.getCurrentUrl(requestContext.getRequest(), true), "PANEL", panel.getId() + "");
      }
      else {
        throw new AccessDeniedException(requestContext.getRequest().getLocale());
      }
    }
  }

  private void authorizeDelfoi(RequestContext requestContext, String actionAccessName) {
    Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
    if (delfoi == null)
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    
    if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName)) {
      if (!requestContext.isLoggedIn()) {
        throw new LoginRequiredException(RequestUtils.getCurrentUrl(requestContext.getRequest(), true), "DELFOI", delfoi.getId() + "");
      }
      else {
        throw new AccessDeniedException(requestContext.getRequest().getLocale());
      }
    }
  }

  protected void authorizePanel(RequestContext requestContext, Panel panel, String actionAccessName) {
    if (panel == null)
      throw new IllegalStateException("PageController panel action without panel");
      
    if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName)) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }

  protected void authorizeDelfoi(RequestContext requestContext, Delfoi delfoi, String actionAccessName) {
    if (delfoi == null)
      throw new IllegalStateException("PageController Delfoi action without Delfoi");
    
    if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName)) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }
  
  @Override
  public void process(PageRequestContext pageRequestContext) {
    List<SmvcMessage> messages = RequestUtils.retrieveRedirectMessages(pageRequestContext);
    if (messages != null) {
      for (SmvcMessage message : messages) {
        pageRequestContext.addMessage(message);
      }
    }
  }
  
  protected void setJsDataVariable(PageRequestContext pageRequestContext, String name, String value) {
    @SuppressWarnings("unchecked")
    Map<String, String> jsData = (Map<String, String>) pageRequestContext.getRequest().getAttribute("jsData");
    if (jsData == null) {
      jsData = new HashMap<>();
      pageRequestContext.getRequest().setAttribute("jsData", jsData);
    }
    
    jsData.put(name, value);
  }
  
  protected void setAccessAction(DelfoiActionName actionName, DelfoiActionScope actionScope) {
    this.accessActionName = actionName;
    this.accessActionScope = actionScope;
  }
  
  @Override
  public DelfoiActionName getAccessActionName() {
    return accessActionName;
  }
  
  @Override
  public DelfoiActionScope getAccessActionScope() {
    return accessActionScope;
  }

  @Override
  public boolean isSynchronous() {
    return false;
  }

}
