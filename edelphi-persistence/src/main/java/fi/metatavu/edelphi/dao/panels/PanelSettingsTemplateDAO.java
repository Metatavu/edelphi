package fi.metatavu.edelphi.dao.panels;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
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

  public PanelSettingsTemplate updateTemplateNameAndDescription(PanelSettingsTemplate template, String templateName, String templateDesc) {
    template.setName(templateName);
    template.setDescription(templateDesc);
    getEntityManager().persist(template);
    return template;
  }

}
