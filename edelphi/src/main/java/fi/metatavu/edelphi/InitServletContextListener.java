package fi.metatavu.edelphi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;

@WebListener
public class InitServletContextListener implements ServletContextListener {
  
  private static Logger logger = Logger.getLogger(InitServletContextListener.class.getName());

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void contextDestroyed(ServletContextEvent ctx) {
    // Nothing to destroy
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    GenericDAO.setEntityManager(entityManager);
    try {
    
      try {
        ServletContext ctx = servletContextEvent.getServletContext();
        String webappPath = ctx.getRealPath("/");
  
        Properties pageControllers = new Properties();
        Properties jsonControllers = new Properties();
        Properties binaryControllers = new Properties();
  
        loadPropertiesFile(pageControllers, "pagemappings.properties");
        loadPropertiesFile(jsonControllers, "jsonmappings.properties");
        loadPropertiesFile(binaryControllers, "binarymappings.properties");
  
        RequestControllerMapper.mapControllers(pageControllers, ".page");
        RequestControllerMapper.mapControllers(jsonControllers, ".json");
        RequestControllerMapper.mapControllers(binaryControllers, ".binary");
        
        System.getProperties().setProperty("appdirectory", webappPath);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Initialization failed", e);
        throw new ExceptionInInitializerError(e);
      }
    
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  private void loadPropertiesFile(Properties properties, String filename) throws IOException {
    try (InputStream fileStream = getClass().getClassLoader().getResourceAsStream(filename)) {
      properties.load(fileStream);
    }
  }
  
}
