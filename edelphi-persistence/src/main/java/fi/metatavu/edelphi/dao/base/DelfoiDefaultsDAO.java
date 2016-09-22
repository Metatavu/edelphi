package fi.metatavu.edelphi.dao.base;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults_;

public class DelfoiDefaultsDAO extends GenericDAO<DelfoiDefaults> {

  public DelfoiDefaults findByDelfoi(Delfoi delfoi) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiDefaults> criteria = criteriaBuilder.createQuery(DelfoiDefaults.class);
    Root<DelfoiDefaults> root = criteria.from(DelfoiDefaults.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DelfoiDefaults_.delfoi), delfoi));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
}
