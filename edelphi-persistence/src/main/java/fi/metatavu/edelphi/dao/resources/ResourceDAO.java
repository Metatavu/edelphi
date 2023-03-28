package fi.metatavu.edelphi.dao.resources;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.resources.ResourceType;
import fi.metatavu.edelphi.domainmodel.resources.Resource_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class ResourceDAO extends GenericDAO<Resource> {
  
  public Resource findByUrlNameAndParentFolder(String urlName, Folder parentFolder) {
    EntityManager entityManager = getEntityManager(); 
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(Resource_.urlName), urlName),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), parentFolder),
            criteriaBuilder.equal(root.get(Resource_.archived), Boolean.FALSE)
        )
    );
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<Resource> listByTypesAndFolderAndArchived(Collection<ResourceType> types, Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            root.get(Resource_.type).in(types),
            criteriaBuilder.equal(root.get(Resource_.archived), archived),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), folder)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Resource> listByTypesAndFolderAndArchived(Collection<ResourceType> types, Folder folder, Boolean archived, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            root.get(Resource_.type).in(types),
            criteriaBuilder.equal(root.get(Resource_.archived), archived),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), folder)
        )
    );
    
    TypedQuery<Resource> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public List<Resource> listByTypesAndFolderAndVisibleAndArchived(Collection<ResourceType> types, Folder folder, Boolean visible, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            root.get(Resource_.type).in(types),
            criteriaBuilder.equal(root.get(Resource_.archived), archived),
            criteriaBuilder.equal(root.get(Resource_.visible), visible),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), folder)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Resource> listByTypesAndFolderAndVisibleAndArchived(Collection<ResourceType> types, Folder folder, Boolean visible, Boolean archived, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            root.get(Resource_.type).in(types),
            criteriaBuilder.equal(root.get(Resource_.archived), archived),
            criteriaBuilder.equal(root.get(Resource_.visible), visible),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), folder)
        )
    );
    
    TypedQuery<Resource> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

  /**
   * Lists all resources by parent folder (including archived)
   *
   * @param parentFolder parent folder
   * @return list of resources
   */
  public List<Resource> listAllByParentFolder(Folder parentFolder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Resource> criteria = criteriaBuilder.createQuery(Resource.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Resource_.parentFolder), parentFolder));

    TypedQuery<Resource> query = entityManager.createQuery(criteria);
    return query.getResultList();
  }
  
  public Long countByTypeAndFolderAndArchived(Collection<ResourceType> types, Folder folder, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
        criteriaBuilder.and(
            root.get(Resource_.type).in(types),
            criteriaBuilder.equal(root.get(Resource_.archived), archived),
            criteriaBuilder.equal(root.get(Resource_.parentFolder), folder)
        )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Resource updateDescription(Resource resource, String description, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    resource.setDescription(description);
    resource.setLastModifier(modifier);
    resource.setLastModified(new Date());
    
    entityManager.persist(resource);
    
    return resource;
  }
  
  // TODO: Rename
  public Resource updateArchived(Resource resource, Boolean archived, User modifier) {
    return updateUrlNameAndArchived(resource, resource.getUrlName(), archived, modifier);
  }

  // TODO: Rename
  public Resource updateUrlNameAndArchived(Resource resource, String urlName, Boolean archived, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    resource.setUrlName(urlName);
    resource.setArchived(archived);
    resource.setLastModifier(modifier);
    resource.setLastModified(new Date());
    
    entityManager.persist(resource);
    
    return resource;
  }

  public Resource updateVisible(Resource resource, Boolean visible, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    resource.setVisible(visible);
    resource.setLastModifier(modifier);
    resource.setLastModified(new Date());
    
    entityManager.persist(resource);
    
    return resource;
  }

  public Resource updateParentFolder(Resource resource, Folder parentFolder, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    resource.setParentFolder(parentFolder);
    resource.setLastModifier(modifier);
    resource.setLastModified(new Date());
    
    entityManager.persist(resource);
    
    return resource;
  }

  public Resource updateResourceIndexNumber(Resource resource, Integer indexNumber, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    resource.setIndexNumber(indexNumber);
    resource.setLastModifier(modifier);
    resource.setLastModified(new Date());
    
    entityManager.persist(resource);
    
    return resource;
  }
  
  public Integer findMaxIndexNumber(Folder parentFolder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Integer> criteria = criteriaBuilder.createQuery(Integer.class);

    Root<Resource> root = criteria.from(Resource.class);
    criteria.select(criteriaBuilder.max(root.get(Resource_.indexNumber)));
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(Resource_.parentFolder), parentFolder),
            criteriaBuilder.equal(root.get(Resource_.archived), Boolean.FALSE)
        )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
}
