package fi.metatavu.edelphi.dao.querymeta;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;

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
