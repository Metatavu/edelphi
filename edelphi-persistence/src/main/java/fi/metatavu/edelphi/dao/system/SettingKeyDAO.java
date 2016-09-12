package fi.metatavu.edelphi.dao.system;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.system.SettingKey;
import fi.metatavu.edelphi.domainmodel.system.SettingKey_;

public class SettingKeyDAO extends GenericDAO<SettingKey> {

  public SettingKey findByName(String settingName) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SettingKey> criteria = criteriaBuilder.createQuery(SettingKey.class);
    Root<SettingKey> root = criteria.from(SettingKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(SettingKey_.name), settingName));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

}
