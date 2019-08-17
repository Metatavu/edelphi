package fi.metatavu.edelphi.taglib.chartutil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
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
  
  private static ReportCacheContainer getCacheContainer() {
    BeanManager beanManager = lookup("java:comp/BeanManager");
    Bean<? extends Object> bean = beanManager.resolve(beanManager.getBeans(ReportCacheContainer.class ));
    CreationalContext<?> context = beanManager.createCreationalContext(bean);
    ReportCacheContainer reportCacheContainer = (ReportCacheContainer) beanManager.getReference(bean, ReportCacheContainer.class, context);
    return reportCacheContainer;
  }
  
  private static Cache<Object, byte[]> getCache() {
    Cache<Object, byte[]> cache = null;
    
    ReportCacheContainer reportCacheContainer = getCacheContainer();
    if (reportCacheContainer != null) {
      cache = reportCacheContainer.getCache();
    }
    
    if (cache != null) {
      return cache;
    }
    
    cache = lookup("java:jboss/infinispan/cache/edelphi/report-image-cache");
    if (cache != null) {
      return cache;
    }
    
    CacheContainer cacheContainer = lookup("java:jboss/infinispan/container/edelphi");
    if (cacheContainer == null) {
      return null;
    }
    
    return cacheContainer.getCache("report-image-cache");
  }
  
  @SuppressWarnings("unchecked")
  private static <L> L lookup(String name) {
    try {
      InitialContext initialContext = new InitialContext();
      return (L) initialContext.lookup(name);
    } catch (NamingException e) {
      if (logger.isLoggable(Level.SEVERE)) {
        logger.log(Level.SEVERE, "Failed to lookup edelphi cache container", e);
      }
    }
    
    return null;
  }
  
}
