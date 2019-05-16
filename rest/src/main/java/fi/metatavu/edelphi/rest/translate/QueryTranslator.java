package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.rest.model.Query;
import fi.metatavu.edelphi.rest.model.QueryState;

/**
 * Translator for queries
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.resources.Query, fi.metatavu.edelphi.rest.model.Query> {
  
  @Override
  public fi.metatavu.edelphi.rest.model.Query translate(fi.metatavu.edelphi.domainmodel.resources.Query entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.Query result = new Query();
    result.setAllowEditReply(entity.getAllowEditReply());
    result.setCloses(translateDate(entity.getCloses()));
    result.setDescription(entity.getDescription());
    result.setId(entity.getId());
    result.setName(entity.getName());
    result.setState(translateEnum(QueryState.class, entity.getState()));
    result.setUrlName(entity.getUrlName());
    result.setVisible(entity.getVisible());
    result.setLastModified(translateDate(entity.getLastModified()));
    result.setLastModifierId(translateUserId(entity.getLastModifier()));
    result.setCreated(translateDate(entity.getCreated()));
    result.setCreatorId(translateUserId(entity.getCreator()));
  
    return result;
  }
  
}