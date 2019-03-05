package fi.metatavu.edelphi.dao.base;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.AuthSource_;

@ApplicationScoped
public class AuthSourceDAO extends GenericDAO<AuthSource> {
  
  /**
   * Finds an auth source by strategy
   * 
   * @param strategy strategy
   * @return auth source or null if not found
   */
  public AuthSource findByStrategy(String strategy) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<AuthSource> criteria = criteriaBuilder.createQuery(AuthSource.class);
    Root<AuthSource> root = criteria.from(AuthSource.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(AuthSource_.strategy), strategy)
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }
  
}
