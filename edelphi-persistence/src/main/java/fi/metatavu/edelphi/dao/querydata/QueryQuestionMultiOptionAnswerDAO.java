package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply_;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;

@ApplicationScoped
public class QueryQuestionMultiOptionAnswerDAO extends GenericDAO<QueryQuestionMultiOptionAnswer> {
  public List<QueryQuestionMultiOptionAnswer> listByReply(QueryReply reply) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionMultiOptionAnswer_.queryReply), reply)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionMultiOptionAnswer create(QueryReply queryReply, QueryField queryField, Set<QueryOptionFieldOption> options) {
    Date now = new Date();
    return create(queryReply, queryField, options, now, now);
  }
  
  public QueryQuestionMultiOptionAnswer create(QueryReply queryReply, QueryField queryField, Set<QueryOptionFieldOption> options, Date created, Date lastModified) {
    QueryQuestionMultiOptionAnswer queryQuestionOptionAnswer = new QueryQuestionMultiOptionAnswer();
    queryQuestionOptionAnswer.setOptions(options);
    queryQuestionOptionAnswer.setQueryField(queryField);
    queryQuestionOptionAnswer.setQueryReply(queryReply);
    queryQuestionOptionAnswer.setCreated(created);
    queryQuestionOptionAnswer.setLastModified(lastModified);
    
    return persist(queryQuestionOptionAnswer);
  }
  
  public QueryQuestionMultiOptionAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryQuestionMultiOptionAnswer> listByQueryRepliesAndQueryField(List<QueryReply> queryReplies, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        root.get(QueryQuestionOptionAnswer_.queryReply).in(queryReplies)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionMultiOptionAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);
    Join<QueryQuestionMultiOptionAnswer, QueryReply> qrJoin = root.join(QueryQuestionMultiOptionAnswer_.queryReply);

    criteria.select(root);
    
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
          criteriaBuilder.equal(qrJoin.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionMultiOptionAnswer> listAllByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionMultiOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionMultiOptionAnswer.class);
    Root<QueryQuestionMultiOptionAnswer> root = criteria.from(QueryQuestionMultiOptionAnswer.class);

    criteria.select(root);

    criteria.where(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField)
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public Map<Long, Long> listOptionAnswerCounts(QueryField queryMultiselectField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> criteria = criteriaBuilder.createQuery(Tuple.class);
    Root<QueryQuestionMultiOptionAnswer> answerRoot = criteria.from(QueryQuestionMultiOptionAnswer.class);
    
    Join<QueryQuestionMultiOptionAnswer, QueryOptionFieldOption> queryFieldOptionRoot = answerRoot.join(QueryQuestionMultiOptionAnswer_.options);
    criteria.multiselect(
        queryFieldOptionRoot.get(QueryOptionFieldOption_.id), 
        criteriaBuilder.count(answerRoot.get(QueryQuestionMultiOptionAnswer_.id)));
    criteria.where(criteriaBuilder.equal(answerRoot.get(QueryQuestionMultiOptionAnswer_.queryField), queryMultiselectField));
    criteria.groupBy(queryFieldOptionRoot.get(QueryOptionFieldOption_.id));

    TypedQuery<Tuple> q = entityManager.createQuery(criteria);
    List<Tuple> resultList = q.getResultList();

    Map<Long, Long> resultMap = new HashMap<>();

    for (Tuple optionAnswer : resultList) {
      resultMap.put(optionAnswer.get(0, Long.class), optionAnswer.get(1, Long.class));
    }
    
    return resultMap;
  }

  public QueryQuestionMultiOptionAnswer updateOptions(QueryQuestionMultiOptionAnswer queryQuestionOptionAnswer, Set<QueryOptionFieldOption> options) {
    queryQuestionOptionAnswer.setOptions(options);
    queryQuestionOptionAnswer.setLastModified(new Date());
    return persist(queryQuestionOptionAnswer);
  }
}
