package fi.metatavu.edelphi.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.queries.QueryFieldController;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.rest.model.QueryPageScale1dAnswerType;
import fi.metatavu.edelphi.rest.model.QueryPageType;

/**
 * Translator for scale1d query pages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageScale1dTranslator extends AbstractQueryPageTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QueryPage, fi.metatavu.edelphi.rest.model.QueryPageScale1d> {

  @Inject
  private Logger logger;
  
  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldController queryFieldController;
  
  @Override
  public fi.metatavu.edelphi.rest.model.QueryPageScale1d translate(fi.metatavu.edelphi.domainmodel.querylayout.QueryPage entity) {
    if (entity == null) {
      return null;
    }
    
    QueryPageType type = QueryPageType.fromValue(entity.getPageType().name());
    
    fi.metatavu.edelphi.rest.model.QueryPageScale1d result = new fi.metatavu.edelphi.rest.model.QueryPageScale1d();
    result.setId(entity.getId());
    result.setTitle(entity.getTitle());
    result.setPageNumber(entity.getPageNumber());
    result.setType(type);
    result.setCommentOptions(createCommentOptions(entity));
    
    QueryOptionField queryField = queryFieldController.findQueryOptionField(entity, QueryFieldController.SCALE1D_FIELD_NAME);
    if (queryField == null) {
      logger.warn("Could not find query field {} from scale1d query", QueryFieldController.SCALE1D_FIELD_NAME);
      return null;
    }
    
    List<String> options = queryFieldController.listQueryOptionFieldOptions(queryField).stream()
      .map(QueryOptionFieldOption::getValue)
      .collect(Collectors.toList());
    
    result.setOptions(options);
    result.setAnswerType(queryPageController.getEnumSetting(entity, QueryPageController.SCALE1D_TYPE, QueryPageScale1dAnswerType.class));
    result.setLabel(queryPageController.getSetting(entity, QueryPageController.SCALE1D_LABEL));
    
    return result;
  }
  
}
