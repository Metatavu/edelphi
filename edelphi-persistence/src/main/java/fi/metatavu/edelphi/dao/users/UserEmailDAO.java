package fi.metatavu.edelphi.dao.users;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserEmail_;
import fi.metatavu.edelphi.domainmodel.users.User_;

@ApplicationScoped
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

  /**
   * Lists users using address like query
   *
   * @param address address
   * @param firstResult first result
   * @param maxResults max results
   * @return users matching the query
   */
  public List<User> listUsersByAddressLike(String address, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<UserEmail> root = criteria.from(UserEmail.class);
    criteria.select(root.get(UserEmail_.user)).distinct(true);
    Join<UserEmail, User> userJoin = root.join(UserEmail_.user);

    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.like(root.get(UserEmail_.address), address),
        criteriaBuilder.isFalse(userJoin.get(User_.archived))
      )
    );
    
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
