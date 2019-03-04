package fi.metatavu.edelphi.rest;

import java.util.Date;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.metatavu.edelphi.comments.QueryQuestionCommentController;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.rest.api.PanelsApi;
import fi.metatavu.edelphi.rest.model.QueryQuestionComment;
import fi.metatavu.edelphi.rest.translate.QueryQuestionCommentTranslator;

/**
 * Panel REST Services
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Stateful
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class PanelRESTService extends AbstractApi implements PanelsApi {

  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Inject
  private QueryQuestionCommentController queryQuestionCommentController;
  
  @Inject
  private QueryQuestionCommentTranslator queryQuestionCommentTranslator;

  @Override
  public Response createQueryQuestionComment(QueryQuestionComment body, Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment parentComment = body.getParentId() != null ? queryQuestionCommentController.findQueryQuestionCommentById(body.getParentId()) : null;
    if (body.getParentId() != null && parentComment == null) {
      return createBadRequest(String.format("Invalid parent id %d", body.getParentId()));
    }
    
    QueryPage queryPage = queryController.findQueryPageById(body.getQueryPageId());
    if (queryPage == null) {
      return createBadRequest(String.format("Invalid query page id %d", body.getQueryPageId()));
    }
    
    QueryReply queryReply = queryController.findQueryReplyById(body.getQueryReplyId());
    if (queryReply == null) {
      return createBadRequest(String.format("Invalid query reply id %d", body.getQueryPageId()));
    }

    if (parentComment != null && !parentComment.getQueryPage().getId().equals(queryPage.getId())) {
      return createBadRequest(String.format("Invalid parent id %d", body.getParentId()));
    }
    
    Query pageQuery = queryPage.getQuerySection().getQuery();
    Query replyQuery = queryReply.getQuery();
    
    if (!pageQuery.getId().equals(replyQuery.getId())) {
      return createBadRequest("Reply and page mismatch");
    }
    
    if (!queryController.isPanelsQuery(pageQuery, panel)) {
      return createBadRequest("Panel and query mismatch");
    }
        
    String contents = body.getContents();
    Boolean hidden = body.isisHidden();
    Date created = new Date(System.currentTimeMillis());
    User creator = getLoggedUser();
    
    return createOk(queryQuestionCommentTranslator.translate(queryQuestionCommentController.createQueryQuestionComment(queryReply, queryPage, parentComment, contents, hidden, creator, created)));
  }

  @Override
  public Response deleteQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
    
    if (inTestMode()) {
      queryQuestionCommentController.deleteQueryQuestionComment(comment);
    } else {
      queryQuestionCommentController.archiveQueryQuestionComment(comment);
    }
    
    return createNoContent();
  }

  @Override
  public Response findQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
    
    return createOk(queryQuestionCommentTranslator.translate(comment));
  }

  @Override
  public Response listQueryQuestionComments(Long panelId, Long queryId, Long pageId, Long stampId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    Query query = queryId != null ? queryController.findQueryById(queryId) : null;
    if (queryId != null && (query == null || queryController.isQueryArchived(query))) {
      return createBadRequest(String.format("Invalid query id %d", queryId));
    }

    QueryPage queryPage = pageId != null ? queryController.findQueryPageById(pageId) : null;
    if (pageId != null && (queryPage == null || queryController.isQueryPageArchived(queryPage))) {
      return createBadRequest(String.format("Invalid query page id %d", pageId));
    }
    
    PanelStamp stamp = stampId != null ? panelController.findPanelStampById(stampId) : null;
    if (stampId != null && (stamp == null || panelController.isPanelStampArchived(stamp))) {
      return createBadRequest(String.format("Invalid panel stamp id %d", stampId));
    }
    
    return createOk(queryQuestionCommentController.listQueryQuestionComments(panel, stamp, queryPage, query).stream()
      .map(queryQuestionCommentTranslator::translate)
      .collect(Collectors.toList()));
  }

  @Override
  public Response updateQueryQuestionComment(QueryQuestionComment body, Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
 
    String contents = body.getContents();
    Boolean hidden = body.isisHidden();
    Date modified = new Date(System.currentTimeMillis());
    User modifier = getLoggedUser();
    
    return createOk(queryQuestionCommentTranslator.translate(queryQuestionCommentController.updateQueryQuestionComment(comment, contents, hidden, modifier, modified)));
  }

  
}
