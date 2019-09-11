package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;

/**
 * Translator for QueryQuestionCommentCategory
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionCommentCategoryTranslator extends AbstractTranslator<QueryQuestionCommentCategory, fi.metatavu.edelphi.rest.model.QueryQuestionCommentCategory> {

  @Override
  public fi.metatavu.edelphi.rest.model.QueryQuestionCommentCategory translate(QueryQuestionCommentCategory entity) {
    if (entity == null) {
      return null;
    }
    
    fi.metatavu.edelphi.rest.model.QueryQuestionCommentCategory result = new fi.metatavu.edelphi.rest.model.QueryQuestionCommentCategory();
    result.setId(entity.getId());
    result.setName(entity.getName());
    result.setQueryPageId(entity.getQueryPage() != null ? entity.getQueryPage().getId() : null);
    result.setQueryId(entity.getQuery() != null ? entity.getQuery().getId() : null);
    
    return result;
  }

}
