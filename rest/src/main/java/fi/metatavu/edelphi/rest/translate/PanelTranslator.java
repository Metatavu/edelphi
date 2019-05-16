package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.rest.model.Panel;
import fi.metatavu.edelphi.rest.model.PanelAccessLevel;
import fi.metatavu.edelphi.rest.model.PanelState;

/**
 * Translator for panels
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.panels.Panel, fi.metatavu.edelphi.rest.model.Panel> {
  
  @Override
  public fi.metatavu.edelphi.rest.model.Panel translate(fi.metatavu.edelphi.domainmodel.panels.Panel entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.Panel result = new Panel();
    result.setAccessLevel(translateEnum(PanelAccessLevel.class, entity.getAccessLevel()));
    result.setDescription(entity.getDescription());
    result.setId(entity.getId());
    result.setName(entity.getName());
    result.setUrlName(entity.getUrlName());
    result.setState(translateEnum(PanelState.class, entity.getState()));
    
    return result;
  }
  
}