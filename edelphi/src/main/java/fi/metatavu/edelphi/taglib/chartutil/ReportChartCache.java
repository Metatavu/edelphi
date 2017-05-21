package fi.metatavu.edelphi.taglib.chartutil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;

public class ReportChartCache {
  
  private static final Logger logger = Logger.getLogger(ReportChartCache.class.getName());

  private ReportChartCache() {
  }
  
  public static void put(String id, byte[] data) {
    Cache<Object, byte[]> cache = getCache();
    if (cache == null) {
      return;
    }
    
    cache.put(id, data);
  }
  
  public static byte[] pop(String id) {
    Cache<Object, byte[]> cache = getCache();
    if (cache == null) {
      return new byte[0];
    }
    
    return cache.remove(id);
  }
  
  private static Cache<Object, byte[]> getCache() {
    CacheContainer cacheContainer = getCacheContainer();
    if (cacheContainer == null) {
      return null;
    }
    
    return cacheContainer.getCache("report-image-cache");
  }
  
  private static CacheContainer getCacheContainer() {
    try {
      InitialContext initialContext = new InitialContext();
      return (CacheContainer) initialContext.lookup("java:jboss/infinispan/container/edelphi");
    } catch (NamingException e) {
      if (logger.isLoggable(Level.SEVERE)) {
        logger.log(Level.SEVERE, "Failed to lookup edelphi cache container", e);
      }
    }
    
    return null;
  }
  
}
