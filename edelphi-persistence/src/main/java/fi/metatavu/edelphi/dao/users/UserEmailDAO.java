package fi.metatavu.edelphi.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
  
  public List<User> listUsersByAddressLike(String address, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<UserEmail> root = criteria.from(UserEmail.class);
    criteria.select(root.get(UserEmail_.user)).distinct(true);
    criteria.where(criteriaBuilder.like(root.get(UserEmail_.address), address));
    
    TypedQuery<User> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }
  
  public UserEmail updateAddress(UserEmail userEmail, String address) {
    userEmail.setAddress(address);
    getEntityManager().persist(userEmail);
    return userEmail;
  }
  

}
