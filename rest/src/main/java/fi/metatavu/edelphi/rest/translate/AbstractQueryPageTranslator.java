package fi.metatavu.edelphi.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPageCommentOptions;

/**
 * Abstract base class for query page translators
 * 
 * @author Antti Leppä
 *
 * @param <J> JPA entity
 * @param <R> REST entity
 */
public abstract class AbstractQueryPageTranslator <J, R> extends AbstractTranslator<J, R> {

  @Inject
  private QueryPageController queryPageController;
  
  /**
   * Creates comment options REST response
   * 
   * @param entity query page
   * @return comment options 
   */
  protected QueryPageCommentOptions createCommentOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    QueryPageCommentOptions result = new QueryPageCommentOptions();
    
    if (entity.getPageType() == QueryPageType.TEXT) {
      result.setCommentable(queryPageController.getBooleanSetting(entity, QueryPageController.TEXT_COMMENTABLE_OPTION));
      result.setDiscussionVisible(queryPageController.getBooleanSetting(entity, QueryPageController.TEXT_VIEW_DISCUSSIONS_OPTION));
    } else {
      result.setCommentable(queryPageController.getBooleanSetting(entity, QueryPageController.THESIS_COMMENTABLE_OPTION));
      result.setDiscussionVisible(queryPageController.getBooleanSetting(entity, QueryPageController.THESIS_VIEW_DISCUSSIONS_OPTION));  
    }
    
    List<String> categories = queryPageController.listCommentCategoriesByPage(entity, false).stream()
      .map(QueryQuestionCommentCategory::getName)
      .collect(Collectors.toList());
    
    result.setCategories(categories);
    
    return result;
  }
  
}
