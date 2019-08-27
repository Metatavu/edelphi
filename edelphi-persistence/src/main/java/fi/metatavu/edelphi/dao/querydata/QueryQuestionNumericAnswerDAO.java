package fi.metatavu.edelphi.dao.querydata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage_;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection_;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField_;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Folder_;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.Query_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class QueryQuestionNumericAnswerDAO extends GenericDAO<QueryQuestionNumericAnswer> {

  public QueryQuestionNumericAnswer create(QueryReply queryReply, QueryField queryField, Double data) {
    Date now = new Date();
    return create(queryReply, queryField, data, now, now);
  }
  
  public QueryQuestionNumericAnswer create(QueryReply queryReply, QueryField queryField, Double data, Date created, Date lastModified) {
    QueryQuestionNumericAnswer queryQuestionNumericAnswer = new QueryQuestionNumericAnswer();
    queryQuestionNumericAnswer.setData(data);
    queryQuestionNumericAnswer.setQueryField(queryField);
    queryQuestionNumericAnswer.setQueryReply(queryReply);
    queryQuestionNumericAnswer.setCreated(created);
    queryQuestionNumericAnswer.setLastModified(lastModified);
    return persist(queryQuestionNumericAnswer);
  }
  
  public QueryQuestionNumericAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(  
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryQuestionNumericAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists answers by field and replies
   * 
   * @param queryField field
   * @param queryReplies replies
   * @return answers into field in with replies
   */
  public List<QueryQuestionNumericAnswer> listByQueryFieldAndRepliesIn(QueryField queryField, List<QueryReply> queryReplies) {
    if (queryReplies == null || queryReplies.isEmpty()) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    criteria.select(root);
    criteria.where(
      root.get(QueryQuestionNumericAnswer_.queryReply).in(queryReplies),
      criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  /**
   * Lists numeric answers by given parameters. All parameters are optional
   * 
   * @param queryPage filter by comment's query page
   * @param stamp filter by panel stamp
   * @param query filter by query
   * @param queryParentFolder filter by query parent folder
   * @param user filter by user
   * @param archived filter by archived
   * 
   * @return a list of comments
   */
  public List<QueryQuestionNumericAnswer> list(QueryPage queryPage, PanelStamp stamp, Query query, Folder queryParentFolder, User user, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryReply> queryReplyJoin = root.join(QueryQuestionNumericAnswer_.queryReply);
    Join<QueryQuestionNumericAnswer, QueryField> queryFieldJoin = root.join(QueryQuestionNumericAnswer_.queryField);
    Join<QueryField, QueryPage> queryPageJoin = queryFieldJoin.join(QueryField_.queryPage);
    Join<QueryPage, QuerySection> querySectionJoin = queryPageJoin.join(QueryPage_.querySection);
    Join<QuerySection, Query> queryJoin = querySectionJoin.join(QuerySection_.query);
    Join<Query, Folder> queryFolderJoin = queryJoin.join(Query_.parentFolder);
    
    List<Predicate> criterias = new ArrayList<>();
    
    if (queryPage != null) {
      criterias.add(criteriaBuilder.equal(queryFieldJoin.get(QueryField_.queryPage), queryPage));
    }

    if (stamp != null || user != null) {
      if (stamp != null) {
        criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.stamp), stamp));
      }
      
      if (user != null) {
        criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.user), user));
      }
    }
    
    if (query != null) {
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.query), query));
    }
    
    if (queryParentFolder != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.parentFolder), queryParentFolder));
    }
    
    if (archived != null) {
      criterias.add(criteriaBuilder.equal(queryJoin.get(Query_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryReplyJoin.get(QueryReply_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryFieldJoin.get(QueryField_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryPageJoin.get(QueryPage_.archived), archived));
      criterias.add(criteriaBuilder.equal(querySectionJoin.get(QuerySection_.archived), archived));
      criterias.add(criteriaBuilder.equal(queryFolderJoin.get(Folder_.archived), archived));
    }
    
    criteria.select(root);
    criteria.where(criteriaBuilder.and(criterias.toArray(new Predicate[0])));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<QueryQuestionNumericAnswer> listByQueryReplyAndQueryPageOrderByData(QueryReply queryReply, QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionNumericAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionNumericAnswer.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryField> qfJoin = root.join(QueryQuestionNumericAnswer_.queryField);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(qfJoin.get(QueryField_.queryPage), queryPage)
      )
    );
    
    criteria.orderBy(criteriaBuilder.asc(root.get(QueryQuestionNumericAnswer_.data)));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  /**
   * Counts query question numeric answers
   * 
   * @param queryField query field
   * @param queryReplies query reply set must be in this set
   * @param data data must equal this value
   * @return count of query question numeric answers
   */
  public Long countByQueryFieldQueryRepliesInAndData(QueryField queryField, Collection<QueryReply> queryReplies, Double data) {
    if (queryReplies == null || queryReplies.isEmpty()) {
      return 0l;
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryReply> replyJoin = root.join(QueryQuestionNumericAnswer_.queryReply);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField),
        root.get(QueryQuestionNumericAnswer_.queryReply).in(queryReplies),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.data), data),
        criteriaBuilder.equal(replyJoin.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Long countByQueryFieldQueryReplyAndData(QueryField queryField, QueryReply queryReply, Double data) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionNumericAnswer> root = criteria.from(QueryQuestionNumericAnswer.class);
    Join<QueryQuestionNumericAnswer, QueryReply> replyJoin = root.join(QueryQuestionNumericAnswer_.queryReply);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.queryReply), queryReply),
        criteriaBuilder.equal(root.get(QueryQuestionNumericAnswer_.data), data),
        criteriaBuilder.equal(replyJoin.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  // TOOD: This is not a DAO method
  public List<Double> listAnswers(QueryField queryField) {
    List<QueryQuestionNumericAnswer> listByQueryField = listByQueryField(queryField);
    List<Double> results = new ArrayList<>();
    
    for (QueryQuestionNumericAnswer answer : listByQueryField) {
      results.add(answer.getData());
    }
    
    return results;
  }
  
  public QueryQuestionNumericAnswer updateData(QueryQuestionNumericAnswer queryQuestionNumericAnswer, Double data) {
    queryQuestionNumericAnswer.setData(data);
    queryQuestionNumericAnswer.setLastModified(new Date());
    return persist(queryQuestionNumericAnswer);
  }
}
