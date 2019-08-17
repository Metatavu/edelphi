package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;

@ApplicationScoped
public class QueryQuestionOptionGroupOptionAnswerDAO extends GenericDAO<QueryQuestionOptionGroupOptionAnswer> {

  public QueryQuestionOptionGroupOptionAnswer create(QueryReply queryReply, QueryField queryField,
      QueryOptionFieldOption option, QueryOptionFieldOptionGroup group) {
    Date now = new Date();
    return create(queryReply, queryField, option, group, now, now);
  }
  
  public QueryQuestionOptionGroupOptionAnswer create(QueryReply queryReply, QueryField queryField,
      QueryOptionFieldOption option, QueryOptionFieldOptionGroup group, Date created, Date lastModified) {
    QueryQuestionOptionGroupOptionAnswer queryQuestionOptionGroupOptionAnswer = new QueryQuestionOptionGroupOptionAnswer();
    queryQuestionOptionGroupOptionAnswer.setOption(option);
    queryQuestionOptionGroupOptionAnswer.setQueryField(queryField);
    queryQuestionOptionGroupOptionAnswer.setQueryReply(queryReply);
    queryQuestionOptionGroupOptionAnswer.setGroup(group);
    queryQuestionOptionGroupOptionAnswer.setCreated(created);
    queryQuestionOptionGroupOptionAnswer.setLastModified(lastModified);
    return persist(queryQuestionOptionGroupOptionAnswer);
  }

  public Long countByQueryOptionFieldOptionGroup(QueryOptionFieldOptionGroup queryOptionFieldOptionGroup) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionOptionGroupOptionAnswer> root = criteria.from(QueryQuestionOptionGroupOptionAnswer.class);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.group), queryOptionFieldOptionGroup)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public List<QueryQuestionOptionGroupOptionAnswer> listByQueryField(QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionGroupOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionGroupOptionAnswer.class);
    Root<QueryQuestionOptionGroupOptionAnswer> root = criteria.from(QueryQuestionOptionGroupOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryField), queryField)
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionOptionGroupOptionAnswer> listByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionGroupOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionGroupOptionAnswer.class);
    Root<QueryQuestionOptionGroupOptionAnswer> root = criteria.from(QueryQuestionOptionGroupOptionAnswer.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryField), queryField),
            criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryReply), queryReply)
            )
    );
    

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionOptionGroupOptionAnswer> listByQueryRepliesAndQueryField(List<QueryReply> queryReplies, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionGroupOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionGroupOptionAnswer.class);
    Root<QueryQuestionOptionGroupOptionAnswer> root = criteria.from(QueryQuestionOptionGroupOptionAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryField), queryField),
        root.get(QueryQuestionOptionGroupOptionAnswer_.queryReply).in(queryReplies)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionOptionGroupOptionAnswer> listByQueryReplyAndQueryFieldAndOptionFieldGroup(QueryReply queryReply, QueryOptionField queryField, QueryOptionFieldOptionGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionGroupOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionGroupOptionAnswer.class);
    Root<QueryQuestionOptionGroupOptionAnswer> root = criteria.from(QueryQuestionOptionGroupOptionAnswer.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryField), queryField),
            criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.queryReply), queryReply),
            criteriaBuilder.equal(root.get(QueryQuestionOptionGroupOptionAnswer_.group), group)
            )
    );
    

    return entityManager.createQuery(criteria).getResultList();
  }

  public QueryQuestionOptionGroupOptionAnswer updateOption(QueryQuestionOptionGroupOptionAnswer answer, QueryOptionFieldOption option) {
    answer.setOption(option);
    answer.setLastModified(new Date());
    return persist(answer);
  }
  
}
