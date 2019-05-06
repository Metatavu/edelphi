package fi.metatavu.edelphi.queries;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Query controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryController {
  
  @Inject
  private QueryDAO queryDAO;

  @Inject
  private QueryPageDAO queryPageDAO;

  @Inject
  private QueryReplyDAO queryReplyDAO;
  
  @Inject
  private ResourceController resourceController;

  /**
   * Returns query by id
   * 
   * @param queryId query id
   * @return query or null if not found
   */
  public Query findQueryById(Long queryId) {
    return queryDAO.findById(queryId);
  }

  /**
   * Returns query page by id
   * 
   * @param pageId query page id
   * @return query page or null if not found
   */
  public QueryPage findQueryPageById(Long pageId) {
    return queryPageDAO.findById(pageId);
  }

  /**
   * Returns query reply by id
   * 
   * @param queryReplyId query reply id
   * @return query reply or null if not found
   */
  public QueryReply findQueryReplyById(Long queryReplyId) {
    return queryReplyDAO.findById(queryReplyId);
  }

  /**
   * Returns whether query page is archived or not
   * 
   * @param queryPage query page
   * @return whether query page is archived or not
   */
  public boolean isQueryPageArchived(QueryPage queryPage) {
    if (queryPage.getArchived()) {
      return true;
    }
    
    QuerySection querySection = queryPage.getQuerySection();
    if (querySection.getArchived()) {
      return true;
    }
    
    return isQueryArchived(querySection.getQuery());
  }

  /**
   * Returns whether query is archived
   * 
   * @param query query
   * @return whether query is archived
   */
  public boolean isQueryArchived(Query query) {
    if (query.getArchived()) {
      return true;
    }
    
    return resourceController.isFolderArchived(query.getParentFolder());
  }
  
  /**
   * Returns whether query belongs to given panel
   * 
   * @param query query
   * @param panel panel
   * @return whether query belongs to given panel
   */
  public boolean isPanelsQuery(Query query, Panel panel) {
    Panel queryPanel = resourceController.getResourcePanel(query);
    if (queryPanel == null || panel == null) {
      return false;
    }
    
    return queryPanel.getId().equals(panel.getId());
  }
}
