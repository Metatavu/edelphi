package fi.metatavu.edelphi.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ejb3.annotation.SecurityDomain;

import fi.metatavu.edelphi.comments.QueryQuestionCommentController;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.mqtt.MqttController;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.permissions.DelfoiActionName;
import fi.metatavu.edelphi.permissions.PermissionController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryQuestionAnswer;
import fi.metatavu.edelphi.queries.QueryQuestionAnswerData;
import fi.metatavu.edelphi.queries.QueryQuestionLive2dAnswerData;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.queries.QuerySectionController;
import fi.metatavu.edelphi.rest.api.PanelsApi;
import fi.metatavu.edelphi.rest.model.QueryPageLive2DAnswersVisibleOption;
import fi.metatavu.edelphi.rest.model.QueryPageLive2d;
import fi.metatavu.edelphi.rest.model.QueryPageText;
import fi.metatavu.edelphi.rest.model.QueryQuestionAnswerLive2d;
import fi.metatavu.edelphi.rest.model.QueryQuestionComment;
import fi.metatavu.edelphi.rest.model.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.rest.mqtt.QueryQuestionAnswerNotification;
import fi.metatavu.edelphi.rest.mqtt.QueryQuestionCommentNotification;
import fi.metatavu.edelphi.rest.translate.PanelExpertiseClassTranslator;
import fi.metatavu.edelphi.rest.translate.PanelExpertiseGroupTranslator;
import fi.metatavu.edelphi.rest.translate.PanelInterestClassTranslator;
import fi.metatavu.edelphi.rest.translate.PanelTranslator;
import fi.metatavu.edelphi.rest.translate.QueryPageLive2dTranslator;
import fi.metatavu.edelphi.rest.translate.QueryPageTextTranslator;
import fi.metatavu.edelphi.rest.translate.QueryPageTranslator;
import fi.metatavu.edelphi.rest.translate.QueryQuestionAnswerLive2dTranslator;
import fi.metatavu.edelphi.rest.translate.QueryQuestionCommentCategoryTranslator;
import fi.metatavu.edelphi.rest.translate.QueryQuestionCommentTranslator;
import fi.metatavu.edelphi.rest.translate.QueryTranslator;
import fi.metatavu.edelphi.users.UserController;

/**
 * Panel REST Services
 * 
 * @author Antti Leppä
 */
@RequestScoped
@Path("/panels")
@Stateful
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
@SecurityDomain("keycloak")
public class PanelRESTService extends AbstractApi implements PanelsApi {

  private static final String QUERY_QUESTION_COMMENTS_MQTT_CHANNEL = "queryquestioncomments";
  private static final String QUERY_QUESTION_ANSWERS_MQTT_CHANNEL = "queryquestionanswers";

  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Inject
  private QuerySectionController querySectionController;

  @Inject  
  private UserController userController;

  @Inject
  private MqttController mqttController;

  @Inject
  private QueryQuestionCommentController queryQuestionCommentController;

  @Inject
  private QueryQuestionCommentTranslator queryQuestionCommentTranslator;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryQuestionAnswerLive2dTranslator queryQuestionAnswerLive2dTranslator;
  
  @Inject
  private QueryPageController queryPageController;
  
  @Inject
  private QueryPageTranslator queryPageTranslator;

  @Inject
  private QueryPageTextTranslator queryPageTextTranslator;

  @Inject
  private QueryPageLive2dTranslator queryPageLive2dTranslator;
  
  @Inject
  private PermissionController permissionController;

  @Inject
  private QueryQuestionCommentCategoryTranslator queryQuestionCommentCategoryTranslator;

  @Inject
  private PanelTranslator panelTranslator;

  @Inject
  private QueryTranslator queryTranslator;

  @Inject
  private PanelExpertiseClassTranslator panelExpertiseClassTranslator;

  @Inject
  private PanelInterestClassTranslator panelInterestClassTranslator;

  @Inject
  private PanelExpertiseGroupTranslator panelExpertiseGroupTranslator;
  
  @Override
  @RolesAllowed("user")
  public Response createQueryQuestionComment(Long panelId, @Valid QueryQuestionComment body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
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
    Boolean hidden = body.getHidden();
    Date created = new Date(System.currentTimeMillis());
    User creator = getLoggedUser();
    Long categoryId = body.getCategoryId();
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = null;

    if (categoryId != null && categoryId > 0) {
      category = queryPageController.findCommentCategory(categoryId);
      if (category == null) {
        return createBadRequest(String.format("Invalid categoryId", categoryId));
      }
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.createQueryQuestionComment(queryReply, 
        queryPage, 
        parentComment, 
        category,
        contents, 
        hidden, 
        creator, 
        created);

    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.CREATED, panel, comment);
    
    return createOk(queryQuestionCommentTranslator.translate(comment));
  }

