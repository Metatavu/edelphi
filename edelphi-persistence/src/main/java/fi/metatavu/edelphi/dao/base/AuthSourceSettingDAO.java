package fi.metatavu.edelphi.dao.base;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.base.AuthSourceSetting;
import fi.metatavu.edelphi.domainmodel.base.AuthSourceSetting_;

public class AuthSourceSettingDAO extends GenericDAO<AuthSource> {
  
  public List<AuthSourceSetting> listByAuthSource(AuthSource authSource) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<AuthSourceSetting> criteria = criteriaBuilder.createQuery(AuthSourceSetting.class);
    Root<AuthSourceSetting> root = criteria.from(AuthSourceSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(AuthSourceSetting_.authSource), authSource)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

}
