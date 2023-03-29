package fi.metatavu.edelphi.queries;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentCategoryDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.resources.ResourceController;
import fi.metatavu.edelphi.settings.SettingsController;

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

  @Inject
  private SettingsController settingsController;

  @Inject
  private QuerySectionDAO querySectionDAO;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryPageSettingDAO queryPageSettingDAO;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryOptionFieldOptionDAO queryOptionFieldOptionDAO;

  @Inject
  private QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO;

  @Inject
  private QueryQuestionCommentCategoryDAO queryQuestionCommentCategoryDAO;

  /**
   * Creates new query
   * 
   * @param parentFolder parent folder
   * @param name query name
   * @param allowEditReply whether to allow editing of replies
   * @param description description
   * @param state query state
   * @param closes closes
   * @param creator creator
   * @return created query
   */
  public Query createQuery(Folder parentFolder, String name, Boolean allowEditReply, String description, QueryState state, OffsetDateTime closes, User creator) {
    Integer indexNumber = resourceController.getNextIndexNumber(parentFolder);
    String urlName = resourceController.getUniqueUrlName(name, parentFolder);
    Date closesDate = closes != null ? Date.from(closes.toInstant()) : null;
    return queryDAO.create(parentFolder, name, urlName, allowEditReply, description, state, closesDate, indexNumber, creator);
  }

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
   * Deletes query including all data
   * 
   * @param query query to be deleted
   */
  public void deleteQuery(Query query) {
    querySectionDAO.listAllByQuery(query).forEach( querySection -> {
      queryPageDAO.listAllByQuerySection(querySection).forEach(this::deleteQueryPage);
      querySectionDAO.delete(querySection);
    });

    queryReplyDAO.listAllByQuery(query).forEach(queryReplyDAO::delete);
    queryQuestionCommentCategoryDAO.listByQuery(query).forEach(queryQuestionCommentCategoryDAO::delete);
    queryDAO.delete(query);
  }

  /**
   * Deletes query page
   *
   * @param queryPage query page to be deleted
   */
  public void deleteQueryPage(QueryPage queryPage) {
    queryReplyController.deleteQueryPageData(queryPage);
    queryFieldDAO.listAllByQueryPage(queryPage).forEach(this::deleteQueryField);
    queryPageSettingDAO.listByQueryPage(queryPage).forEach(queryPageSettingDAO::delete);
    queryQuestionCommentCategoryDAO.listByQueryPage(queryPage).forEach(queryQuestionCommentCategoryDAO::delete);
    queryPageDAO.delete(queryPage);
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
   * Deletes query reply
   * 
   * @param queryReply query reply
   */
  public void deleteQueryReply(QueryReply queryReply) {
    if (settingsController.isInTestMode()) {
      queryReplyDAO.delete(queryReply);
    } else {
      queryReplyDAO.archive(queryReply);  
    }    
  }
  
  /**
   * Archives an query
   * 
   * @param query query
   */
  public void archiveQuery(Query query) {
    queryDAO.archive(query);
  }

  /**
   * Unarchives an query
   * 
   * @param query query
   */
  public void unarchiveQuery(Query query) {
    queryDAO.unarchive(query);
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

  /**
   * Lists queries in a panel
   * 
   * @param panel panel
   * @return list of panel queries
   */
  public List<Query> listPanelQueries(Panel panel) {
    return queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
  }

  /**
   * Lists query pages by query
   * 
   * @param query query
   * @param visible filter by page visibility
   * @return query pages
   */
  public List<QueryPage> listQueryPages(Query query, Boolean visible) {
    return queryPageDAO.list(query, visible, Boolean.FALSE);
  }

  /**
   * Lists query pages by query and page type
   * 
   * @param query query
   * @param pageType page type
   * @return query pages
   */
  public List<QueryPage> listQueryPagesByType(Query query, QueryPageType pageType) {
    return queryPageDAO.listByQueryAndType(query, pageType);
  }

  /**
   * Deletes a query field
   *
   * @param queryField query field
   */
  private void deleteQueryField(QueryField queryField) {
    if (queryField instanceof QueryOptionField) {
      QueryOptionField queryOptionField = (QueryOptionField) queryField;
      queryOptionFieldOptionGroupDAO.listAllByQueryField(queryOptionField).forEach(queryOptionFieldOptionGroupDAO::delete);
      queryOptionFieldOptionDAO.listAllByQueryField(queryOptionField).forEach(queryOptionFieldOptionDAO::delete);
    }

    queryFieldDAO.delete(queryField);
  }

}
