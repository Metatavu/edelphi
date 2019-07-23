package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.rest.model.PanelExpertiseGroup;

/**
 * Translator for PanelExpertiseGroup
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelExpertiseGroupTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup, fi.metatavu.edelphi.rest.model.PanelExpertiseGroup> {

  @Override
  public PanelExpertiseGroup translate(PanelUserExpertiseGroup entity) {
    if (entity == null) {
      return null;
    }
    
    PanelExpertiseGroup result = new PanelExpertiseGroup();
    result.setId(entity.getId());
    result.setExpertiseClassId(entity.getExpertiseClass() != null ? entity.getExpertiseClass().getId() : null);
    result.setInterestClassId(entity.getIntressClass() != null ? entity.getIntressClass().getId() : null);
    
    return result;
  }

}
