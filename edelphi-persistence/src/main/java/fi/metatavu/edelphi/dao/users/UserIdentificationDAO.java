package fi.metatavu.edelphi.dao.users;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.AuthSource;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification_;

@ApplicationScoped
public class UserIdentificationDAO extends GenericDAO<UserIdentification> {

  public UserIdentification create(User user, String externalId, AuthSource authSource) {
    EntityManager entityManager = getEntityManager(); 
    
    UserIdentification userIdentification = new UserIdentification();
    userIdentification.setAuthSource(authSource);
    userIdentification.setExternalId(externalId);
    userIdentification.setUser(user);
    
    entityManager.persist(userIdentification);

    return userIdentification;
  }
  
  /**
   * Lists user identifications by user and auth source
   * 
   * @param user user
   * @param authSource auth source
   * @return user identification
   */
  public List<UserIdentification> listByUserAndAuthSource(User user, AuthSource authSource) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentification> criteria = criteriaBuilder.createQuery(UserIdentification.class);
    Root<UserIdentification> root = criteria.from(UserIdentification.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(UserIdentification_.user), user),
            criteriaBuilder.equal(root.get(UserIdentification_.authSource), authSource)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<UserIdentification> listByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentification> criteria = criteriaBuilder.createQuery(UserIdentification.class);
    Root<UserIdentification> root = criteria.from(UserIdentification.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserIdentification_.user), user)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Finds user by auth source and external id.
   *
   * Method groups by user to mitigate possible duplicates from the same source
   *
   * @param externalId external id
   * @param authSource auth source
   * @return user or null if not found
   */
  public User findUserByAuthSourceAndExternalId(String externalId, AuthSource authSource) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<UserIdentification> root = criteria.from(UserIdentification.class);
    criteria.select(root.get(UserIdentification_.user));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UserIdentification_.externalId), externalId),
        criteriaBuilder.equal(root.get(UserIdentification_.authSource), authSource)
      )
    );

    criteria.groupBy(root.get(UserIdentification_.user));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}
