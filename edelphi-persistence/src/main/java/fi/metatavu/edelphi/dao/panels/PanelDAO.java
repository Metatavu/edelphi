package fi.metatavu.edelphi.dao.panels;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser_;
import fi.metatavu.edelphi.domainmodel.panels.Panel_;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class PanelDAO extends GenericDAO<Panel> {

  public void archivePanelByScheduler (Panel panel) {
    panel.setArchived(true);
    panel.setLastModified(new Date());
    persist(panel);
  }

  @SuppressWarnings ("squid:S00107")
  public Panel create(Delfoi delfoi, String name, String description, Folder rootFolder, 
      PanelState state, PanelAccessLevel accessLevel, PanelUserRole defaultPanelUserRole, User creator) {
    Date now = new Date();

    Panel panel = new Panel();
    panel.setArchived(Boolean.FALSE);
    panel.setCreated(now);
    panel.setLastModified(now);
    panel.setDelfoi(delfoi);
    panel.setDescription(description);
    panel.setName(name);
    panel.setRootFolder(rootFolder);
    panel.setState(state);
    panel.setAccessLevel(accessLevel);
    panel.setDefaultPanelUserRole(defaultPanelUserRole);
    panel.setLastModifier(creator);
    panel.setCreator(creator);

    return persist(panel);
  }
  
  public Panel findByRootFolder(Folder rootFolder) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Panel_.rootFolder), rootFolder));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<Panel> listByDelfoi(Delfoi delfoi) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Panel_.delfoi), delfoi),
        criteriaBuilder.equal(root.get(Panel_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<Panel> listByDelfoiAndAccessLevelInAndState(Delfoi delfoi, Collection<PanelAccessLevel> accessLevels, PanelState state) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(Panel_.delfoi), delfoi),
        root.get(Panel_.accessLevel).in(accessLevels),
        criteriaBuilder.equal(root.get(Panel_.state), state),
        criteriaBuilder.equal(root.get(Panel_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public List<Panel> listByDelfoiAndUser(Delfoi delfoi, User user) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);

    Root<PanelUser> puRoot = criteria.from(PanelUser.class);
    Join<PanelUser, Panel> pRoot = puRoot.join(PanelUser_.panel);
    criteria.select(pRoot);
    criteria.where(
      criteriaBuilder.equal(puRoot.get(PanelUser_.stamp), pRoot.get(Panel_.currentStamp)),
      criteriaBuilder.equal(puRoot.get(PanelUser_.archived), Boolean.FALSE),
      criteriaBuilder.equal(puRoot.get(PanelUser_.user), user),
      criteriaBuilder.equal(pRoot.get(Panel_.delfoi), delfoi),
      criteriaBuilder.equal(pRoot.get(Panel_.archived), Boolean.FALSE)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<Panel> listPanelsToArchive(PanelState panelState, Date before, int maxResults) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);

    criteria.where(
            criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(Panel_.state), panelState),
                    criteriaBuilder.lessThan(root.get(Panel_.lastModified), before),
                    criteriaBuilder.equal(root.get(Panel_.archived), Boolean.FALSE)
            )
    );

    return entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
  }

  public List<Panel> listPanelsToDelete(Date before, int maxResults) {
    EntityManager entityManager = getEntityManager();
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Panel> criteria = criteriaBuilder.createQuery(Panel.class);
    Root<Panel> root = criteria.from(Panel.class);
    criteria.select(root);

    criteria.where(
            criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get(Panel_.lastModified), before),
                    criteriaBuilder.equal(root.get(Panel_.archived), Boolean.TRUE)
            )
    );

    return entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
  }
  
  public Panel update(Panel panel, String name, String description, PanelAccessLevel accessLevel, PanelState state, User modifier) {
    panel.setName(name);
    panel.setDescription(description);
    panel.setAccessLevel(accessLevel);
    panel.setState(state);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    return persist(panel);
  }
  
  public Panel updateInvitationTemplate(Panel panel, String invitationTemplate, User modifier) {
    panel.setInvitationTemplate(invitationTemplate);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    return persist(panel);
  }
  
  public Panel updateCurrentStamp(Panel panel, PanelStamp currentStamp, User modifier) {
    panel.setCurrentStamp(currentStamp);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);
    
    return persist(panel);
  }

  /**
   * Updates root folder of panel
   *
   * @param panel panel
   * @param rootFolder root folder
   * @param modifier modifier
   * @return updated panel
   */
  public Panel updateRootFolder(Panel panel, Folder rootFolder, User modifier) {
    panel.setRootFolder(rootFolder);
    panel.setLastModified(new Date());
    panel.setLastModifier(modifier);

    return persist(panel);
  }
}
