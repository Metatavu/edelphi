package fi.metatavu.edelphi.dao.panels;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate_;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;

public class PanelSettingsTemplateDAO extends GenericDAO<PanelSettingsTemplate> {
  
  public PanelSettingsTemplate create(String name, String description, PanelState state, PanelAccessLevel accessLevel, PanelUserRole defaultPanelUserRole, Boolean archived) {
    PanelSettingsTemplate panelSettingsTemplate = new PanelSettingsTemplate();
    panelSettingsTemplate.setAccessLevel(accessLevel);
    panelSettingsTemplate.setArchived(archived);
    panelSettingsTemplate.setDefaultPanelUserRole(defaultPanelUserRole);
    panelSettingsTemplate.setDescription(description);
    panelSettingsTemplate.setName(name);
    panelSettingsTemplate.setState(state);
    return persist(panelSettingsTemplate);
  }

  public PanelSettingsTemplate findFirst() {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PanelSettingsTemplate> criteria = criteriaBuilder.createQuery(PanelSettingsTemplate.class);
    Root<PanelSettingsTemplate> root = criteria.from(PanelSettingsTemplate.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PanelSettingsTemplate_.archived), Boolean.FALSE));
    TypedQuery<PanelSettingsTemplate> query = entityManager.createQuery(criteria);
    query.setMaxResults(1);
    
    return getSingleResult(query); 
  }

  public PanelSettingsTemplate updateTemplateNameAndDescription(PanelSettingsTemplate template, String templateName, String templateDesc) {
    template.setName(templateName);
    template.setDescription(templateDesc);
    getEntityManager().persist(template);
    return template;
  }

}
