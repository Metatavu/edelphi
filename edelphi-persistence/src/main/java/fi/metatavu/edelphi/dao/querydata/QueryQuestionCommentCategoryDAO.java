package fi.metatavu.edelphi.dao.querydata;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
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
   * @param queryPage queryPage
   * @param name name
   * @param creator creator's id
   * @param lastModifier last modifier's id
   * @return created queryQuestionCommentCategory
   */
  public QueryQuestionCommentCategory create(QueryPage queryPage, String name, User creator, User lastModifier, Date created, Date lastModified) {
    QueryQuestionCommentCategory queryQuestionCommentCategory = new QueryQuestionCommentCategory();
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

}
