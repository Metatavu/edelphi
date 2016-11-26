package fi.metatavu.edelphi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;

@Singleton
@Startup
@DependsOn (value = "RequestControllersInitializer")
public class ActionsInitializer {
  
  private static final Logger logger = Logger.getLogger(ActionsInitializer.class.getName());

  @PersistenceContext
  private EntityManager entityManager;

  @PostConstruct
  public void init() {
    GenericDAO.setEntityManager(entityManager);
    try {
      initializeActions();
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  private void initializeActions() {
    Properties properties;
    try {
      properties = loadPropertiesFile("edelphiactions.properties");
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read edelphi actions property file", e);
      return;
    }

    createActions(properties);

    // Read Actions bound to controllers
    
    Map<String, RequestController> controllers = RequestControllerMapper.getControllers();
    Set<String> controllerKeys = controllers.keySet();
    for (String key : controllerKeys) {
      RequestController controller = controllers.get(key);
      
      if (controller instanceof ActionedController) {
        ActionedController actionedController = (ActionedController) controller;
        String actionName = actionedController.getAccessActionName() == null ? null : actionedController.getAccessActionName().toString();
        if (actionName != null) {
          createDelfoiAction(actionedController, actionName);
        }
      }
    }
  }

  private void createDelfoiAction(ActionedController actionedController, String actionName) {
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    
    DelfoiAction delfoiAction = delfoiActionDAO.findByActionName(actionName);
    DelfoiActionScope actionScope = actionedController.getAccessActionScope();
    
    if (delfoiAction == null) {
      delfoiActionDAO.create(actionName, actionScope);
      logger.info(String.format("Created new %s scope action %s", actionScope, actionName));
    } else {
      if (!delfoiAction.getScope().equals(actionScope)) {
        logger.severe("LoadActions.process - Action with same name has different scopes");
      }
    }
  }

  private DelfoiActionDAO createActions(Properties properties) {
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
    
    return delfoiActionDAO;
  }
  
  private Properties loadPropertiesFile(String filename) throws IOException {
    Properties properties = new Properties();
    
    try (InputStream fileStream = getClass().getClassLoader().getResourceAsStream(filename)) {
      properties.load(fileStream);
    }
    
    return properties;
  }
  
}
