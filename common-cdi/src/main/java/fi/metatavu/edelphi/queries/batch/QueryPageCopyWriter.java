package fi.metatavu.edelphi.queries.batch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
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
 * Batch item reader for writing query pages
 * 
 * @author Antti Leppä
 */
@Named
public class QueryPageCopyWriter extends TypedItemWriter<QueryPage> {
  
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
  private UserController userController;
  
  @Inject
  @JobProperty
  private Locale locale;

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
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
  }

  @Override
  public void write(List<QueryPage> queryPages) throws Exception {
    User copier = userController.findUserByKeycloakId(loggedUserId);
    Panel targetPanel = panelController.findPanelById(targetPanelId);

    if (targetPanel != null && !targetPanel.getArchived()) {
      Query originalQuery = queryController.findQueryById(queryCopyBatchContext.getOriginalQueryId());
      Panel sourcePanel = resourceController.getResourcePanel(originalQuery);
      Query newQuery = queryController.findQueryById(queryCopyBatchContext.getNewQueryId());

      Map<Long, Long> queryCommentCategoryIdMap = queryCopyBatchContext.getQueryCommentCategoryIdMap();
      Map<Long, Long> queryReplyIdMap = queryCopyBatchContext.getQueryReplyIdMap();
      Map<Long, Long> querySectionIdMap = queryCopyBatchContext.getQuerySectionIdMap();

      Map<Long, QueryQuestionCommentCategory> queryCommentCategoryMap = new HashMap<>(queryCommentCategoryIdMap.size());
      Map<Long, QueryReply> replyMap = new HashMap<>(queryReplyIdMap.size());

      for (Entry<Long, Long> entry : queryCommentCategoryIdMap.entrySet()) {
        queryCommentCategoryMap.put(entry.getKey(), queryPageController.findCommentCategory(entry.getValue()));
      }

      for (Entry<Long, Long> entry : queryReplyIdMap.entrySet()) {
        replyMap.put(entry.getKey(), queryReplyController.findQueryReply(entry.getValue()));
      }

      List<QueryReply> originalQueryReplies = queryReplyIdMap.keySet().stream().map(queryReplyController::findQueryReply).collect(Collectors.toList());

      for (QueryPage originalQueryPage : queryPages) {
        logger.info("Writing copy of query page {}", originalQueryPage.getId());

        QuerySection newQuerySection = querySectionController.findQuerySectionById(querySectionIdMap.get(originalQueryPage.getQuerySection().getId()));

        QueryPage newQueryPage = queryPageController.copyQueryPage(originalQueryPage,
          targetPanel,
          sourcePanel,
          newQuery,
          originalQueryReplies,
          newQuerySection,
          copyAnswers,
          copyComments,
          replyMap,
          queryCommentCategoryMap,
          copier);

        queryCopyBatchContext.setQueryPageId(originalQueryPage.getId(), newQueryPage.getId());
      }
    }
  }
  

}
