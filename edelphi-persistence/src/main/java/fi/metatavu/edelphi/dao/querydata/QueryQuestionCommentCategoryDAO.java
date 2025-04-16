package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;

/**
 * DAO class for QueryQuestionCommentCategory
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryQuestionCommentCategoryDAO extends GenericDAO<QueryQuestionCommentCategory> {

  /**
   * Creates new QueryQuestionCommentCategory
   * 
   * @param id id
   * @param query query
   * @param queryPage queryPage
   * @param name name
   * @param creator creator's id
   * @param lastModifier last modifier's id
   * @return created queryQuestionCommentCategory
   */
  public QueryQuestionCommentCategory create(Query query, QueryPage queryPage, String name, User creator, User lastModifier, Date created, Date lastModified) {
    QueryQuestionCommentCategory queryQuestionCommentCategory = new QueryQuestionCommentCategory();
    queryQuestionCommentCategory.setQuery(query);
    queryQuestionCommentCategory.setQueryPage(queryPage);
    queryQuestionCommentCategory.setName(name);
    queryQuestionCommentCategory.setCreator(creator);
    queryQuestionCommentCategory.setLastModifier(lastModifier);
    queryQuestionCommentCategory.setLastModified(lastModified);
    queryQuestionCommentCategory.setCreated(created);
    return persist(queryQuestionCommentCategory);
  }

  /**
   * Updates name
   *
   * @param name name
   * @param lastModifier last modifier's id
   * @return updated queryQuestionCommentCategory
   */
  public QueryQuestionCommentCategory updateName(QueryQuestionCommentCategory queryQuestionCommentCategory, String name, User lastModifier) {
    queryQuestionCommentCategory.setLastModifier(lastModifier);
    queryQuestionCommentCategory.setName(name);
    return persist(queryQuestionCommentCategory);
  }
  
  /**
   * Lists query question comment categories by query page
   * 
   * @param queryPage query page
   * @return query question comment categories by query page
   */
  public List<QueryQuestionCommentCategory> listByQueryPage(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.queryPage), queryPage)
    );
    
    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  /**
   * Lists query question comment categories by query page including query scoped categories
   * 
   * @param queryPage query page
   * @return query question comment categories by query page including query scoped categories
   */
  public List<QueryQuestionCommentCategory> listByQueryPageOrPageQuery(QueryPage queryPage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.or(
        criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.queryPage), queryPage),
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.query), queryPage.getQuerySection().getQuery()),
          criteriaBuilder.isNull(root.get(QueryQuestionCommentCategory_.queryPage))
        )
      )
    );
    
    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  /**
   * Lists query question comment categories by query
   * 
   * @param query query
   * @return query question comment categories by query
   */
  public List<QueryQuestionCommentCategory> listByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.query), query)
    );
    
    return entityManager.createQuery(criteria).getResultList(); 
  }
  
  /**
   * Lists query question comment categories by query and page null
   * 
   * @param query query
   * @return query question comment categories by query and page null
   */
  public List<QueryQuestionCommentCategory> listByQueryAndPageNull(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.query), query),
      criteriaBuilder.isNull(root.get(QueryQuestionCommentCategory_.queryPage))
    );
    
    return entityManager.createQuery(criteria).getResultList(); 
  }

  public List<QueryQuestionCommentCategory> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryQuestionCommentCategory> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryQuestionCommentCategory> criteria = criteriaBuilder.createQuery(QueryQuestionCommentCategory.class);
    Root<QueryQuestionCommentCategory> root = criteria.from(QueryQuestionCommentCategory.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryQuestionCommentCategory_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
