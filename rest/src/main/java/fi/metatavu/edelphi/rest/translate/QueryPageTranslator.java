package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPage;
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
  
  private static final String LIVE2D_LABEL_OPTION = "live2d.label.%s";
  private static final String LIVE2D_COLOR_OPTION = "live2d.color.%s";
  private static final String OPTIONS_OPTION = "live2d.options.%s";
  
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
    result.setQueryOptions(createOptions(entity));
    result.setType(type);
    
    return result;
  }

  private Object createOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    switch (entity.getPageType()) {
      case LIVE_2D:
        return createLive2dOptions(entity);
      default:
      break;
    }
    
    return null;
  }

  private QueryPageLive2DOptions createLive2dOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage queryPage) {
    QueryPageLive2DOptions result = new QueryPageLive2DOptions();
    result.setAxisX(createLive2dAxisOptions(queryPage, "x"));
    result.setAxisY(createLive2dAxisOptions(queryPage, "y"));
    return result;
  }

  private QueryPageLive2DAxis createLive2dAxisOptions(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage queryPage, String axis) {
    QueryPageLive2DAxis result = new QueryPageLive2DAxis();
    QueryPageLive2DColor color = queryPageController.getEnumSetting(queryPage, String.format(LIVE2D_COLOR_OPTION, axis), QueryPageLive2DColor.class);
    
    result.setColor(color);
    result.setLabel(queryPageController.getSetting(queryPage, String.format(LIVE2D_LABEL_OPTION, axis)));
    result.setOptions(queryPageController.getListSetting(queryPage, String.format(OPTIONS_OPTION, axis)));
    
    return result;
  }

}
