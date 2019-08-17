package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.rest.model.PanelInterestClass;

/**
 * Translator for PanelInterestClass
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelInterestClassTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass, fi.metatavu.edelphi.rest.model.PanelInterestClass> {

  @Override
  public PanelInterestClass translate(PanelUserIntressClass entity) {
    if (entity == null) {
      return null;
    }
    
    PanelInterestClass result = new PanelInterestClass();
    result.setId(entity.getId());
    result.setName(entity.getName());
    
    return result;
  }

}
