package fi.metatavu.edelphi.dao.users;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserActivation;
import fi.metatavu.edelphi.domainmodel.users.UserActivation_;

@ApplicationScoped
public class UserActivationDAO extends GenericDAO<UserActivation> {
  
  public UserActivation create(User user, String email, String hash) {
    UserActivation userActivation = new UserActivation();
    userActivation.setUser(user);
    userActivation.setEmail(email);
    userActivation.setHash(hash);
    getEntityManager().persist(userActivation);
    return userActivation;
  }

  public UserActivation findByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserActivation> criteria = criteriaBuilder.createQuery(UserActivation.class);
    Root<UserActivation> root = criteria.from(UserActivation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserActivation_.user), user)
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  public UserActivation findByEmailAndHash(String email, String hash) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserActivation> criteria = criteriaBuilder.createQuery(UserActivation.class);
    Root<UserActivation> root = criteria.from(UserActivation.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(UserActivation_.email), email),
          criteriaBuilder.equal(root.get(UserActivation_.hash), hash)
        )
      );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

}