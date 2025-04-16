package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;

@ApplicationScoped
public class QueryQuestionTextAnswerDAO extends GenericDAO<QueryQuestionTextAnswer> {
  public List<QueryQuestionTextAnswer> listByReply(QueryReply reply) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionTextAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionTextAnswer.class);
    Root<QueryQuestionTextAnswer> root = criteria.from(QueryQuestionTextAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryReply), reply)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionTextAnswer create(QueryReply queryReply, QueryField queryField, String data) {
    Date now = new Date();
    return create(queryReply, queryField, data, now, now);
  }
  
  public QueryQuestionTextAnswer create(QueryReply queryReply, QueryField queryField, String data, Date created, Date lastModified) {
    QueryQuestionTextAnswer queryQuestionTextAnswer = new QueryQuestionTextAnswer();
    queryQuestionTextAnswer.setData(data);
    queryQuestionTextAnswer.setQueryField(queryField);
    queryQuestionTextAnswer.setQueryReply(queryReply);
    queryQuestionTextAnswer.setCreated(created);
    queryQuestionTextAnswer.setLastModified(lastModified);
    return persist(queryQuestionTextAnswer);
  }
  
  public QueryQuestionTextAnswer findByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionTextAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionTextAnswer.class);
    Root<QueryQuestionTextAnswer> root = criteria.from(QueryQuestionTextAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryReply), queryReply)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryQuestionTextAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionTextAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionTextAnswer.class);
    Root<QueryQuestionTextAnswer> root = criteria.from(QueryQuestionTextAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionTextAnswer_.queryField), queryField)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public QueryQuestionTextAnswer updateData(QueryQuestionTextAnswer queryQuestionTextAnswer, String data) {
    queryQuestionTextAnswer.setData(data);
    queryQuestionTextAnswer.setLastModified(new Date());
    return persist(queryQuestionTextAnswer);
  }
  
}
