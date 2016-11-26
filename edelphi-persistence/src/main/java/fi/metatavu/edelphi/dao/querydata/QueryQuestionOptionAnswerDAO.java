package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

public class QueryQuestionOptionAnswerDAO extends GenericDAO<QueryQuestionOptionAnswer> {

  public QueryQuestionOptionAnswer create(QueryReply queryReply, QueryField queryField, QueryOptionFieldOption option) {
    Date now = new Date();
    return create(queryReply, queryField, option, now, now);
  }
  
  public QueryQuestionOptionAnswer create(QueryReply queryReply, QueryField queryField, QueryOptionFieldOption option, Date created, Date lastModified) {
    QueryQuestionOptionAnswer queryQuestionOptionAnswer = new QueryQuestionOptionAnswer();
    queryQuestionOptionAnswer.setOption(option);
    queryQuestionOptionAnswer.setQueryField(queryField);
    queryQuestionOptionAnswer.setQueryReply(queryReply);
    queryQuestionOptionAnswer.setCreated(created);
    queryQuestionOptionAnswer.setLastModified(lastModified);
    return persist(queryQuestionOptionAnswer);
  }
  
  public QueryQuestionOptionAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Long countByQueryOptionFieldOption(QueryOptionFieldOption queryOptionFieldOption) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.option), queryOptionFieldOption)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public List<QueryQuestionOptionAnswer> listByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionOptionAnswer> listByQueryRepliesAndQueryField(List<QueryReply> queryReplies, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        root.get(QueryQuestionOptionAnswer_.queryReply).in(queryReplies)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<QueryQuestionOptionAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField)
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionOptionAnswer updateOption(QueryQuestionOptionAnswer queryQuestionOptionAnswer, QueryOptionFieldOption option) {
    queryQuestionOptionAnswer.setOption(option);
    queryQuestionOptionAnswer.setLastModified(new Date());
    return persist(queryQuestionOptionAnswer);
  }

}
