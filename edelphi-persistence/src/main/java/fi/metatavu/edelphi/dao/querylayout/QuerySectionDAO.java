package fi.metatavu.edelphi.dao.querylayout;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection_;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class QuerySectionDAO extends GenericDAO<QuerySection> {

  public QuerySection create(User creator, Query query, String title, Integer sectionNumber, Boolean visible, Boolean commentable, Boolean viewDiscussions) {
    Date now = new Date();

    QuerySection querySection = new QuerySection();
    querySection.setQuery(query);
    querySection.setSectionNumber(sectionNumber);
    querySection.setVisible(visible);
    querySection.setCommentable(commentable);
    querySection.setViewDiscussions(viewDiscussions);
    querySection.setTitle(title);
    querySection.setCreated(now);
    querySection.setLastModified(now);
    querySection.setCreator(creator);
    querySection.setLastModifier(creator);

    getEntityManager().persist(querySection);
    
    return querySection;
  }
  
  public List<QuerySection> listByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QuerySection> criteria = criteriaBuilder.createQuery(QuerySection.class);
    Root<QuerySection> root = criteria.from(QuerySection.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QuerySection_.archived), Boolean.FALSE),
            criteriaBuilder.equal(root.get(QuerySection_.query), query)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  /**
   * Lists all query sections by query (including archived)
   *
   * @param query query
   * @return list of query sections
   */
  public List<QuerySection> listAllByQuery(Query query) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QuerySection> criteria = criteriaBuilder.createQuery(QuerySection.class);
    Root<QuerySection> root = criteria.from(QuerySection.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QuerySection_.query), query));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public QuerySection updateTitle(QuerySection querySection, String title, User modifier) {
    Date now = new Date();
      
    querySection.setTitle(title);
    querySection.setLastModified(now);
    querySection.setLastModifier(modifier);
    
    getEntityManager().persist(querySection);
    
    return querySection;
  }
  
  public QuerySection updateVisible(QuerySection querySection, Boolean visible, User modifier) {
    Date now = new Date();
      
    querySection.setVisible(visible);
    querySection.setLastModified(now);
    querySection.setLastModifier(modifier);
    
    getEntityManager().persist(querySection);
    
    return querySection;
  }
  
  public QuerySection updateCommentable(QuerySection querySection, Boolean commentable, User modifier) {
    Date now = new Date();
      
    querySection.setCommentable(commentable);
    querySection.setLastModified(now);
    querySection.setLastModifier(modifier);
    
    getEntityManager().persist(querySection);
    
    return querySection;
  }
  
  public QuerySection updateViewDiscussions(QuerySection querySection, Boolean viewDiscussions, User modifier) {
    Date now = new Date();
      
    querySection.setViewDiscussions(viewDiscussions);
    querySection.setLastModified(now);
    querySection.setLastModifier(modifier);
    
    getEntityManager().persist(querySection);
    
    return querySection;
  }
  
  public QuerySection updateSectionNumber(QuerySection querySection, Integer sectionNumber, User modifier) {
    Date now = new Date();
      
    querySection.setSectionNumber(sectionNumber);
    querySection.setLastModified(now);
    querySection.setLastModifier(modifier);
    
    getEntityManager().persist(querySection);
    
    return querySection;
  }

  public List<QuerySection> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QuerySection> criteria = criteriaBuilder.createQuery(QuerySection.class);
    Root<QuerySection> root = criteria.from(QuerySection.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QuerySection_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QuerySection> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QuerySection> criteria = criteriaBuilder.createQuery(QuerySection.class);
    Root<QuerySection> root = criteria.from(QuerySection.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QuerySection_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
