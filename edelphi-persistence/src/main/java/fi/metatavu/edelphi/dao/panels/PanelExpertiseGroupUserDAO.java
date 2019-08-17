package fi.metatavu.edelphi.dao.panels;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser_;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class PanelExpertiseGroupUserDAO extends GenericDAO<PanelExpertiseGroupUser> {

  public PanelExpertiseGroupUser create(PanelUserExpertiseGroup expertiseGroup, PanelUser panelUser, Double weight) {
    PanelExpertiseGroupUser panelExpertiseGroupUser = new PanelExpertiseGroupUser();
    
    panelExpertiseGroupUser.setExpertiseGroup(expertiseGroup);
    panelExpertiseGroupUser.setPanelUser(panelUser);
    panelExpertiseGroupUser.setWeight(weight);
    
    getEntityManager().persist(panelExpertiseGroupUser);
    return panelExpertiseGroupUser;
  }
  
  public PanelExpertiseGroupUser findByGroupAndPanelUser(PanelUserExpertiseGroup group, PanelUser panelUser) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelExpertiseGroupUser> criteria = criteriaBuilder.createQuery(PanelExpertiseGroupUser.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.expertiseGroup), group),
        criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.panelUser), panelUser)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  public List<PanelExpertiseGroupUser> listByGroup(PanelUserExpertiseGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelExpertiseGroupUser> criteria = criteriaBuilder.createQuery(PanelExpertiseGroupUser.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.expertiseGroup), group)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<PanelExpertiseGroupUser> listByGroupAndArchived(PanelUserExpertiseGroup group, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelExpertiseGroupUser> criteria = criteriaBuilder.createQuery(PanelExpertiseGroupUser.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    Join<PanelExpertiseGroupUser, PanelUser> ppJoin = root.join(PanelExpertiseGroupUser_.panelUser);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.expertiseGroup), group),
        criteriaBuilder.equal(ppJoin.get(PanelUser_.archived), archived)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public PanelExpertiseGroupUser updateGroup(PanelExpertiseGroupUser panelExpertiseGroupUser, PanelUserExpertiseGroup newExpertiseGroup) {
    panelExpertiseGroupUser.setExpertiseGroup(newExpertiseGroup);
    getEntityManager().persist(panelExpertiseGroupUser);
    return panelExpertiseGroupUser;
  }

  public List<PanelExpertiseGroupUser> listByUser(PanelUser pUser) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelExpertiseGroupUser> criteria = criteriaBuilder.createQuery(PanelExpertiseGroupUser.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.panelUser), pUser)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  /**
   * Lists users in given expertise groups
   * 
   * @param expertiseGroups expertise groups
   * @return users in given expertise groups
   */
  public List<User> listUsersByExpertiseGroups(List<PanelUserExpertiseGroup> expertiseGroups) {
    if (expertiseGroups == null || expertiseGroups.isEmpty()) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    Join<PanelExpertiseGroupUser, PanelUserExpertiseGroup> expertiseGroupJoin = root.join(PanelExpertiseGroupUser_.expertiseGroup);
    Join<PanelExpertiseGroupUser, PanelUser> panelUser = root.join(PanelExpertiseGroupUser_.panelUser);
    
    criteria.select(panelUser.get(PanelUser_.user)).distinct(true);
    criteria.where(expertiseGroupJoin.in(expertiseGroups));
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public Long getUserCountInGroup(PanelUserExpertiseGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<PanelExpertiseGroupUser> root = criteria.from(PanelExpertiseGroupUser.class);
    Join<PanelExpertiseGroupUser, PanelUser> ppJoin = root.join(PanelExpertiseGroupUser_.panelUser);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelExpertiseGroupUser_.expertiseGroup), group),
        criteriaBuilder.equal(ppJoin.get(PanelUser_.archived), Boolean.FALSE)
      )
    );
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
}
