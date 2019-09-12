package fi.metatavu.edelphi.queries;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;

/**
 * Query controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QuerySectionController {

  @Inject
  private QueryController queryController;
  
  @Inject
  private QuerySectionDAO querySectionDAO;

  /**
   * Returns query section by id
   * 
   * @param querySectionId query id
   * @return query or null if not found
   */
  public QuerySection findQuerySectionById(Long querySectionId) {
    return querySectionDAO.findById(querySectionId);
  }

  /**
   * Returns whether query section belongs to given panel
   * 
   * @param querySection query section
   * @param panel panel
   * @return whether query section belongs to given panel
   */
  public boolean isPanelsQuerySection(QuerySection querySection, Panel panel) {
    return queryController.isPanelsQuery(querySection.getQuery(), panel);
  }

}
