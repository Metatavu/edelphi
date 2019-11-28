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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer_;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage_;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection_;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField_;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption_;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Folder_;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.Query_;
import fi.metatavu.edelphi.domainmodel.users.User;

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
  
  /**
   * Lists option answers by given parameters. All parameters are optional
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
  public List<QueryQuestionOptionAnswer> list(QueryPage queryPage, PanelStamp stamp, Query query, Folder queryParentFolder, User user, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionOptionAnswer> criteria = criteriaBuilder.createQuery(QueryQuestionOptionAnswer.class);
    Root<QueryQuestionOptionAnswer> root = criteria.from(QueryQuestionOptionAnswer.class);
    Join<QueryQuestionOptionAnswer, QueryReply> queryReplyJoin = root.join(QueryQuestionOptionAnswer_.queryReply);
    Join<QueryQuestionOptionAnswer, QueryField> queryFieldJoin = root.join(QueryQuestionOptionAnswer_.queryField);
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

}
