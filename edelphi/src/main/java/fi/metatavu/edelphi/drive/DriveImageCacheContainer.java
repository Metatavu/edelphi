package fi.metatavu.edelphi.drive;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.Cache;

/**
 * EJB container for report image cache
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
@Singleton
public class DriveImageCacheContainer {

  @Resource(lookup = "java:jboss/infinispan/cache/edelphi/google-image-cache")
  private Cache<Object, Object> cache; 
  
  /**
   * Returns cache
   * 
   * @return cache
   */
  public Cache<Object, Object> getCache() {
    return cache;
  }
  
}
