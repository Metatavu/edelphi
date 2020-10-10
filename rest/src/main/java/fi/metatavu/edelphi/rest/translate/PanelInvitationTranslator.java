package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.rest.model.PanelInvitationState;

/**
 * Translator for PanelInvitation
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelInvitationTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.panels.PanelInvitation, fi.metatavu.edelphi.rest.model.PanelInvitation> {

  @Override
  public fi.metatavu.edelphi.rest.model.PanelInvitation translate(PanelInvitation entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.PanelInvitation result = new fi.metatavu.edelphi.rest.model.PanelInvitation();
    result.setId(entity.getId());
    result.setEmail(entity.getEmail());
    result.setPanelId(entity.getPanel() != null ? entity.getPanel().getId() : null);
    result.setQueryId(entity.getQuery() != null ? entity.getQuery().getId() : null);
    result.setState(translateEnum(PanelInvitationState.class, entity.getState()));
    result.setLastModified(translateDate(entity.getLastModified()));
    result.setLastModifierId(translateUserId(entity.getLastModifier()));
    result.setCreated(translateDate(entity.getCreated()));
    result.setCreatorId(translateUserId(entity.getCreator()));
    
    
    return result;
  }

}
