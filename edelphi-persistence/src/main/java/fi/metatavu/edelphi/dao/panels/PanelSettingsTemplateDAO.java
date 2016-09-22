package fi.metatavu.edelphi.dao.panels;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;

public class PanelSettingsTemplateDAO extends GenericDAO<PanelSettingsTemplate> {

  public PanelSettingsTemplate updateTemplateNameAndDescription(PanelSettingsTemplate template, String templateName, String templateDesc) {
    template.setName(templateName);
    template.setDescription(templateDesc);
    getEntityManager().persist(template);
    return template;
  }

}
