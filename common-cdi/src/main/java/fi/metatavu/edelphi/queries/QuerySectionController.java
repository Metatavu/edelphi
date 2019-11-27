package fi.metatavu.edelphi.queries;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.settings.SettingsController;

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

  @Inject
  private SettingsController settingsController;

  /**
   * Creates new query section
   * 
   * @param query query
   * @param title title
   * @param sectionNumber section number
   * @param visible visible
   * @param commentable is section commentable
   * @param viewDiscussions are section discussions visible
   * @param creator creator
   * @return created section
   */
  public QuerySection createQuerySection(Query query, String title, Integer sectionNumber, Boolean visible, Boolean commentable, Boolean viewDiscussions, User creator) {
    return querySectionDAO.create(creator, query, title, sectionNumber, visible, commentable, viewDiscussions);
  }

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
  
  /**
   * Deletes query section
   * 
   * @param querySection query section
   */
  public void deleteQuerySection(QuerySection querySection) {
    if (settingsController.isInTestMode()) {
      querySectionDAO.delete(querySection);
    } else {
      querySectionDAO.archive(querySection);
    }
  }

}
