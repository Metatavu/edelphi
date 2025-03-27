package fi.metatavu.edelphi.dao.querylayout;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplate_;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class QueryPageTemplateDAO extends GenericDAO<QueryPageTemplate> {

  public QueryPageTemplate create(QueryPageType pageType, User creator, LocalizedEntry name, String iconName) {
    Date now = new Date();

    QueryPageTemplate queryPageTemplate = new QueryPageTemplate();
    queryPageTemplate.setName(name);
    queryPageTemplate.setIconName(iconName);
    queryPageTemplate.setPageType(pageType);
    queryPageTemplate.setArchived(Boolean.FALSE);
    queryPageTemplate.setCreated(now);
    queryPageTemplate.setLastModified(now);
    queryPageTemplate.setCreator(creator);
    queryPageTemplate.setLastModifier(creator);

    getEntityManager().persist(queryPageTemplate);
    return queryPageTemplate;
  }

  public List<QueryPageTemplate> listAllByCreator(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplate> criteria = criteriaBuilder.createQuery(QueryPageTemplate.class);
    Root<QueryPageTemplate> root = criteria.from(QueryPageTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryPageTemplate_.creator), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<QueryPageTemplate> listAllByModifier(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryPageTemplate> criteria = criteriaBuilder.createQuery(QueryPageTemplate.class);
    Root<QueryPageTemplate> root = criteria.from(QueryPageTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(QueryPageTemplate_.lastModifier), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}
