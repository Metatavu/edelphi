package fi.metatavu.edelphi.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import fi.metatavu.edelphi.domainmodel.base.ArchivableEntity;
import fi.metatavu.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.metatavu.edelphi.domainmodel.users.User;

public class GenericDAO<T> {

  private static final ThreadLocal<EntityManager> THREAD_LOCAL = new ThreadLocal<>();

  @SuppressWarnings("unchecked")
  public T findById(Long id) {
    return (T) getEntityManager().find(getGenericTypeClass(), id);
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll() {
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = getEntityManager().createQuery("select o from " + genericTypeClass.getName() + " o");
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<T> listAll(int firstResult, int maxResults) {
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = getEntityManager().createQuery("select o from " + genericTypeClass.getName() + " o");
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    return query.getResultList();
  }
  
  public void archive(T entity) {
    archive(entity, null);
  }
  
  public void archive(T entity, User user) {
    if (!(entity instanceof ArchivableEntity)) {
      throw new PersistenceException("Entity does not implement ArchivableEntity");
    }
    
    ((ArchivableEntity) entity).setArchived(Boolean.TRUE);
    if (entity instanceof ModificationTrackedEntity && user != null) {
      ((ModificationTrackedEntity) entity).setLastModified(new Date());
      ((ModificationTrackedEntity) entity).setLastModifier(user);
    }
    
    persist(entity);
}

  public void unarchive(T entity) {
    unarchive(entity, null);
  }
  
  public void unarchive(T entity, User user) {
    if (!(entity instanceof ArchivableEntity)) {
      throw new PersistenceException("Entity does not implement ArchivableEntity");
    }
    
    ((ArchivableEntity) entity).setArchived(Boolean.FALSE);
    if (entity instanceof ModificationTrackedEntity && user != null) {
      ((ModificationTrackedEntity) entity).setLastModified(new Date());
      ((ModificationTrackedEntity) entity).setLastModifier(user);
    }
    
    persist(entity);
  }

  public Integer count() {
    Class<?> genericTypeClass = getGenericTypeClass();
    Query query = getEntityManager().createQuery("select count(o) from " + genericTypeClass.getName() + " o");
    return (Integer) query.getSingleResult();
  }

  public void delete(T e) {
    getEntityManager().remove(e);
  }
  
  public void flush() {
    getEntityManager().flush();
  }
  
  public T persist(T entity) {
    getEntityManager().persist(entity);
    return entity;
  }
  
  protected T getSingleResult(Query query) {
    @SuppressWarnings("unchecked")
    List<T> list = query.getResultList();
    
    if (list.isEmpty())
      return null;
    
    if (list.size() == 1)
      return list.get(0);
    
    throw new NonUniqueResultException("SingleResult query returned " + list.size() + " elements");
  }
  
  protected EntityManager getEntityManager() {
    return THREAD_LOCAL.get();
  }
  
  public static void setEntityManager(EntityManager entityManager) {
    if (entityManager == null)
      THREAD_LOCAL.remove();
    else
      THREAD_LOCAL.set(entityManager);
  }
  
  private Class<?> getGenericTypeClass() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<?>) parameterizedType.getActualTypeArguments()[0];
  }
  
}
