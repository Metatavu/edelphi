package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.rest.model.QueryPage;
import fi.metatavu.edelphi.rest.model.QueryPageType;

/**
 * Translator for QueryQuestionComments
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageTranslator extends AbstractQueryPageTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QueryPage, fi.metatavu.edelphi.rest.model.QueryPage> {

  @Override
  public fi.metatavu.edelphi.rest.model.QueryPage translate(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    if (entity == null) {
      return null;
    }
    
    QueryPageType type = QueryPageType.fromValue(entity.getPageType().name());
    
    fi.metatavu.edelphi.rest.model.QueryPage result = new QueryPage();
    result.setId(entity.getId());
    result.setTitle(entity.getTitle());
    result.setPageNumber(entity.getPageNumber());
    result.setType(type);
    result.setCommentOptions(createCommentOptions(entity));
    
    return result;
  }

}
