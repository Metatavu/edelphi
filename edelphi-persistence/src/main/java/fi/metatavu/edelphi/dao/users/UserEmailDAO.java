package fi.metatavu.edelphi.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserEmail_;

public class UserEmailDAO extends GenericDAO<UserEmail> {

  public UserEmail create(User user, String address) {
    UserEmail userEmail = new UserEmail();
    
    userEmail.setUser(user);
    userEmail.setAddress(address);
    getEntityManager().persist(userEmail);

    return userEmail;
  }

  public UserEmail findByAddress(String address) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserEmail> criteria = criteriaBuilder.createQuery(UserEmail.class);
    Root<UserEmail> root = criteria.from(UserEmail.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserEmail_.address), address));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<UserEmail> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserEmail> criteria = criteriaBuilder.createQuery(UserEmail.class);
    Root<UserEmail> root = criteria.from(UserEmail.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserEmail_.user), user));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public UserEmail updateAddress(UserEmail userEmail, String address) {
    userEmail.setAddress(address);
    getEntityManager().persist(userEmail);
    return userEmail;
  }
  

}
