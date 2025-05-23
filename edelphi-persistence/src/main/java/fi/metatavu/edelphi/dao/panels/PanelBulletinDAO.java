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
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin;
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class PanelBulletinDAO extends GenericDAO<PanelBulletin> {
  
  public PanelBulletin create(Panel panel, String title, String message, User creator, Boolean important, Date importantEnds) {
    
    Date now = new Date();
    
    PanelBulletin panelBulletin = new PanelBulletin();
    panelBulletin.setArchived(Boolean.FALSE);
    panelBulletin.setCreated(now);
    panelBulletin.setCreator(creator);
    panelBulletin.setLastModified(now);
    panelBulletin.setLastModifier(creator);
    panelBulletin.setTitle(title);
    panelBulletin.setMessage(message);
    panelBulletin.setPanel(panel);
    panelBulletin.setImportant(important);
    panelBulletin.setImportantEnds(importantEnds);
    
    return persist(panelBulletin);
  }
  
  public List<PanelBulletin> listByPanelAndArchived(Panel panel, Boolean archived) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelBulletin> criteria = criteriaBuilder.createQuery(PanelBulletin.class);
    Root<PanelBulletin> root = criteria.from(PanelBulletin.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelBulletin_.archived), archived),
        criteriaBuilder.equal(root.get(PanelBulletin_.panel), panel)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all panel bulletins by panel (including archived)
   *
   * @param panel panel
   * @return list of panel bulletins
   */
  public List<PanelBulletin> listAllByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelBulletin> criteria = criteriaBuilder.createQuery(PanelBulletin.class);
    Root<PanelBulletin> root = criteria.from(PanelBulletin.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PanelBulletin_.panel), panel));

    return entityManager.createQuery(criteria).getResultList();
  }

  public PanelBulletin updateTitle(PanelBulletin panelBulletin, String title, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    panelBulletin.setTitle(title);
    panelBulletin.setLastModifier(modifier);
    panelBulletin.setLastModified(new Date());
    
    entityManager.persist(panelBulletin);
    
    return panelBulletin;
  }

  public PanelBulletin updateMessage(PanelBulletin panelBulletin, String message, User modifier) {
    EntityManager entityManager = getEntityManager();
    
    panelBulletin.setMessage(message);
    panelBulletin.setLastModifier(modifier);
    panelBulletin.setLastModified(new Date());
    
    entityManager.persist(panelBulletin);
    
    return panelBulletin;
  }

  public PanelBulletin updateImportant(PanelBulletin bulletin, Boolean important) {
    bulletin.setImportant(important);
    return persist(bulletin);
  }

  public PanelBulletin updateImportantEnds(PanelBulletin bulletin, Date importantEnds) {
    bulletin.setImportantEnds(importantEnds);
    return persist(bulletin);
  }

  public List<PanelBulletin> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelBulletin> criteria = criteriaBuilder.createQuery(PanelBulletin.class);
    Root<PanelBulletin> root = criteria.from(PanelBulletin.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelBulletin_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelBulletin> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelBulletin> criteria = criteriaBuilder.createQuery(PanelBulletin.class);
    Root<PanelBulletin> root = criteria.from(PanelBulletin.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelBulletin_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
