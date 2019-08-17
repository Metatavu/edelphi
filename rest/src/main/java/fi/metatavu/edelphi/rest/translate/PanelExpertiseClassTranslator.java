package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.rest.model.PanelExpertiseClass;

/**
 * Translator for PanelExpertiseClass
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelExpertiseClassTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass, fi.metatavu.edelphi.rest.model.PanelExpertiseClass> {

  @Override
  public PanelExpertiseClass translate(PanelUserExpertiseClass entity) {
    if (entity == null) {
      return null;
    }
    
    PanelExpertiseClass result = new PanelExpertiseClass();
    result.setId(entity.getId());
    result.setName(entity.getName());
    
    return result;
  }

}
