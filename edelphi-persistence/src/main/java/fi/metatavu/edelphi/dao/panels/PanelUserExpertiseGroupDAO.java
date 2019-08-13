package fi.metatavu.edelphi.dao.panels;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup_;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;

@ApplicationScoped
public class PanelUserExpertiseGroupDAO extends GenericDAO<PanelUserExpertiseGroup> {

  public PanelUserExpertiseGroup create(Panel panel, PanelUserExpertiseClass expertiseClass, PanelUserIntressClass intressClass, Long color, PanelStamp stamp) {
    PanelUserExpertiseGroup panelUserExpertiseGroup = new PanelUserExpertiseGroup();
    
    panelUserExpertiseGroup.setPanel(panel);
    panelUserExpertiseGroup.setExpertiseClass(expertiseClass);
    panelUserExpertiseGroup.setIntressClass(intressClass);
    panelUserExpertiseGroup.setColor(color);
    panelUserExpertiseGroup.setStamp(stamp);
    
    getEntityManager().persist(panelUserExpertiseGroup);
    return panelUserExpertiseGroup;
  }
  
  public List<PanelUserExpertiseGroup> listByPanelAndStamp(Panel panel, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.panel), panel),
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.stamp), stamp)
        )
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public PanelUserExpertiseGroup findByInterestAndExpertiseAndStamp(PanelUserIntressClass interest, PanelUserExpertiseClass expertise, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.intressClass), interest),
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.expertiseClass), expertise),
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.stamp), stamp)
        )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<PanelUserExpertiseGroup> listByInterest(PanelUserIntressClass interest) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.intressClass), interest)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUserExpertiseGroup> listByInterestAndStamp(PanelUserIntressClass interest, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.intressClass), interest),
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.stamp), stamp)
        )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUserExpertiseGroup> listByExpertise(PanelUserExpertiseClass expertise) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.expertiseClass), expertise)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<PanelUserExpertiseGroup> listByExpertiseAndStamp(PanelUserExpertiseClass expertise, PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseGroup> criteria = criteriaBuilder.createQuery(PanelUserExpertiseGroup.class);
    Root<PanelUserExpertiseGroup> root = criteria.from(PanelUserExpertiseGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.expertiseClass), expertise),
            criteriaBuilder.equal(root.get(PanelUserExpertiseGroup_.stamp), stamp)
        )
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
}
