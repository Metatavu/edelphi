package fi.metatavu.edelphi.dao.base;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.Delfoi_;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.search.SearchUtils;

@ApplicationScoped
public class DelfoiDAO extends GenericDAO<Delfoi> {

  public Delfoi create(String domain, Folder rootFolder) {
    Delfoi delfoi = new Delfoi();
    delfoi.setDomain(domain);
    delfoi.setRootFolder(rootFolder);
    getEntityManager().persist(delfoi);
    return delfoi;
  }
  
  public Delfoi updateDomain(Delfoi delfoi, String domain) {
    delfoi.setDomain(domain);
    getEntityManager().persist(delfoi);
    return delfoi;
  }
  
  public Delfoi updateRootFolder(Delfoi delfoi, Folder rootFolder) {
    delfoi.setRootFolder(rootFolder);
    getEntityManager().persist(delfoi);
    return delfoi;
  }
  
  public Delfoi findByDomain(String domain) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Delfoi> criteria = criteriaBuilder.createQuery(Delfoi.class);
    Root<Delfoi> root = criteria.from(Delfoi.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Delfoi_.domain), domain));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public Delfoi findByRootFolder(Folder rootFolder) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Delfoi> criteria = criteriaBuilder.createQuery(Delfoi.class);
    Root<Delfoi> root = criteria.from(Delfoi.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Delfoi_.rootFolder), rootFolder));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  // TODO not a DAO method
  public void reindexEntities() {
    try {
      SearchUtils.reindexHibernateSearchObjects(getEntityManager());
    }
    catch (Exception e) {
    }
  }
  
}
