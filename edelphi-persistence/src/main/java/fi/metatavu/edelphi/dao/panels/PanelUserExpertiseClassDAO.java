package fi.metatavu.edelphi.dao.panels;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass_;

@ApplicationScoped
public class PanelUserExpertiseClassDAO extends GenericDAO<PanelUserExpertiseClass> {

  public PanelUserExpertiseClass create(Panel panel, String name) {
    PanelUserExpertiseClass panelUserExpertiseClass = new PanelUserExpertiseClass();
    
    panelUserExpertiseClass.setName(name);
    panelUserExpertiseClass.setPanel(panel);
    
    getEntityManager().persist(panelUserExpertiseClass);
    return panelUserExpertiseClass;
  }
  
  public List<PanelUserExpertiseClass> listByPanel(Panel panel) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelUserExpertiseClass> criteria = criteriaBuilder.createQuery(PanelUserExpertiseClass.class);
    Root<PanelUserExpertiseClass> root = criteria.from(PanelUserExpertiseClass.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelUserExpertiseClass_.panel), panel)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  public PanelUserExpertiseClass updateName(PanelUserExpertiseClass expertise, String name) {
    expertise.setName(name);
    getEntityManager().persist(expertise);
    return expertise;
  }
  
}
