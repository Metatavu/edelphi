package fi.metatavu.edelphi.dao.users;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import fi.metatavu.edelphi.domainmodel.users.UserPassword_;
import java.util.List;

@ApplicationScoped
public class UserPasswordDAO extends GenericDAO<UserPassword> {
  public List<UserPassword> listAllByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserPassword> criteria = criteriaBuilder.createQuery(UserPassword.class);
    Root<UserPassword> root = criteria.from(UserPassword.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserPassword_.user), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}