package fi.metatavu.edelphi.taglib.chartutil;

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
public class ReportCacheContainer {

  @Resource(lookup = "java:jboss/infinispan/cache/edelphi/report-image-cache")
  private Cache<Object, byte[]> cache; 
  
  /**
   * Returns cache
   * 
   * @return cache
   */
  public Cache<Object, byte[]> getCache() {
    return cache;
  }
  
}
