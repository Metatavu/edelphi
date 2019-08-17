package fi.metatavu.edelphi.dao.users;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserSetting;
import fi.metatavu.edelphi.domainmodel.users.UserSettingKey;
import fi.metatavu.edelphi.domainmodel.users.UserSetting_;

@ApplicationScoped
public class UserSettingDAO extends GenericDAO<UserSetting> {
  
  public UserSetting create(User user, UserSettingKey key, String value) {
    UserSetting userSetting = new UserSetting();
    userSetting.setUser(user);
    userSetting.setKey(key);
    userSetting.setValue(value);
    getEntityManager().persist(userSetting);
    return userSetting;
  }
  
  public UserSetting findByUserAndKey(User user, UserSettingKey key) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserSetting> criteria = criteriaBuilder.createQuery(UserSetting.class);
    Root<UserSetting> root = criteria.from(UserSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(UserSetting_.user), user), 
          criteriaBuilder.equal(root.get(UserSetting_.key), key) 
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public UserSetting updateValue(UserSetting userSetting, String value) {
    userSetting.setValue(value);
    getEntityManager().persist(userSetting);
    return userSetting;
  }

  
}
