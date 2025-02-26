package fi.metatavu.edelphi.comments;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Controller for query question comments
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionCommentController {

  @Inject
  private QueryController queryController;
  
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private ResourceController resourceController;
  
  /**
   * Creates new query question comment
   * 
   * @param queryReply query reply
   * @param queryPage query page
   * @param parentComment parent comment
   * @param category category
   * @param comment comment contents
   * @param hidden whether comment should be hidden
   * @param creator creator
   * @param created create time
   * @return created comment
   */
  public QueryQuestionComment createQueryQuestionComment(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, QueryQuestionCommentCategory category, String comment, Boolean hidden, User creator, Date created) {
    return queryQuestionCommentDAO.create(queryReply, queryPage, parentComment, category, comment, hidden, creator, created, creator, created);
  }
  
  /**
   * Finds query question comment by id
   * 
   * @param id id
   * @return query question comment or null if not found
   */
  public QueryQuestionComment findQueryQuestionCommentById(Long id) {
    return queryQuestionCommentDAO.findById(id);
  }

  /**
   * List all query question comments by reply
   *
   * @param reply reply
   */
  public List<QueryQuestionComment> listAllByReply(QueryReply reply) {
    return queryQuestionCommentDAO.listAllByReply(reply);
  }

  /**
   * List all query question comments by query
   *
   * @param query query
   */
  public List<QueryQuestionComment> listAllByQuery(Query query) {
    return queryQuestionCommentDAO.listAllByQuery(query);
  }

  /**
   * Lists not archived comments by given parameters.
   * 
   * @param panel panel. Required
   * @param stamp filter by panel stamp. Defaults to panel's current stamp
   * @param queryPage filter by comment's query page. Ignored if null
   * @param query filter by query. Ignored if null
   * @param parentComment filter by parent comment. Ignored if null
   * @param user filter by user. Ignored if null.
   * @param onlyRootComments return only root comments. 
   * @param category return only comments of specified category. Ignored if null
   * @param onlyNullCategories return only comments without category. Ignored if null
   * @param firstResult first result
   * @param maxResults max results
   * @param oldestFirst sort by oldest first
   * @return a list of comments
   */
  public List<QueryQuestionComment> listQueryQuestionComments(
    Panel panel,
    PanelStamp stamp,
    QueryPage queryPage,
    Query query,
    QueryQuestionComment parentComment,
    User user,
    boolean onlyRootComments,
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category,
    boolean onlyNullCategories,
    Integer firstResult,
    Integer maxResults,
    boolean oldestFirst
  ) {
    if (stamp == null) {
      stamp = panel.getCurrentStamp();
    }
    
    return queryQuestionCommentDAO.list(
      queryPage,
      stamp,
      query,
      panel.getRootFolder(),
      parentComment,
      onlyRootComments,
      user,
      category,
      onlyNullCategories,
      Boolean.FALSE,
      firstResult,
      maxResults,
      oldestFirst
    );
  }

  /**
   * Counts not archived comments by given parameters.
   *
   * @param panel panel. Required
   * @param stamp filter by panel stamp. Defaults to panel's current stamp
   * @param queryPage filter by comment's query page. Ignored if null
   * @param query filter by query. Ignored if null
   * @param parentComment filter by parent comment. Ignored if null
   * @param user filter by user. Ignored if null.
   * @param onlyRootComments return only root comments.
   * @param category return only comments of specified category. Ignored if null
   * @param onlyNullCategories return only comments without category. Ignored if null
   * @return count of comments
   */
  public Long countQueryQuestionComments(
    Panel panel,
    PanelStamp stamp,
    QueryPage queryPage,
    Query query,
    QueryQuestionComment parentComment,
    User user,
    boolean onlyRootComments,
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category,
    boolean onlyNullCategories
  ) {
    return queryQuestionCommentDAO.count(
      queryPage,
      stamp,
      query,
      panel.getRootFolder(),
      parentComment,
      onlyRootComments,
      user,
      category,
      onlyNullCategories,
      Boolean.FALSE
    );
  }

  /**
   * Counts child comments
   *
   * @param parentComment parent comment
   * @return count of child comments
   */
  public int countChildComments(QueryQuestionComment parentComment) {
    return queryQuestionCommentDAO.count(
      parentComment.getQueryPage(),
      null,
      null,
      null,
      parentComment,
      false,
      null,
      null,
      false,
      false
    ).intValue();
  }

  /**
   * Updates query question comment
   * 
   * @param queryQuestionComment comment to be updated
   * @param comment comment contents
   * @param hidden whether comment should be hidden
   * @param modifier modifier
   * @param modified modification time 
   * @return updated comment
   */
  public QueryQuestionComment updateQueryQuestionComment(QueryQuestionComment queryQuestionComment, QueryQuestionCommentCategory category, String comment, Boolean hidden, User modifier, Date modified) {
    queryQuestionCommentDAO.updateHidden(queryQuestionComment, hidden, modifier);
    queryQuestionCommentDAO.updateCategory(queryQuestionComment, category, modifier, modified);
    return queryQuestionCommentDAO.updateComment(queryQuestionComment, comment, modifier, modified);
  }
  
  /**
   * Archives query question comment
   * 
   * @param comment comment to be archived
   */
  public void archiveQueryQuestionComment(QueryQuestionComment comment) {
    queryQuestionCommentDAO.archive(comment);
  }

  /**
   * Deletes query question comment
   * 
   * @param comment comment to be deleted
   */
  public void deleteQueryQuestionComment(QueryQuestionComment comment) {
    queryQuestionCommentDAO.delete(comment);
  }

  /**
   * Returns whether query question comment is archived or not
   * 
   * @param comment comment
   * @return whether query question comment is archived or not
   */
  public boolean isQueryQuestionCommentArchived(QueryQuestionComment comment) {
    if (comment == null || comment.getArchived()) {
      return true;
    }
    
    if (comment.getParentComment() != null && isQueryQuestionCommentArchived(comment.getParentComment())) {
      return true;
    }
    
    if (comment.getQueryReply().getArchived()) {
      return true;
    }
    
    return queryController.isQueryPageArchived(comment.getQueryPage());
  }

  /**
   * Returns whether comment belongs to given panel
   * 
   * @param comment comment
   * @param panel panel
   * @return whether comment belongs to given panel
   */
  public boolean isPanelsComment(QueryQuestionComment comment, Panel panel) {
    Panel queryPanel = resourceController.getResourcePanel(comment.getQueryPage().getQuerySection().getQuery());
    if (queryPanel == null || panel == null) {
      return false;
    }
    
    return queryPanel.getId().equals(panel.getId());
  }

  /**
   * Returns whether comment is from given panel
   * 
   * @param category category
   * @param panel panel
   * @return whether page is from given panel
   */
  public boolean isPanelsCommentCategory(QueryQuestionCommentCategory category, Panel panel) {
    if (category == null) {
      return false;
    }
    
    return queryController.isPanelsQuery(category.getQuery(), panel);
  }

  public void removeParent(QueryQuestionComment queryQuestionComment) {
    queryQuestionComment.setParentComment(null);
    queryQuestionCommentDAO.persist(queryQuestionComment);
  }
}
