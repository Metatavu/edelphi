package fi.metatavu.edelphi.queries.batch;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.batch.i18n.BatchMessages;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.queries.QuerySectionController;
import fi.metatavu.edelphi.resources.ResourceController;
import fi.metatavu.edelphi.users.UserController;

/**
 * Batch item writer for writing a copy of an query
 * 
 * @author Antti Leppä
 */
@Named
public class QueryCopyWriter extends TypedItemWriter<Query> {
  
  @Inject
  private Logger logger;

  @Inject
  private QueryCopyBatchContext queryCopyBatchContext;

  @Inject
  private QueryController queryController; 

  @Inject
  private QueryPageController queryPageController; 
  
  @Inject
  private QueryReplyController queryReplyController; 

  @Inject
  private QuerySectionController querySectionController; 
  
  @Inject
  private ResourceController resourceController; 

  @Inject
  private PanelController panelController;

  @Inject
  private UserController UserController;

  @Inject
  private BatchMessages batchMessages;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long queryId;

  @Inject
  @JobProperty
  private UUID loggedUserId;
  
  @Inject
  @JobProperty
  private Boolean copyAnswers;

  @Inject
  @JobProperty
  private Boolean copyComments;

  @Inject
  @JobProperty
  private Long targetPanelId;

  @Inject
  @JobProperty
  private String newName;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
  }

  @Override
  public void write(List<Query> items) throws Exception {
    if (items.size() != 1) {
      throw new CopyQueryException("QueryCopyWriter supports only single query batches");
    }

    Query originalQuery = items.get(0);
    Panel targetPanel = panelController.findPanelById(targetPanelId);
    User user = UserController.findUserByKeycloakId(loggedUserId);
    
    queryCopyBatchContext.setOriginalQueryId(originalQuery.getId());
    Query newQuery = copyQuery(originalQuery, targetPanel, user);
    queryCopyBatchContext.setNewQueryId(newQuery.getId());
  }

  /**
   * Makes a copy of a query
   * 
   * @param originalQuery original query
   * @param targetPanel target panel
   * @param copier copying user
   * @return copy of a query
   * @throws CopyQueryException when copying fails
   */
  private Query copyQuery(Query originalQuery, Panel targetPanel, User copier) throws CopyQueryException {
    logger.info("Writing copy of query {}", originalQuery.getId());
    
    Panel sourcePanel = resourceController.getResourcePanel(originalQuery); 
    List<QueryPage> expertisePages = queryController.listQueryPagesByType(originalQuery, QueryPageType.EXPERTISE); 
    if (!expertisePages.isEmpty() && !sourcePanel.getId().equals(targetPanel.getId())) {
      throw new CopyQueryException(batchMessages.getText(locale, "batch.copyQuery.error.cannotCopyExpertiseQuery"));
    }
    
    Query newQuery = queryController.createQuery(targetPanel.getRootFolder(), 
        newName, 
        originalQuery.getAllowEditReply(), 
        originalQuery.getDescription(), 
        originalQuery.getState(), 
        getOffsetDateTime(originalQuery.getCloses()), 
        copier
    );
    
    // New query is in archived mode until it's completely copied
    
    queryController.archiveQuery(newQuery);
    

    // Copy query scoped comment categories
    
    List<QueryQuestionCommentCategory> queryCommentCategories = queryPageController.listCommentCategoriesByQuery(originalQuery, true);
    for (QueryQuestionCommentCategory queryCommentCategory : queryCommentCategories) {
      QueryQuestionCommentCategory newCategory = queryPageController.createCommentCategory(newQuery, null, queryCommentCategory.getName(), copier);
      queryCopyBatchContext.setQueryCommentCategoryId(queryCommentCategory.getId(), newCategory.getId());
    }

    // Replies
    
    if (copyAnswers) {
      if (sourcePanel.getId().equals(targetPanel.getId())) {
        // When copying within the same panel, copy all replies of all stamps
        
        List<QueryReply> queryReplies = queryReplyController.listQueryRepliesInAllStamps(originalQuery);
        for (QueryReply queryReply : queryReplies) {
          PanelStamp stamp = queryReply.getStamp();
          QueryReply newReply = queryReplyController.copyQueryReply(queryReply, newQuery, stamp);
          queryCopyBatchContext.setQueryReplyId(queryReply.getId(), newReply.getId());
        }
      } else {
        // When copying between panels, only copy the replies of the latest source panel stamp to the latest target panel stamp
        PanelStamp stamp = targetPanel.getCurrentStamp();

        List<QueryReply> queryReplies = queryReplyController.listQueryReplies(originalQuery, sourcePanel.getCurrentStamp());
        for (QueryReply queryReply : queryReplies) {
          QueryReply newReply = queryReplyController.copyQueryReply(queryReply, newQuery, stamp);
          queryCopyBatchContext.setQueryReplyId(queryReply.getId(), newReply.getId());
        }
      }
    }

    // Sections
    
    List<QuerySection> querySections = querySectionController.listSectionsByQuery(originalQuery);
    for (QuerySection querySection : querySections) {
      QuerySection newQuerySection = querySectionController.createQuerySection(newQuery, querySection.getTitle(), querySection.getSectionNumber(), querySection.getVisible(), querySection.getCommentable(), querySection.getViewDiscussions(), copier);
      queryCopyBatchContext.setQuerySectionId(querySection.getId(), newQuerySection.getId());
    }
    
    return newQuery;
  }

  /**
   * Returns Date as OffsetDateTime 
   * 
   * @param date date
   * @return OffsetDateTime
   */
  private OffsetDateTime getOffsetDateTime(Date date) {
    if (date == null) {
      return null;
    }
    
    return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }
  
}
