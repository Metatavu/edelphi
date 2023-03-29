package fi.metatavu.edelphi.dao.querymeta;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup_;

@ApplicationScoped
public class QueryOptionFieldOptionGroupDAO extends GenericDAO<QueryOptionFieldOptionGroup> {

  public QueryOptionFieldOptionGroup create(QueryOptionField optionField, String name) {
    QueryOptionFieldOptionGroup queryOptionFieldOptionGroup = new QueryOptionFieldOptionGroup();
    queryOptionFieldOptionGroup.setOptionField(optionField);
    queryOptionFieldOptionGroup.setName(name);
    queryOptionFieldOptionGroup.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryOptionFieldOptionGroup);
    return queryOptionFieldOptionGroup;
  }
  
  public QueryOptionFieldOptionGroup findByQueryFieldAndName(QueryOptionField optionField, String name) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOptionGroup> criteria = criteriaBuilder.createQuery(QueryOptionFieldOptionGroup.class);
    Root<QueryOptionFieldOptionGroup> root = criteria.from(QueryOptionFieldOptionGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.optionField), optionField),
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.name), name),
        criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.archived), Boolean.FALSE)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<QueryOptionFieldOptionGroup> listByQueryField(QueryOptionField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOptionGroup> criteria = criteriaBuilder.createQuery(QueryOptionFieldOptionGroup.class);
    Root<QueryOptionFieldOptionGroup> root = criteria.from(QueryOptionFieldOptionGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.optionField), queryField),
            criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.archived), Boolean.FALSE)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();

  }

  /**
   * Lists all option groups by query field (including archived)
   *
   * @param queryField query field
   * @return list of option groups
   */
  public List<QueryOptionFieldOptionGroup> listAllByQueryField(QueryOptionField queryField) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryOptionFieldOptionGroup> criteria = criteriaBuilder.createQuery(QueryOptionFieldOptionGroup.class);
    Root<QueryOptionFieldOptionGroup> root = criteria.from(QueryOptionFieldOptionGroup.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(QueryOptionFieldOptionGroup_.optionField), queryField));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public QueryOptionFieldOptionGroup updateName(QueryOptionFieldOptionGroup queryOptionFieldOptionGroup, String name) {
    queryOptionFieldOptionGroup.setName(name);
    getEntityManager().persist(queryOptionFieldOptionGroup);
    return queryOptionFieldOptionGroup;
  }
}