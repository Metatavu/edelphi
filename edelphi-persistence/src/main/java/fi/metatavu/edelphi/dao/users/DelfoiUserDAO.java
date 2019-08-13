package fi.metatavu.edelphi.dao.users;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiUser;
import fi.metatavu.edelphi.domainmodel.base.DelfoiUser_;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class DelfoiUserDAO extends GenericDAO<DelfoiUser> {

  public DelfoiUser create(Delfoi delfoi, User user, DelfoiUserRole role, User creator) {
    EntityManager entityManager = getEntityManager(); 
    
    Date now = new Date();
    
    DelfoiUser delfoiUser = new DelfoiUser();
    delfoiUser.setCreated(now);
    delfoiUser.setCreator(creator);
    delfoiUser.setDelfoi(delfoi);
    delfoiUser.setLastModified(now);
    delfoiUser.setLastModifier(creator);
    delfoiUser.setRole(role);
    delfoiUser.setUser(user);
    
    entityManager.persist(delfoiUser);
    
    return delfoiUser;
  }
  
  public DelfoiUser findByDelfoiAndUser(Delfoi delfoi, User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUser> criteria = criteriaBuilder.createQuery(DelfoiUser.class);
    Root<DelfoiUser> root = criteria.from(DelfoiUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(DelfoiUser_.delfoi), delfoi), 
          criteriaBuilder.equal(root.get(DelfoiUser_.user), user), 
          criteriaBuilder.equal(root.get(DelfoiUser_.archived), Boolean.FALSE)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<DelfoiUser> listByUser(User user) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUser> criteria = criteriaBuilder.createQuery(DelfoiUser.class);
    Root<DelfoiUser> root = criteria.from(DelfoiUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(DelfoiUser_.user), user), 
          criteriaBuilder.equal(root.get(DelfoiUser_.archived), Boolean.FALSE)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<DelfoiUser> listByDelfoiAndRoleAndArchived(Delfoi delfoi, DelfoiUserRole role, Boolean archived) {
		EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DelfoiUser> criteria = criteriaBuilder.createQuery(DelfoiUser.class);
    Root<DelfoiUser> root = criteria.from(DelfoiUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(DelfoiUser_.role), role), 
          criteriaBuilder.equal(root.get(DelfoiUser_.delfoi), delfoi), 
          criteriaBuilder.equal(root.get(DelfoiUser_.archived), archived)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

}
