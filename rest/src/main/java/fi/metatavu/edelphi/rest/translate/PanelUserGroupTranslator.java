package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;

/**
 * Translator for PanelUserGroup
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelUserGroupTranslator extends AbstractTranslator<PanelUserGroup, fi.metatavu.edelphi.rest.model.PanelUserGroup> {

  @Override
  public fi.metatavu.edelphi.rest.model.PanelUserGroup translate(PanelUserGroup entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.PanelUserGroup result = new fi.metatavu.edelphi.rest.model.PanelUserGroup();
    result.setId(entity.getId());
    result.setName(entity.getName());
    
    return result;
  }

}
