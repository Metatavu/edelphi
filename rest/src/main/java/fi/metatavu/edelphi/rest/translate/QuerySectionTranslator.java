package fi.metatavu.edelphi.rest.translate;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.rest.model.QuerySection;

/**
 * Translator for QuerySections
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QuerySectionTranslator extends AbstractQueryPageTranslator<fi.metatavu.edelphi.domainmodel.querylayout.QuerySection, fi.metatavu.edelphi.rest.model.QuerySection> {

  @Override
  public QuerySection translate(fi.metatavu.edelphi.domainmodel.querylayout.QuerySection entity) {
    if (entity == null) {
      return null;
    }
    
    QuerySection result = new QuerySection();
    result.setCommentable(entity.getCommentable());
    result.setId(entity.getId());
    result.setSectionNumber(entity.getSectionNumber());
    result.setTitle(entity.getTitle());
    result.setViewDiscussions(entity.getViewDiscussions());
    result.setVisible(entity.getVisible());
    result.setLastModified(translateDate(entity.getLastModified()));
    result.setLastModifierId(translateUserId(entity.getLastModifier()));
    result.setCreated(translateDate(entity.getCreated()));
    result.setCreatorId(translateUserId(entity.getCreator()));
    
    return result;
  }
  
}
