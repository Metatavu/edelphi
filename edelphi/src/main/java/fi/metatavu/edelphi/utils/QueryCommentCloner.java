package fi.metatavu.edelphi.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;

/**
 * Helper class for cloning comments when staping panels
 * 
 * @author Antti Leppä
 */
public class QueryCommentCloner {

  private static final Logger logger = Logger.getLogger(QueryCommentCloner.class.getName());
  private static final int FAILSAFE_COUNT = 10000;
  
  private QueryPage queryPage;
  private Map<Long, QueryQuestionComment> commentMap;
  private Map<Long, QueryReply> replyMap;
  private List<QueryQuestionComment> queue;
  private int iterations = 0;
  private Set<Long> cloneIds;
  
  /**
   * Constructor
   * 
   * @param queryPage query page
   * @param replyMap mapping for old <> new replies
   * @param queryComments comments to be cloned
   */
  public QueryCommentCloner(QueryPage queryPage, Map<Long, QueryReply> replyMap, List<QueryQuestionComment> queryComments) {
    this.queue = new ArrayList<>(queryComments);
    this.commentMap = new HashMap<>();
    this.queryPage = queryPage;
    this.replyMap = replyMap;
    this.cloneIds = new HashSet<>(this.queue.stream().map(QueryQuestionComment::getId).collect(Collectors.toSet()));
    
    Collections.sort(queue, (o1, o2) -> {
      if (o1.getParentComment() == o2.getParentComment()) {
        return 0;
      }
      
      if (o1.getParentComment() == null) {
        return -1;
      }
      
      if (o2.getParentComment() == null) {
        return 1;
      }
      
      return 0;
    });
  }
  
  /**
   * Clones comments
   */
  public void cloneComments() {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    
    while (!queue.isEmpty()) {
      QueryQuestionComment queryComment = queue.remove(0);
      QueryQuestionComment originalParentComment = queryComment.getParentComment();
      
      QueryReply newReply = replyMap.get(queryComment.getQueryReply().getId());
      if (originalParentComment != null && !commentMap.containsKey(originalParentComment.getId())) {
        if (this.cloneIds.contains(originalParentComment.getId())) {
          iterations++;
          if (iterations > FAILSAFE_COUNT) {
            throw new SmvcRuntimeException(EdelfoiStatusCode.NO_PARENT_COMMENT, "Comment cloning failed because parent comment could not be found");
          }

          queue.add(queryComment);
        } else {
          this.cloneIds.remove(queryComment.getId());
          logger.severe(() -> String.format("Could not clone comment %d because parent did not exist on original list", queryComment.getId()));
        }
        
        continue;
      }
      
      QueryQuestionComment parentComment = originalParentComment == null ? null : commentMap.get(originalParentComment.getId());
      QueryQuestionComment newComment = queryQuestionCommentDAO.create(
          newReply,
          queryPage,
          parentComment,
          queryComment.getComment(),
          queryComment.getHidden(),
          queryComment.getCreator(),
          queryComment.getCreated(),
          queryComment.getLastModifier(),
          queryComment.getLastModified());
      
      commentMap.put(queryComment.getId(), newComment);
    }
  }
  
}
