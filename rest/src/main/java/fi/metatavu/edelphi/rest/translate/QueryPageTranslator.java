package fi.metatavu.edelphi.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPage;
import fi.metatavu.edelphi.rest.model.QueryPageCommentOptions;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DAnswersVisibleOption;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DAxis;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DColor;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DOptions;
import fi.metatavu.edelphi.rest.model.QueryPageType;

/**
 * Translator for QueryQuestionComments
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageTranslator extends AbstractTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QueryPage, fi.metatavu.edelphi.rest.model.QueryPage> {

  @Inject
  private QueryPageController queryPageController;
  
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
    result.setQueryOptions(createOptions(entity));
    result.setType(type);
    result.setCommentOptions(createCommentOptions(entity));
    
    return result;
  }

  /**
   * Creates comment options REST response
   * 
   * @param entity query page
   * @return comment options 
   */
  private QueryPageCommentOptions createCommentOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    QueryPageCommentOptions result = new QueryPageCommentOptions();
    result.setCommentable(queryPageController.getBooleanSetting(entity, "thesis.commentable"));
    result.setDiscussionVisible(queryPageController.getBooleanSetting(entity, "thesis.viewDiscussions"));
    
    List<String> categories = queryPageController.listCommentCategoriesByPage(entity).stream()
      .map(QueryQuestionCommentCategory::getName)
      .collect(Collectors.toList());
    
    result.setCategories(categories);
    
    return result;
  }

  /**
   * Creates options REST response
   * 
   * @param entity query page
   * @return options REST response
   */
  private Object createOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    switch (entity.getPageType()) {
      case LIVE_2D:
        return createLive2dOptions(entity);
      default:
      break;
    }
    
    return null;
  }

  /**
   * Creates live2d options REST response
   * 
   * @param queryPage query page
   * @return live2d options REST response
   */
  private QueryPageLive2DOptions createLive2dOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage queryPage) {
    QueryPageLive2DOptions result = new QueryPageLive2DOptions();
    
    QueryPageLive2DAnswersVisibleOption answersVisible = queryPageController.getEnumSetting(queryPage, QueryPageController.LIVE2D_VISIBLE_OPTION, QueryPageLive2DAnswersVisibleOption.class);
    if (answersVisible == null) {
      answersVisible = QueryPageLive2DAnswersVisibleOption.IMMEDIATELY;
    }
    
    result.setAnswersVisible(answersVisible);
    result.setAxisX(createLive2dAxisOptions(queryPage, "x"));
    result.setAxisY(createLive2dAxisOptions(queryPage, "y"));
    
    return result;
  }

  /**
   * Creates live 2d axis options
   * 
   * @param queryPage query page
   * @param axis axis
   * @return live 2d axis options
   */
  private QueryPageLive2DAxis createLive2dAxisOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage queryPage, String axis) {
    QueryPageLive2DAxis result = new QueryPageLive2DAxis();
    QueryPageLive2DColor color = queryPageController.getEnumSetting(queryPage, String.format(QueryPageController.LIVE2D_COLOR_OPTION_TEMPLATE, axis), QueryPageLive2DColor.class);
    
    result.setColor(color);
    result.setLabel(queryPageController.getSetting(queryPage, String.format(QueryPageController.LIVE2D_LABEL_OPTION_TEMPLATE, axis)));
    result.setOptions(queryPageController.getListSetting(queryPage, String.format(QueryPageController.OPTIONS_OPTION_TEMPLATE, axis)));
    
    return result;
  }

}
