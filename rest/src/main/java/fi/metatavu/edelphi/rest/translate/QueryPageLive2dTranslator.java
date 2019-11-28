package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DAnswersVisibleOption;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DAxis;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DColor;
import fi.metatavu.edelphi.rest.model.QueryPageType;

/**
 * Translator for live2d query pages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageLive2dTranslator extends AbstractQueryPageTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QueryPage, fi.metatavu.edelphi.rest.model.QueryPageLive2d> {

  @Inject
  private QueryPageController queryPageController;
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryPageLive2d translate(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    if (entity == null) {
      return null;
    }
    
    QueryPageType type = QueryPageType.fromValue(entity.getPageType().name());
    
    fi.metatavu.edelphi.rest.model.QueryPageLive2d result = new fi.metatavu.edelphi.rest.model.QueryPageLive2d();
    result.setId(entity.getId());
    result.setTitle(entity.getTitle());
    result.setPageNumber(entity.getPageNumber());
    result.setType(type);
    result.setCommentOptions(createCommentOptions(entity));
    
    QueryPageLive2DAnswersVisibleOption answersVisible = queryPageController.getEnumSetting(entity, QueryPageController.LIVE2D_VISIBLE_OPTION, QueryPageLive2DAnswersVisibleOption.class);
    if (answersVisible == null) {
      answersVisible = QueryPageLive2DAnswersVisibleOption.IMMEDIATELY;
    }
    
    result.setAnswersVisible(answersVisible);
    result.setAxisX(createLive2dAxisOptions(entity, "x"));
    result.setAxisY(createLive2dAxisOptions(entity, "y"));
    
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
    result.setOptions(queryPageController.getListSetting(queryPage, String.format(QueryPageController.LIVE2D_OPTIONS_OPTION_TEMPLATE, axis)));
    
    return result;
  }

}
