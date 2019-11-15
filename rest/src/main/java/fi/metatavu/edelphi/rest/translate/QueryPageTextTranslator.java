package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPageType;

/**
 * Translator for text query pages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageTextTranslator extends AbstractQueryPageTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QueryPage, fi.metatavu.edelphi.rest.model.QueryPageText> {

  @Inject
  private QueryPageController queryPageController;
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryPageText translate(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    if (entity == null) {
      return null;
    }
    
    QueryPageType type = QueryPageType.fromValue(entity.getPageType().name());
    
    fi.metatavu.edelphi.rest.model.QueryPageText result = new fi.metatavu.edelphi.rest.model.QueryPageText();
    result.setId(entity.getId());
    result.setTitle(entity.getTitle());
    result.setPageNumber(entity.getPageNumber());
    result.setType(type);
    result.setCommentOptions(createCommentOptions(entity));
    result.setContent(queryPageController.getSetting(entity, QueryPageController.TEXT_CONTENT_OPTION));
    
    return result;
  }

}
