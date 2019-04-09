package fi.metatavu.edelphi.dao.base;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiAuth;
import fi.metatavu.edelphi.domainmodel.base.DelfoiAuth_;

@ApplicationScoped
public class DelfoiAuthDAO extends GenericDAO<DelfoiAuth> {

  public List<DelfoiAuth> listByDelfoi(Delfoi delfoi) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiAuth> criteria = criteriaBuilder.createQuery(DelfoiAuth.class);
    Root<DelfoiAuth> root = criteria.from(DelfoiAuth.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(DelfoiAuth_.delfoi), delfoi)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
 
}
