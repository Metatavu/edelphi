package fi.metatavu.edelphi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;

@Singleton
@Startup
public class RequestControllersInitializer {
  
  private static Logger logger = Logger.getLogger(RequestControllersInitializer.class.getName());

  @PersistenceContext
  private EntityManager entityManager;

  @PostConstruct
  public void init() {
    GenericDAO.setEntityManager(entityManager);
    try {
      initializeRequestControllers();
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  public void initializeRequestControllers() {
    try {
      mapRequestControllers("pagemappings.properties", ".page");
      mapRequestControllers("jsonmappings.properties", ".json");
      mapRequestControllers("binarymappings.properties", ".binary");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Initialization failed", e);
      throw new ExceptionInInitializerError(e);
    }
  }
  
  private void mapRequestControllers(String propertiesFile, String postfix) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    Properties pageControllers = loadPropertiesFile(propertiesFile);
    RequestControllerMapper.mapControllers(pageControllers, postfix);
  }
  
  private Properties loadPropertiesFile(String filename) throws IOException {
    Properties properties = new Properties();
    
    try (InputStream fileStream = getClass().getClassLoader().getResourceAsStream(filename)) {
      properties.load(fileStream);
    }
    
    return properties;
  }
  
}
