package fi.metatavu.edelphi.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;
import fi.metatavu.edelphi.domainmodel.users.UserPassword_;

public class UserPasswordDAO extends GenericDAO<UserPassword> {

  public UserPassword create(User user, String passwordHash) {
    UserPassword userPassword = new UserPassword();
   
    userPassword.setUser(user);
    userPassword.setPasswordHash(passwordHash);
    
    getEntityManager().persist(userPassword);
    return userPassword;
  }
  
  public UserPassword updatePasswordHash(UserPassword userPassword, String passwordHash) {
    userPassword.setPasswordHash(passwordHash);

    getEntityManager().persist(userPassword);
    return userPassword;
  }
  
  public UserPassword findByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserPassword> criteria = criteriaBuilder.createQuery(UserPassword.class);
    Root<UserPassword> root = criteria.from(UserPassword.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserPassword_.user), user));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  
}
