package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;

/**
 * Translator for QueryReplyTranslator
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryReplyTranslator extends AbstractQueryPageTranslator<QueryReply, fi.metatavu.edelphi.rest.model.QueryReply> {

  @Override
  public fi.metatavu.edelphi.rest.model.QueryReply translate(QueryReply entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.QueryReply result = new fi.metatavu.edelphi.rest.model.QueryReply();
    result.setId(entity.getId());
    result.setQueryId(entity.getQuery() != null ? entity.getQuery().getId() : null);
    result.setStampId(entity.getStamp() != null ? entity.getStamp().getId() : null);
    result.setLastModified(translateDate(entity.getLastModified()));
    result.setLastModifierId(translateUserId(entity.getLastModifier()));
    result.setCreated(translateDate(entity.getCreated()));
    result.setCreatorId(translateUserId(entity.getCreator()));
    
    return result;
  }

}
