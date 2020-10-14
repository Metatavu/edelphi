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
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation_;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class PanelInvitationDAO extends GenericDAO<PanelInvitation> {

  public PanelInvitation create(Panel panel, Query query, String email, String hash, PanelUserRole role, PanelInvitationState state, User creator) {
    Date now = new Date();
    
    PanelInvitation panelInvitation = new PanelInvitation();
    panelInvitation.setPanel(panel);
    panelInvitation.setQuery(query);
    panelInvitation.setEmail(email);
    panelInvitation.setHash(hash);
    panelInvitation.setRole(role);
    panelInvitation.setState(state);
    panelInvitation.setCreated(now);
    panelInvitation.setCreator(creator);
    panelInvitation.setLastModified(now);
    panelInvitation.setLastModifier(creator);
    panelInvitation.setArchived(Boolean.FALSE);
    
    return persist(panelInvitation);
  }
  
  public PanelInvitation findByPanelAndQueryAndEmail(Panel panel, Query query, String email) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    if (query == null) {
      criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(PanelInvitation_.panel), panel),
          criteriaBuilder.equal(root.get(PanelInvitation_.email), email),
          criteriaBuilder.isNull(root.get(PanelInvitation_.query)),
          criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
        )
      );
    }
    else {
      criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(PanelInvitation_.panel), panel),
          criteriaBuilder.equal(root.get(PanelInvitation_.email), email),
          criteriaBuilder.equal(root.get(PanelInvitation_.query), query),
          criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
        )
      );
    }

    return getSingleResult(entityManager.createQuery(criteria)); 
  }

  public PanelInvitation findByHash(String hash) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelInvitation_.hash), hash),
        criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria)); 
  }
  
  public List<PanelInvitation> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelInvitation_.panel), panel),
        criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public List<PanelInvitation> listByEmailAndState(String email, PanelInvitationState state) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelInvitation_.state), state),
        criteriaBuilder.equal(root.get(PanelInvitation_.email), email),
        criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<PanelInvitation> listByStateAndArchived(PanelInvitationState state, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelInvitation_.state), state),
        criteriaBuilder.equal(root.get(PanelInvitation_.archived), archived)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<PanelInvitation> listByPanelAndEmail(Panel panel, String email) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelInvitation> criteria = criteriaBuilder.createQuery(PanelInvitation.class);
    Root<PanelInvitation> root = criteria.from(PanelInvitation.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(PanelInvitation_.panel), panel),
        criteriaBuilder.equal(root.get(PanelInvitation_.email), email),
        criteriaBuilder.equal(root.get(PanelInvitation_.archived), Boolean.FALSE)
      )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }

  public PanelInvitation updateState(PanelInvitation panelInvitation, PanelInvitationState state, User modifier) {
    panelInvitation.setState(state);
    panelInvitation.setLastModifier(modifier);
    panelInvitation.setLastModified(new Date());
    
    getEntityManager().persist(panelInvitation);
    return panelInvitation;
    
  }
  
}
