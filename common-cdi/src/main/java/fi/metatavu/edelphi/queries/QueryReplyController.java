package fi.metatavu.edelphi.queries;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Controller for query replies
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryReplyController {

  private static final String LIVE_2D_FIELD_Y = "y";
  private static final String LIVE_2D_FIELD_X = "x";

  @Inject
  private Logger logger;

  @Inject
  private ResourceController resourceController;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;

  @Inject
  private QueryPageDAO queryPageDAO;

  @Inject
  private QueryReplyDAO queryReplyDAO;
  
  @Inject
  private PanelExpertiseGroupUserDAO expertiseGroupUserDAO;
    
  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;
  
  @Inject
  private QueryQuestionAnswerDAO queryQuestionAnswerDAO;
  
  @Inject
  private QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO;

  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;
  
  @Inject
  private QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO;
  
  @Inject
  private QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO;

  @Inject
  private QuerySectionDAO querySectionDAO;

  /**
   * Copy query reply into new query
   * 
   * @param queryReply reply
   * @param newQuery new query
   * @param stamp stamp
   * @return new reply
   */
  public QueryReply copyQueryReply(QueryReply queryReply, Query newQuery, PanelStamp stamp) {
    return queryReplyDAO.create(
      queryReply.getUser(),
      newQuery,
      stamp,
      queryReply.getComplete(),
      queryReply.getCreator(),
      queryReply.getCreated(),
      queryReply.getLastModifier(),
      queryReply.getLastModified()
    );
  }
  
  /**
   * Finds a query answer data object by id
   * 
   * @param answerId answer id
   * @return query answer data object or null if not found
   */
  public QueryQuestionAnswer<?> findQueryQuestionAnswerData(String answerId) {
    String[] parts = StringUtils.split(answerId, "-", 3);
    if (parts.length != 2) {
      logger.warn("Invalid answerId {}", answerId);
      return null;
    }
    
    try {
      Long queryPageId = NumberUtils.createLong(parts[0]);
      Long queryReplyId = NumberUtils.createLong(parts[1]);
      
      QueryPage queryPage = queryPageDAO.findById(queryPageId);
      QueryReply queryReply = queryReplyDAO.findById(queryReplyId);

      if (queryPage != null && queryReply != null) {
        return new QueryQuestionAnswer<>(queryReply, queryPage, getAnswerData(queryPage, queryReply));
      }
    } catch (NumberFormatException e) {
      logger.warn("Invalid number in answerId {}", answerId);
    }
    
    return null;
  }
  
  /**
   * Returns single query reply
   * 
   * @param id id
   * @return query reply
   */
  public QueryReply findQueryReply(Long id) {
    return queryReplyDAO.findById(id);
  }
  
  /**
   * Returns replies by query and stamp
   * 
   * @param query query
   * @param panelStamp stamp
   * @return replies
   */
  public List<QueryReply> listQueryReplies(Query query, PanelStamp panelStamp) {
    return queryReplyDAO.listByQueryAndStamp(query, panelStamp);
  }

  /**
   * Returns replies in all stamps of given query
   * 
   * @param query query
   * @return replies in all stamps of given query
   */
  public List<QueryReply> listQueryRepliesInAllStamps(Query query) {
    return queryReplyDAO.listByQueryAndArchived(query, Boolean.FALSE);
  }
  
  /**
   * Filters reply list by user's membership in given list of expertise groups
   * 
   * @param replies replies
   * @param expertiseGroups expertise groups
   * @return filtered replies
   */
  public List<QueryReply> filterQueryRepliesByExpertiseGroup(List<QueryReply> replies, List<PanelUserExpertiseGroup> expertiseGroups) {
    Set<Long> expertiseGroupUserIds = expertiseGroupUserDAO.listUsersByExpertiseGroups(expertiseGroups).stream()
      .map(User::getId)
      .collect(Collectors.toSet());
    
    return replies.stream()
      .filter(reply -> reply.getUser() != null)
      .filter(reply -> expertiseGroupUserIds.contains(reply.getUser().getId()))
      .collect(Collectors.toList());
  }
  
  /**
   * Filters reply list by user's membership in given list of panel user groups
   * 
   * @param replies replies
   * @param panelUserGroups panel user groups
   * @return filtered replies
   */
  public List<QueryReply> filterQueryRepliesByPanelUserGroup(List<QueryReply> replies, List<PanelUserGroup> panelUserGroups) {
    Set<Long> userIds = panelUserGroups.stream()
      .map(PanelUserGroup::getUsers)
      .flatMap(List::stream)
      .map(User::getId)
      .collect(Collectors.toSet());
       
    return replies.stream()
      .filter(reply -> reply.getUser() != null)
      .filter(reply -> userIds.contains(reply.getUser().getId()))
      .collect(Collectors.toList());
  }

  /**
   * Lists query question answers
   * 
   * @param queryPage query page
   * @param stamp filter by stamp (optional)
   * @param query filter by query (optional)
   * @param queryParentFolder filter by query parent folder (optional)
   * @param user filter by user (optional)
   * @return found answers
   */
  public List<QueryQuestionAnswer<? extends QueryQuestionAnswerData>> listQueryQuestionAnswers(QueryPage queryPage, PanelStamp stamp, Query query, Folder queryParentFolder, User user) {
    switch (queryPage.getPageType()) {
      case LIVE_2D:
        return listLive2dQueryQuestionAnswers(queryPage, stamp, query, queryParentFolder, user);
      default:
    }
    
    return Collections.emptyList();
  }
  
  /**
   * Deletes query page replies
   * 
   * @param queryPage query page
   * @param user deleting user
   */
  public void deleteQueryPageReplies(QueryPage queryPage, User user) {
    logger.warn("RemoveAnswersQueryPage - userId: {}, queryPageId: {}", user.getId(), queryPage.getId());
    Panel panel = resourceController.getResourcePanel(queryPage.getQuerySection().getQuery());
    removeAnswers(queryPage, panel.getCurrentStamp());
  }
  
  /**
   * Deletes query section replies
   * 
   * @param querySection query section
   * @param user deleting user
   */
  public void deleteQuerySectionReplies(QuerySection querySection, User user) {
    logger.warn("RemoveAnswersQuerySection - userId: {}, querySectionId: {}", user.getId(), querySection.getId());
    Panel panel = resourceController.getResourcePanel(querySection.getQuery());
    removeAnswers(querySection, panel.getCurrentStamp());
  }
  
  /**
   * Deletes query replies
   * 
   * @param query query
   * @param user deleting user
   */
  public void deleteQueryReplies(Query query, User user) {
    logger.warn("RemoveAnswersQuery - userId: {}, queryId: {}", user.getId(), query.getId());
    
    Panel panel = resourceController.getResourcePanel(query);
    List<QueryReply> replies = queryReplyDAO.listByQueryAndStamp(query, panel.getCurrentStamp());
    for (QueryReply reply : replies) {
      queryReplyDAO.archive(reply, user);
    }
    
    List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
    for (QueryPage queryPage : queryPages) {
      List<QueryQuestionComment> comments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, panel.getCurrentStamp());
      for (QueryQuestionComment comment : comments) {
        queryQuestionCommentDAO.archive(comment);
      }
    }
  }

  /**
   * Sets value for live 2d query page
   * 
   * @param answerData answer data object
   * @param x new x
   * @param y new y
   * @return updated / created answer data
   */
  public QueryQuestionAnswer<QueryQuestionLive2dAnswerData> setLive2dAnswer(QueryQuestionAnswer<?> answerData, Double x, Double y) {
    setNumericAnswer(answerData.getQueryPage(), answerData.getQueryReply(), LIVE_2D_FIELD_X, x);
    setNumericAnswer(answerData.getQueryPage(), answerData.getQueryReply(), LIVE_2D_FIELD_Y, y);
    return new QueryQuestionAnswer<QueryQuestionLive2dAnswerData>(answerData.getQueryReply(), answerData.getQueryPage(), new QueryQuestionLive2dAnswerData(x, y));
  }

  /**
   * Deletes all data from query page including archived answers and comments
   *
   * @param queryPage query page
   */
  public void deleteQueryPageData(QueryPage queryPage) {
    queryFieldDAO.listAllByQueryPage(queryPage).forEach(this::deleteQueryFieldAnswers);
    queryQuestionCommentDAO.listAllByQueryPage(queryPage).forEach(queryQuestionCommentDAO::delete);
  }
  
  /**
   * Lists query question answers for live 2d queries
   * 
   * @param queryPage query page
   * @param stamp filter by stamp (optional)
   * @param query filter by query (optional)
   * @param queryParentFolder filter by query parent folder (optional)
   * @param user filter by user (optional)
   * @return found answers
   */
  private List<QueryQuestionAnswer<? extends QueryQuestionAnswerData>> listLive2dQueryQuestionAnswers(QueryPage queryPage, PanelStamp stamp, Query query, Folder queryParentFolder, User user) {
    List<QueryQuestionNumericAnswer> answers = queryQuestionNumericAnswerDAO.list(queryPage, stamp, query, queryParentFolder, user, Boolean.FALSE);
    Map<Long, QueryQuestionLive2dAnswerData> dataMap = new HashMap<>();
    Map<Long, QueryReply> replyMap = new HashMap<>();
    
    for (QueryQuestionNumericAnswer answer : answers) {
      QueryReply reply = answer.getQueryReply();
      Long replyId = reply.getId();
      if (!dataMap.containsKey(replyId)) {
        dataMap.put(replyId, new QueryQuestionLive2dAnswerData(null, null));
        replyMap.put(replyId, reply);
      }
      
      QueryQuestionLive2dAnswerData answerData = dataMap.get(replyId);
      String fieldName = answer.getQueryField().getName();
      
      if (LIVE_2D_FIELD_X.equals(fieldName)) {
        answerData.setX(answer.getData());
      } else if (LIVE_2D_FIELD_Y.equals(fieldName)) {
        answerData.setY(answer.getData());
      } else {
        logger.error("Live 2d query page contained field {}", fieldName);
      }
    }
    
    return dataMap.entrySet().stream().map(entry -> {
      Long replyId = entry.getKey();
      QueryReply queryReply = replyMap.get(replyId);
      QueryQuestionLive2dAnswerData data = entry.getValue();
      return new QueryQuestionAnswer<QueryQuestionAnswerData>(queryReply, queryPage, data);
    }).collect(Collectors.toList());
  }
  
  /**
   * Returns answer data for a query page
   * 
   * @param queryPage query page
   * @param queryReply query reply
   * @return answer data for a query page
   */
  private QueryQuestionAnswerData getAnswerData(QueryPage queryPage, QueryReply queryReply) {
    switch (queryPage.getPageType()) {
      case LIVE_2D:
        return new QueryQuestionLive2dAnswerData(getLive2dAnswerX(queryPage, queryReply), getLive2dAnswerY(queryPage, queryReply));
      default:
    }
    
    return null;
  }

  /**
   * Returns single x axis answer for a live 2d query
   * 
   * @param queryPage query page
   * @param queryReply query reply
   * @return value
   */
  private Double getLive2dAnswerX(QueryPage queryPage, QueryReply queryReply) {
    return getNumericAnswer(queryPage, queryReply, LIVE_2D_FIELD_X);
  }

  /**
   * Returns single y axis answer for a live 2d query
   * 
   * @param queryPage query page
   * @param queryReply query reply
   * @return value
   */
  private Double getLive2dAnswerY(QueryPage queryPage, QueryReply queryReply) {
    return getNumericAnswer(queryPage, queryReply, LIVE_2D_FIELD_Y);
  }

  /**
   * Returns single numeric answer
   * 
   * @param queryPage query page
   * @param queryReply query reply
   * @param fieldName field name
   * @return single numeric answer or null if not found
   */
  private Double getNumericAnswer(QueryPage queryPage, QueryReply queryReply, String fieldName) {
    QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
    if ((numericAnswer != null) && (numericAnswer.getData() != null)) {
      return numericAnswer.getData();
    }
    
    return null;
  }
  
  /**
   * Sets numeric answer value
   * 
   * @param queryPage query page
   * @param queryReply query reply
   * @param fieldName field name
   * @param value value
   * @return updated answer object
   */
  private QueryQuestionNumericAnswer setNumericAnswer(QueryPage queryPage, QueryReply queryReply, String fieldName, Double value) {
    QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
    Date now = new Date();

    if (numericAnswer == null) {
      return queryQuestionNumericAnswerDAO.create(queryReply, queryField, value, now, now);
    } else {
      return queryQuestionNumericAnswerDAO.updateData(numericAnswer, value);
    }
  }
  
  /**
   * Deletes answers from query section
   * 
   * @param querySection query section
   * @param stamp panel stamp
   */
  private void removeAnswers(QuerySection querySection, PanelStamp stamp) {
    List<QueryPage> queryPages = queryPageDAO.listByQuerySection(querySection);
    for (QueryPage queryPage : queryPages) {
      removeAnswers(queryPage, stamp);
    }
  }

  /**
   * Removes query page replies
   * 
   * @param queryPage query page
   * @param stamp panel stamp
   */
  private void removeAnswers(QueryPage queryPage, PanelStamp stamp) {
    List<QueryField> queryFields = queryFieldDAO.listByQueryPage(queryPage);
    for (QueryField queryField : queryFields) {
      deleteQueryFieldAnswers(queryField);
    }
    
    List<QueryQuestionComment> comments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, stamp);
    for (QueryQuestionComment comment : comments) {
      queryQuestionCommentDAO.archive(comment);
    }
  }

  /**
   * Deletes answers from query field
   *
   * @param queryField query field
   */
  private void deleteQueryFieldAnswers(QueryField queryField) {
    switch (queryField.getType()) {
      case TEXT:
        List<QueryQuestionTextAnswer> textAnswers = queryQuestionTextAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionTextAnswer textAnswer : textAnswers) {
          queryQuestionTextAnswerDAO.delete(textAnswer);
          queryQuestionAnswerDAO.delete(textAnswer);
        }
        break;
      case NUMERIC:
      case NUMERIC_SCALE:
        List<QueryQuestionNumericAnswer> numericAnswers = queryQuestionNumericAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionNumericAnswer numericAnswer : numericAnswers) {
          queryQuestionNumericAnswerDAO.delete(numericAnswer);
          queryQuestionAnswerDAO.delete(numericAnswer);
        }
        break;
      case OPTIONFIELD:
        List<QueryQuestionOptionGroupOptionAnswer> optionGroupAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionOptionGroupOptionAnswer optionGroupAnswer : optionGroupAnswers) {
          queryQuestionOptionGroupOptionAnswerDAO.delete(optionGroupAnswer);
        }
        List<QueryQuestionMultiOptionAnswer> multiAnswers = queryQuestionMultiOptionAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionMultiOptionAnswer multiAnswer : multiAnswers) {
          queryQuestionMultiOptionAnswerDAO.delete(multiAnswer);
          queryQuestionAnswerDAO.delete(multiAnswer);
        }
        List<QueryQuestionOptionAnswer> optionAnswers = queryQuestionOptionAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionOptionAnswer optionAnswer : optionAnswers) {
          queryQuestionOptionAnswerDAO.delete(optionAnswer);
          queryQuestionAnswerDAO.delete(optionAnswer);
        }
        break;
    }
  }
}
