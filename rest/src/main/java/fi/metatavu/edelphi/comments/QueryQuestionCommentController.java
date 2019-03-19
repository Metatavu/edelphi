package fi.metatavu.edelphi.comments;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
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
   * @param comment comment contents
   * @param hidden whether comment should be hidden
   * @param creator creator
   * @param created create time
   * @return created comment
   */
  public QueryQuestionComment createQueryQuestionComment(QueryReply queryReply, QueryPage queryPage, QueryQuestionComment parentComment, String comment, Boolean hidden, User creator, Date created) {
    return queryQuestionCommentDAO.create(queryReply, queryPage, parentComment, comment, hidden, creator, created, creator, created);
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
   * Lists not archived comments by given parameters.
   * 
   * @param panel panel. Required
   * @param stamp filter by panel stamp. Defaults to panel's current stamp
   * @param queryPage filter by comment's query page. Ignored if null
   * @param query filter by query. Ignored if null
   * @param parentComment filter by parent comment. Ignored if null
   * @param onlyRootComments return only root comments. 
   * @return a list of comments
   */
  public List<QueryQuestionComment> listQueryQuestionComments(Panel panel, PanelStamp stamp, QueryPage queryPage, Query query, QueryQuestionComment parentComment, boolean onlyRootComments) {
    if (stamp == null) {
      stamp = panel.getCurrentStamp();
    }
    
    return queryQuestionCommentDAO.list(queryPage, stamp, query, panel.getRootFolder(), parentComment, onlyRootComments, Boolean.FALSE);
  }

  /**
   * Updates query question comment
   * 
   * @param queryQuestionComment comment to be updated
   * @param comment comment contents
   * @param hidden whether comment should be hidden
   * @param modifier modifier
   * @param modified modification time 
   * @return
   */
  public QueryQuestionComment updateQueryQuestionComment(QueryQuestionComment queryQuestionComment, String comment, Boolean hidden, User modifier, Date modified) {
    queryQuestionCommentDAO.updateHidden(queryQuestionComment, hidden, modifier);
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

}
