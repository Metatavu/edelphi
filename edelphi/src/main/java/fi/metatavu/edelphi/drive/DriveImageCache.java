package fi.metatavu.edelphi.drive;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;

public class DriveImageCache {
  
  private static final Logger logger = Logger.getLogger(DriveImageCache.class.getName());

  private DriveImageCache() {
  }
  
  public static void put(String fileId, String contentType, byte[] data) {
    Cache<Object, Object> cache = getCache();
    if (cache == null) {
      return;
    }
    
    cache.put(fileId, new ImageEntry(contentType, data));
  }
  
  public static ImageEntry get(String fileId) {
    Cache<Object, Object> cache = getCache();
    if (cache == null) {
      return null;
    }
    
    return (ImageEntry) cache.get(fileId);
  }
  
  private static DriveImageCacheContainer getCacheContainer() {
    BeanManager beanManager = lookup("java:comp/BeanManager");
    Bean<? extends Object> bean = beanManager.resolve(beanManager.getBeans(DriveImageCacheContainer.class ));
    CreationalContext<?> context = beanManager.createCreationalContext(bean);
    DriveImageCacheContainer driveImageCacheContainer = (DriveImageCacheContainer) beanManager.getReference(bean, DriveImageCacheContainer.class, context);
    return driveImageCacheContainer;
  }
  
  private static Cache<Object, Object> getCache() {
    Cache<Object, Object> cache = null;
    
    DriveImageCacheContainer driveImageCacheContainer = getCacheContainer();
    if (driveImageCacheContainer != null) {
      cache = driveImageCacheContainer.getCache();
    }
    
    if (cache != null) {
      return cache;
    }
    
    cache = lookup("java:jboss/infinispan/cache/edelphi/google-image-cache");
    if (cache != null) {
      return cache;
    }
    
    CacheContainer cacheContainer = lookup("java:jboss/infinispan/container/edelphi");
    if (cacheContainer == null) {
      return null;
    }
    
    return cacheContainer.getCache("google-image-cache");
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
  
  public static class ImageEntry implements Serializable {
    
    private static final long serialVersionUID = 5289138477736492992L;
    
    private byte[] data;
    private String contentType;
    
    public ImageEntry() {
      // Zero-argument constructor
    }
    
    public ImageEntry(String contentType, byte[] data) {
      super();
      this.data = data;
      this.contentType = contentType;
    }

    public String getContentType() {
      return contentType;
    }
    
    public void setContentType(String contentType) {
      this.contentType = contentType;
    }
    
    public byte[] getData() {
      return data;
    }
    
    public void setData(byte[] data) {
      this.data = data;
    }
    
  }
  
}