  @Override
  @RolesAllowed("user")
  public Response deleteQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound();
    }

    if (!comment.getCreator().getId().equals(getLoggedUser().getId())) {
      if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_QUERY_COMMENTS)) {
        return createForbidden("Forbidden");
      }
    }
    
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound();
    }
    
    if (inTestMode()) {
      queryQuestionCommentController.deleteQueryQuestionComment(comment);
    } else {
      queryQuestionCommentController.archiveQueryQuestionComment(comment);
    }
    
    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.DELETED, panel, comment);

    return createNoContent();
  }

  @Override
  @RolesAllowed("user")
  public Response findQueryQuestionComment(Long panelId, Long commentId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
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
  @RolesAllowed("user")
  public Response listQueryQuestionComments(Long panelId, Long queryId, Long pageId, UUID userId, Long stampId, Long parentId, Long categoryId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
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
    
    User user = userId != null ? userController.findUserByKeycloakId(userId) : null;
    if (userId != null && user == null) {
      return createBadRequest(String.format("Invalid user id %s", userId));
    }

    boolean onlyNullCategories = false;
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = null;

    if (categoryId != null) {
      if (categoryId > 0) {
        category = queryPageController.findCommentCategory(categoryId);
        if (category == null) {
          return createBadRequest(String.format("Invalid categoryId", categoryId));
        }
      } else {
        onlyNullCategories = true;
      }
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment parentComment = null; 
    boolean onlyRootComments = false;
    
    if (parentId != null) {
      if (parentId == 0) {
        onlyRootComments = true;
      } else {
        parentComment = queryQuestionCommentController.findQueryQuestionCommentById(parentId);
        if (parentComment == null) {
          return createBadRequest("Invalid parent comment");
        }
      }
    }
    
    return createOk(queryQuestionCommentController.listQueryQuestionComments(panel, stamp, queryPage, query, parentComment, user, onlyRootComments, category, onlyNullCategories).stream()
      .map(queryQuestionCommentTranslator::translate)
      .collect(Collectors.toList()));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response updateQueryQuestionComment(Long panelId, Long commentId, @Valid QueryQuestionComment body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound(String.format("Panel with id %s not found", panelId));
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment = queryQuestionCommentController.findQueryQuestionCommentById(commentId);
    if (comment == null || queryQuestionCommentController.isQueryQuestionCommentArchived(comment)) {
      return createNotFound(String.format("Comment with id %s not found", commentId));
    }

    if (!comment.getCreator().getId().equals(getLoggedUser().getId())) {
      if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_QUERY_COMMENTS)) {
        return createForbidden("Forbidden");
      }
    }
   
    if (!queryQuestionCommentController.isPanelsComment(comment, panel)) {
      return createNotFound("Comment not found from given panel");
    }
 
    String contents = body.getContents();
    Boolean hidden = body.getHidden();
    Date modified = new Date(System.currentTimeMillis());
    User modifier = getLoggedUser();
    Long categoryId = body.getCategoryId();
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = null;
    
    if (categoryId != null && categoryId > 0) {
      category = queryPageController.findCommentCategory(categoryId);
      if (category == null) {
        return createBadRequest(String.format("Invalid categoryId", categoryId));
      }
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment updatedComment = queryQuestionCommentController.updateQueryQuestionComment(comment, 
        category,
        contents, 
        hidden, 
        modifier, 
        modified);

    publishCommentMqttNotification(QueryQuestionCommentNotification.Type.UPDATED, panel, comment);
    
    return createOk(queryQuestionCommentTranslator.translate(updatedComment));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response findQueryQuestionAnswerLive2d(Long panelId, String answerId) {
    QueryQuestionAnswer<?> answerData = queryReplyController.findQueryQuestionAnswerData(answerId);
    if (answerData == null) {
      return createNotFound();
    }
    
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = answerData.getQueryPage();
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
       
    return createOk(queryQuestionAnswerLive2dTranslator.translate(answerData));
  }

  @Override
  @RolesAllowed("user")  
  public Response listQueryQuestionAnswers(Long panelId, Long queryId, Long pageId, UUID userId, Long stampId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    Query query = queryId != null ? queryController.findQueryById(queryId) : null;
    if (queryId != null && (query == null || queryController.isQueryArchived(query))) {
      return createBadRequest(String.format("Invalid query id %d", queryId));
    }

    QueryPage queryPage = pageId != null ? queryController.findQueryPageById(pageId) : null;
    if (queryPage == null || queryController.isQueryPageArchived(queryPage)) {
      return createBadRequest(String.format("Invalid query page id %d", pageId));
    }
    
    PanelStamp stamp = stampId != null ? panelController.findPanelStampById(stampId) : null;
    if (stampId != null && (stamp == null || panelController.isPanelStampArchived(stamp))) {
      return createBadRequest(String.format("Invalid panel stamp id %d", stampId));
    }
    
    User user = userId != null ? userController.findUserByKeycloakId(userId) : null;
    if (userId != null && user == null) {
      return createBadRequest(String.format("Invalid user id %s", userId));
    }
    
    return createOk(queryReplyController.listQueryQuestionAnswers(queryPage, stamp, query, panel.getRootFolder(), user).stream()
      .map(queryQuestionAnswerLive2dTranslator::translate)
      .collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user")  
  public Response deleteQueryQuestionAnswers(Long panelId, Long queryId, Long queryPageId, Long querySectionId) {
    User loggedUser = getLoggedUser();
    
    if (queryId == null && queryPageId == null && querySectionId == null) {
      return createBadRequest("queryId, queryPageId or querySectionId needs to be specified");
    }
    
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }

    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    if (queryId != null) {
      Query query = queryController.findQueryById(queryId);
      if (queryController.isQueryArchived(query)) {
        return createBadRequest(String.format("Invalid query id %d", queryId));
      }
      
      if (!queryController.isPanelsQuery(query, panel)) {
        return createBadRequest("Panel and query mismatch");
      }
      
      queryReplyController.deleteQueryReplies(query, loggedUser);
    }
    
    if (queryPageId != null) {
      QueryPage queryPage = queryController.findQueryPageById(queryPageId);
      if (queryController.isQueryPageArchived(queryPage)) {
        return createBadRequest(String.format("Invalid query page id %d", queryPageId));
      }
      
      if (!queryPageController.isPanelsPage(panel, queryPage)) {
        return createBadRequest("Panel and page mismatch");
      }
      
      queryReplyController.deleteQueryPageReplies(queryPage, loggedUser);
    }
    
    if (querySectionId != null) {
      QuerySection querySection = querySectionController.findQuerySectionById(querySectionId);
      if (querySection == null) {
        return createBadRequest(String.format("Invalid query section id %d", querySectionId));
      }
      
      if (!querySectionController.isPanelsQuerySection(querySection, panel)) {
        return createBadRequest("Panel and section mismatch");
      }

      queryReplyController.deleteQuerySectionReplies(querySection, loggedUser);
    }
    
    return createNoContent();
  }
  
  @Override
  @RolesAllowed("user")  
  public Response upsertQueryQuestionAnswerLive2d(Long panelId, String answerId, @Valid QueryQuestionAnswerLive2d body) {
    QueryQuestionAnswer<?> answerData = queryReplyController.findQueryQuestionAnswerData(answerId);
    if (answerData == null) {
      return createNotFound();
    }
    
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = answerData.getQueryPage();
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createForbidden("Forbidden");
    }
    
    fi.metatavu.edelphi.rest.model.QueryQuestionLive2dAnswerData data = body.getData();
    QueryQuestionAnswer<QueryQuestionLive2dAnswerData> answer = queryReplyController.setLive2dAnswer(answerData, data.getX(), data.getY());
    publishAnswerMqttNotification(QueryQuestionAnswerNotification.Type.UPDATED, panel, answer);
    
    return createOk(queryQuestionAnswerLive2dTranslator.translate(answer));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response findQueryPageText(Long panelId, Long queryPageId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
    
    return createOk(queryPageTextTranslator.translate(queryPage));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response updateQueryPageText(Long panelId, Long queryPageId, @Valid QueryPageText body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
    
    queryPageController.setSetting(queryPage, QueryPageController.TEXT_CONTENT_OPTION, body.getContent(), loggedUser);  
    
    return createOk(queryPageTextTranslator.translate(queryPage));
  }

  @Override
  @RolesAllowed("user")  
  public Response findQueryPageLive2d(Long panelId, Long queryPageId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
    
    return createOk(queryPageLive2dTranslator.translate(queryPage));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response updateQueryPageLive2d(Long panelId, Long queryPageId, @Valid QueryPageLive2d body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    QueryPage queryPage = queryPageController.findQueryPage(queryPageId);
    if (queryPage == null) {
      return createNotFound();
    }
    
    if (!queryPageController.isPanelsPage(panel, queryPage)) {
      return createNotFound(String.format("Page %d is not from panel %d", queryPage.getId(), panel.getId()));
    }
    
    QueryPageLive2DAnswersVisibleOption answersVisible = body.getAnswersVisible();
    if (answersVisible == null) {
      answersVisible = QueryPageLive2DAnswersVisibleOption.IMMEDIATELY;
    }
    
    queryPageController.setSetting(queryPage, QueryPageController.LIVE2D_VISIBLE_OPTION, answersVisible.toString(), loggedUser);  
    
    return createOk(queryPageLive2dTranslator.translate(queryPage));
  }
  
  @Override
  @RolesAllowed("user")  
  public Response createQueryQuestionCommentCategory(Long panelId, @Valid QueryQuestionCommentCategory body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    Long queryId = body.getQueryId();
    Long queryPageId = body.getQueryPageId();
    String name = body.getName();
    User loggedUser = getLoggedUser();
    
    Query query = queryController.findQueryById(queryId);
    if (query == null) {
      return createBadRequest(String.format("Invalid query id %d", queryId));
    }
    
    QueryPage queryPage = queryPageId != null ? queryPageController.findQueryPage(queryPageId) : null;
    if (queryPageId != null && queryPage == null) {
      return createBadRequest(String.format("Invalid query page id %d", queryPageId));
    }
    
    return createOk(queryQuestionCommentCategoryTranslator.translate(queryPageController.createCommentCategory(query, queryPage, name, loggedUser)));
  }

  @Override
  @RolesAllowed("user") 
  public Response deleteQueryQuestionCommentCategory(Long panelId, Long categoryId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = queryPageController.findCommentCategory(categoryId);
    if (!queryQuestionCommentController.isPanelsCommentCategory(category, panel)) {
      return createBadRequest("Panel and comment mismatch");
    }
    
    queryPageController.deleteCommentCategory(category);
    
    return createNoContent();
  }

  @Override
  @RolesAllowed("user") 
  public Response findQueryQuestionCommentCategory(Long panelId, Long categoryId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = queryPageController.findCommentCategory(categoryId);
    if (!queryQuestionCommentController.isPanelsCommentCategory(category, panel)) {
      return createBadRequest("Panel and comment mismatch");
    }
    
    return createOk(queryQuestionCommentCategoryTranslator.translate(category));
  }
  
  @Override
  @RolesAllowed("user") 
  public Response listQueryQuestionCommentCategories(Long panelId, Long pageId, Long queryId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    if (!permissionController.hasPanelAccess(panel, getLoggedUser(), DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    if ((pageId == null || pageId == 0) && queryId == null) {
      return createBadRequest("Either pageId or queryId is required");
    }
    
    boolean includeAllPages = pageId != null && pageId == 0;
    List<fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory> commentCategories = null;
    
    if (pageId != null && !includeAllPages) {
      QueryPage queryPage = queryPageController.findQueryPage(pageId);
      if (queryPage == null) {
        return createNotFound();
      }
  
      if (!queryPageController.isPanelsPage(panel, queryPage)) {
        return createBadRequest("Panel and page mismatch");
      }
      
      commentCategories = queryPageController.listCommentCategoriesByPage(queryPage, false);
    } else if (queryId != null) {
      Query query = queryController.findQueryById(queryId);
      if (query == null) {
        return createBadRequest(String.format("Invalid query id %d", queryId));
      }
      
      if (!queryController.isPanelsQuery(query, panel)) {
        return createBadRequest("Panel and query mismatch");
      }
      
      commentCategories = queryPageController.listCommentCategoriesByQuery(query, !includeAllPages);
    }
    
    return createOk(commentCategories.stream().map(queryQuestionCommentCategoryTranslator::translate).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user") 
  public Response updateQueryQuestionCommentCategory(Long panelId, Long categoryId, @Valid QueryQuestionCommentCategory body) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory category = queryPageController.findCommentCategory(categoryId);
    if (category == null) {
      return createNotFound(String.format("Category %d not found", categoryId));
    }

    if (!queryQuestionCommentController.isPanelsCommentCategory(category, panel)) {
      return createBadRequest("Panel and comment mismatch");
    }

    return createOk(queryQuestionCommentCategoryTranslator.translate(queryPageController.updateCommentCategory(category, body.getName(), loggedUser)));
  }

  @Override
  @RolesAllowed("user") 
  public Response findPanel(Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    return createOk(panelTranslator.translate(panel));
  }
  
  @Override
  @RolesAllowed("user") 
  public Response listPanels(String urlName) {
    User loggedUser = getLoggedUser();
    
    List<Panel> panels = panelController.listPanels(urlName);
    List<Panel> result = new ArrayList<>();
    
    for (Panel panel : panels) {
      if (permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.ACCESS_PANEL)) {
        result.add(panel);
      }
    }
    
    return createOk(panelTranslator.translate(result));
  }

  @Override
  @RolesAllowed("user") 
  public Response listQueries(Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    List<Query> queries = queryController.listPanelQueries(panel);
    
    return createOk(queries.stream().map(queryTranslator::translate).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user") 
  public Response listQueryPages(Long panelId, Long queryId, Boolean includeHidden) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.ACCESS_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    Query query = queryController.findQueryById(queryId);
    if (query == null) {
      return createNotFound();
    }
    
    if (!queryController.isPanelsQuery(query, panel)) {
      return createNotFound();
    }
    
    if (queryController.isQueryArchived(query)) {
      return createGone();
    }
    
    if (includeHidden && !permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    List<QueryPage> queryPages = queryController.listQueryPages(query, Boolean.TRUE.equals(includeHidden) ? null : true);

    return createOk(queryPages.stream().map(queryPageTranslator::translate).collect(Collectors.toList()));
  }
  
  @Override
  @RolesAllowed("user") 
  public Response listExpertiseClasses(Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    return createOk(panelController.listPanelUserExpertiseClasses(panel).stream().map(this.panelExpertiseClassTranslator::translate).collect(Collectors.toList()));
  }
  
  @Override
  @RolesAllowed("user") 
  public Response listInterestClasses(Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    return createOk(panelController.listPanelUserInterestClasses(panel).stream().map(this.panelInterestClassTranslator::translate).collect(Collectors.toList()));
  }

  @Override
  @RolesAllowed("user") 
  public Response listExpertiseGroups(Long panelId) {
    Panel panel = panelController.findPanelById(panelId);
    if (panel == null || panelController.isPanelArchived(panel)) {
      return createNotFound();
    }
    
    User loggedUser = getLoggedUser();
    if (!permissionController.hasPanelAccess(panel, loggedUser, DelfoiActionName.MANAGE_PANEL)) {
      return createForbidden("Forbidden");
    }
    
    return createOk(panelController.listPanelUserExpertiseGroups(panel, panel.getCurrentStamp()).stream().map(this.panelExpertiseGroupTranslator::translate).collect(Collectors.toList()));
  }

  /**
   * Publishes MQTT notification about comment update 
   * 
   * @param type type
   * @param panel panel
   * @param comment comment
   */
  private void publishCommentMqttNotification(QueryQuestionCommentNotification.Type type, Panel panel, fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment comment) {
    QueryPage page = comment.getQueryPage();
    Query query = page.getQuerySection().getQuery();
    Long commentParentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
    
    mqttController.publish(QUERY_QUESTION_COMMENTS_MQTT_CHANNEL, new QueryQuestionCommentNotification(type, 
        panel.getId(), 
        query.getId(), 
        page.getId(), 
        comment.getId(),
        commentParentId));
    
  }

  /**
   * Publishes MQTT notification about answer update 
   * 
   * @param type type
   * @param panel panel
   * @param answer answer
   */
  private void publishAnswerMqttNotification(QueryQuestionAnswerNotification.Type type, Panel panel, QueryQuestionAnswer<? extends QueryQuestionAnswerData> answer) {
    QueryPage page = answer.getQueryPage();
    QueryReply queryReply = answer.getQueryReply();
    Query query = page.getQuerySection().getQuery();
    String answerId = String.format("%d-%d", page.getId(), queryReply.getId());
    
    mqttController.publish(QUERY_QUESTION_ANSWERS_MQTT_CHANNEL, new QueryQuestionAnswerNotification(type, 
        panel.getId(), 
        query.getId(), 
        page.getId(), 
        answerId));
    
  }

}
