package fi.metatavu.edelphi.dao.panels;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole_;
import fi.metatavu.edelphi.domainmodel.users.UserRole;

@ApplicationScoped
public class PanelSettingsTemplateRoleDAO extends GenericDAO<PanelSettingsTemplateRole> {
  
  public PanelSettingsTemplateRole create(PanelSettingsTemplate panelSettingsTemplate, DelfoiAction delfoiAction, UserRole userRole) {
    PanelSettingsTemplateRole panelSettingsTemplateRole = new PanelSettingsTemplateRole();
    
    panelSettingsTemplateRole.setDelfoiAction(delfoiAction);
    panelSettingsTemplateRole.setPanelSettingsTemplate(panelSettingsTemplate);
    panelSettingsTemplateRole.setUserRole(userRole);
    
    getEntityManager().persist(panelSettingsTemplateRole);
    return panelSettingsTemplateRole;
  }

  public List<PanelSettingsTemplateRole> listByTemplate(PanelSettingsTemplate panelSettingsTemplate) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelSettingsTemplateRole> criteria = criteriaBuilder.createQuery(PanelSettingsTemplateRole.class);
    Root<PanelSettingsTemplateRole> root = criteria.from(PanelSettingsTemplateRole.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PanelSettingsTemplateRole_.panelSettingsTemplate), panelSettingsTemplate)
    );

    return entityManager.createQuery(criteria).getResultList(); 
  }
}
