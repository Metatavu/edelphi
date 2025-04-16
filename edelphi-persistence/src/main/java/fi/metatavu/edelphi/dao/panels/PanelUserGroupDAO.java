package fi.metatavu.edelphi.dao.panels;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class PanelUserGroupDAO extends GenericDAO<PanelUserGroup> {

  public PanelUserGroup create(Panel panel, String name, List<User> users, PanelStamp stamp, User creator) {
    Date now = new Date();

    PanelUserGroup panelUserGroup = new PanelUserGroup();
    panelUserGroup.setPanel(panel);
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setStamp(stamp);
    panelUserGroup.setCreated(now);
    panelUserGroup.setCreator(creator);
    panelUserGroup.setLastModified(now);
    panelUserGroup.setLastModifier(creator);
    panelUserGroup.setArchived(Boolean.FALSE);

    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }

  public PanelUserGroup create(Panel panel, String name, List<User> users, PanelStamp stamp, User creator, Date created, User modifier, Date modified) {
    PanelUserGroup panelUserGroup = new PanelUserGroup();
    panelUserGroup.setPanel(panel);
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setStamp(stamp);
    panelUserGroup.setCreated(created);
    panelUserGroup.setCreator(creator);
    panelUserGroup.setLastModified(modified);
    panelUserGroup.setLastModifier(modifier);
    panelUserGroup.setArchived(Boolean.FALSE);

    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }

  public List<PanelUserGroup> listByPanelAndStamp(Panel panel, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelUserGroup_.panel), panel), 
        criteriaBuilder.equal(root.get(PanelUserGroup_.stamp), stamp), 
        criteriaBuilder.equal(root.get(PanelUserGroup_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all panel user groups by panel (including archived from all stamps)
   *
   * @param panel panel
   * @return list of panel user groups
   */
  public List<PanelUserGroup> listAllByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PanelUserGroup_.panel), panel));

    return entityManager.createQuery(criteria).getResultList();
  }

  public PanelUserGroup update(PanelUserGroup panelUserGroup, String name, List<User> users, User updater) {
    panelUserGroup.setName(name);
    panelUserGroup.setUsers(users);
    panelUserGroup.setLastModified(new Date());
    panelUserGroup.setLastModifier(updater);
    getEntityManager().persist(panelUserGroup);
    return panelUserGroup;
  }

  public List<PanelUserGroup> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserGroup_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUserGroup> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserGroup_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUserGroup> listUserPanelGroups(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserGroup> criteria = criteriaBuilder.createQuery(PanelUserGroup.class);
    Root<PanelUserGroup> root = criteria.from(PanelUserGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.isMember(user, root.get(PanelUserGroup_.users))
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
