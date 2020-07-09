package fi.metatavu.edelphi.dao.querydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;

@ApplicationScoped
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

  /**
   * Finds answer text by query reply and query field
   * 
   * @param queryReply reply
   * @param queryField field
   * @return answer text
   */
  public String findTextByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    Join<QueryQuestionOptionAnswer, QueryOptionFieldOption> option = root.join(QueryQuestionOptionAnswer_.option);
    
    criteria.select(option.get(QueryOptionFieldOption_.text));
    
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  /**
   * Finds answer value by query reply and query field
   * 
   * @param queryReply reply
   * @param queryField field
   * @return answer value
   */
  public String findValueByQueryReplyAndQueryField(QueryReply queryReply, QueryField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    Join<QueryQuestionOptionAnswer, QueryOptionFieldOption> option = root.join(QueryQuestionOptionAnswer_.option);
    
    criteria.select(option.get(QueryOptionFieldOption_.value));
    
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryField), queryField),
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.queryReply), queryReply)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  /**
   * Finds answer by query reply and query field
   * 
   * @param queryReply reply
   * @param queryField field
   * @return answer
   */
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

  /**
   * Counts how many times option has been selected in given set of replies
   * 
   * @param option option
   * @param queryReplies replies
   * @return count of how many times option has been selected in given set of replies
   */
  public Long countByOptionAndReplyIn(QueryOptionFieldOption option, List<QueryReply> queryReplies) {
    if (queryReplies == null || queryReplies.isEmpty()) {
      return 0l;
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryQuestionOptionAnswer_.option), option),
        root.get(QueryQuestionOptionAnswer_.queryReply).in(queryReplies)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
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

  /**
   * Returns list of tuples containing x (string), y (string) and count (long) describing 2d answer counts for given x ad y fields within given reply set
   * 
   * @param queryReplies query replies
   * @param queryFieldX x field
   * @param queryFieldY y field
   * @return list of tuples
   */
  public List<Tuple> countReplies2d(List<QueryReply> queryReplies, QueryOptionField queryFieldX, QueryOptionField queryFieldY) {

    if (queryReplies == null || queryReplies.isEmpty()) {
      return new ArrayList<>();
    }

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> criteria = criteriaBuilder.createTupleQuery();
    
    Root<QueryQuestionOptionAnswer> xAnswerRoot = criteria.from(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> yAnswerRoot = criteria.from(QueryQuestionOptionAnswer.class);

    Join<QueryQuestionOptionAnswer, QueryOptionFieldOption> xOption = xAnswerRoot.join(QueryQuestionOptionAnswer_.option);
    Join<QueryQuestionOptionAnswer, QueryOptionFieldOption> yOption = yAnswerRoot.join(QueryQuestionOptionAnswer_.option);

    criteria.multiselect(xOption.get(QueryOptionFieldOption_.value).alias("x"), yOption.get(QueryOptionFieldOption_.value).alias("y"), criteriaBuilder.count(xAnswerRoot).alias("count"));
    criteria.where(
      xAnswerRoot.get(QueryQuestionOptionAnswer_.queryReply).in(queryReplies),
      criteriaBuilder.equal(xAnswerRoot.get(QueryQuestionOptionAnswer_.queryField), queryFieldX),
      criteriaBuilder.equal(yAnswerRoot.get(QueryQuestionOptionAnswer_.queryField), queryFieldY),
      criteriaBuilder.equal(xAnswerRoot.get(QueryQuestionOptionAnswer_.queryReply), yAnswerRoot.get(QueryQuestionOptionAnswer_.queryReply))
    );
    
    criteria.groupBy(xOption.get(QueryOptionFieldOption_.value), yOption.get(QueryOptionFieldOption_.value));

    return entityManager.createQuery(criteria).getResultList();
  }

}
