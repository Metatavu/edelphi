package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply_;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class QueryReplyDAO extends GenericDAO<QueryReply> {

  public QueryReply create(User user, Query query, PanelStamp panelStamp, User creator) {
    Date now = new Date();
    return create(user, query, panelStamp, Boolean.FALSE, creator, now, creator, now);
  }

  @SuppressWarnings ("squid:S00107")
  public QueryReply create(User user, Query query, PanelStamp panelStamp, Boolean complete, User creator, Date created, User modifier, Date modified) {
    QueryReply queryReply = new QueryReply();
    queryReply.setStamp(panelStamp);
    queryReply.setArchived(Boolean.FALSE);
    queryReply.setCreated(created);
    queryReply.setCreator(creator);
    queryReply.setLastModified(modified);
    queryReply.setLastModifier(modifier);
    queryReply.setQuery(query);
    queryReply.setUser(user);
    queryReply.setComplete(complete);

    getEntityManager().persist(queryReply);

    return queryReply;
  }

  public QueryReply findByUserAndQueryAndStamp(User user, Query query, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.user), user),
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp),
        criteriaBuilder.equal(root.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<QueryReply> listByQueryAndStamp(Query query, PanelStamp panelStamp) {
    return listByQueryAndStampAndArchived(query, panelStamp, Boolean.FALSE);
  }

  public List<QueryReply> listByQueryAndStampAndArchived(Query query, PanelStamp panelStamp, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), archived)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listByQuery(Query query, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.equal(root.get(QueryReply_.query), query)
    );

    return entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
  }
  
  public List<Long> listIdsByQueryAndStamp(Query query, PanelStamp panelStamp) {
    return listIdsByQueryAndStampAndArchived(query, panelStamp, Boolean.FALSE);
  }
  
  public List<Long> listIdsByQueryAndStampAndArchived(Query query, PanelStamp panelStamp, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root.get(QueryReply_.id));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), archived)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listByQueryAndArchived(Query query, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.archived), archived)
      )
    );
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.user), user),
        criteriaBuilder.equal(root.get(QueryReply_.archived), Boolean.FALSE)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listAllByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.user), user)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all query replies by query (including archived)
   *
   * @param query query
   * @return list of query replies
   */
  public List<QueryReply> listAllByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryReply_.query), query));

    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all query replies by stamp (including archived)
   *
   * @param stamp stamp
   * @return list of query replies
   */
  public List<QueryReply> listAllByStamp(PanelStamp stamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryReply_.stamp), stamp));

    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByQueryAndStamp(Query query, PanelStamp panelStamp) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryReply_.query), query), 
        criteriaBuilder.equal(root.get(QueryReply_.stamp), panelStamp)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public QueryReply updateUser(QueryReply queryReply, User user, User modifier) {
    queryReply.setUser(user);
    queryReply.setLastModifier(modifier);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }

  public QueryReply updateLastModified(QueryReply queryReply, User modifier) {
    queryReply.setLastModifier(modifier);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }
  
  public QueryReply updateComplete(QueryReply queryReply, User user, Boolean complete) {
    queryReply.setComplete(complete);
    queryReply.setLastModifier(user);
    queryReply.setLastModified(new Date());
    getEntityManager().persist(queryReply);
    return queryReply;
  }

  public List<QueryReply> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryReply_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryReply> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryReply> criteria = criteriaBuilder.createQuery(QueryReply.class);
    Root<QueryReply> root = criteria.from(QueryReply.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryReply_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
