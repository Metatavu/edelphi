package fi.metatavu.edelphi.dao.querymeta;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField_;

@ApplicationScoped
public class QueryNumericFieldDAO extends GenericDAO<QueryNumericField> {

  public QueryNumericField create(QueryPage queryPage, String name, Boolean mandatory, String caption, Double min, Double max, Double precision) {
    QueryNumericField queryNumericField = new QueryNumericField();
    queryNumericField.setCaption(caption);
    queryNumericField.setMandatory(mandatory);
    queryNumericField.setMax(max);
    queryNumericField.setMin(min);
    queryNumericField.setName(name);
    queryNumericField.setPrecision(precision);
    queryNumericField.setQueryPage(queryPage);
    queryNumericField.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryNumericField);
    return queryNumericField;
  }
  
  /**
   * Finds a numeric field by query page and name
   * 
   * @param queryPage query page
   * @param name name
   * @return numeric field or null if not found
   */
  public QueryNumericField findByQueryPageAndName(QueryPage queryPage, String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<QueryNumericField> criteria = criteriaBuilder.createQuery(QueryNumericField.class);
    Root<QueryNumericField> root = criteria.from(QueryNumericField.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(QueryNumericField_.queryPage), queryPage),
        criteriaBuilder.equal(root.get(QueryNumericField_.name), name),
        criteriaBuilder.equal(root.get(QueryNumericField_.archived), Boolean.FALSE)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public QueryNumericField updateCaption(QueryNumericField queryNumericField, String caption) {
    queryNumericField.setCaption(caption);
    getEntityManager().persist(queryNumericField);
    return queryNumericField;
  }

  public QueryNumericField updateMin(QueryNumericField queryNumericField, Double min) {
    queryNumericField.setMin(min);
    getEntityManager().persist(queryNumericField);
    return queryNumericField;
  }

  public QueryNumericField updateMax(QueryNumericField queryNumericField, Double max) {
    queryNumericField.setMax(max);
    getEntityManager().persist(queryNumericField);
    return queryNumericField;
  }

  public QueryNumericField updatePrecision(QueryNumericField queryNumericField, Double precision) {
    queryNumericField.setPrecision(precision);
    getEntityManager().persist(queryNumericField);
    return queryNumericField;
  }
}
