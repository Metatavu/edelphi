package fi.metatavu.edelphi.dao.base;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRole;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRoleType;
import fi.metatavu.edelphi.domainmodel.base.SystemUserRole_;

@ApplicationScoped
public class SystemUserRoleDAO extends GenericDAO<SystemUserRole> {

  public SystemUserRole findByType(SystemUserRoleType type) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SystemUserRole> criteria = criteriaBuilder.createQuery(SystemUserRole.class);
    Root<SystemUserRole> root = criteria.from(SystemUserRole.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SystemUserRole_.type), type));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
}
