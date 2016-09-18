package fi.metatavu.edelphi.pages.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.StatusCode;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;
import fi.metatavu.edelphi.ActionedController;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.pages.PageController;

public class LoadActionsPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String webappPath = System.getProperty("appdirectory");

    Properties properties = new Properties();

    File settingsFile = new File(webappPath + "WEB-INF/classes/edelphiactions.properties");
    if (settingsFile.canRead()) {
      try {
        properties.load(new FileReader(settingsFile));
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    Enumeration<Object> keys = properties.keys();
    
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = properties.getProperty(key);
      
      DelfoiAction delfoiAction = delfoiActionDAO.findByActionName(key);
      DelfoiActionScope scope = DelfoiActionScope.valueOf(value); 
      
      if (delfoiAction == null)
        delfoiActionDAO.create(key, scope);
    }

    // Read Actions bound to controllers
    
    Map<String, RequestController> controllers = RequestControllerMapper.getControllers();
    Set<String> controllerKeys = controllers.keySet();
    for (String key : controllerKeys) {
      RequestController controller = controllers.get(key);
      
      if (controller instanceof ActionedController) {
        ActionedController ac = (ActionedController) controller;
        String actionName = ac.getAccessActionName() == null ? null : ac.getAccessActionName().toString();
        if (actionName != null) {
          DelfoiAction delfoiAction = delfoiActionDAO.findByActionName(actionName);
          DelfoiActionScope actionScope = ac.getAccessActionScope();
          
          if (delfoiAction == null) {
            delfoiActionDAO.create(actionName, actionScope);
          } else {
            if (!delfoiAction.getScope().equals(actionScope))
              throw new SmvcRuntimeException(StatusCode.UNDEFINED, "LoadActions.process - Action with same name has different scopes");
          }
        }
      }
    }
    
    pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath());
  }

}