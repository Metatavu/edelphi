package fi.metatavu.edelphi.dao.querymeta;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;

@ApplicationScoped
public class QueryOptionFieldDAO extends GenericDAO<QueryOptionField> {

  public QueryOptionField create(QueryPage queryPage, String name, Boolean mandatory, String caption) {
    QueryOptionField queryOptionField = new QueryOptionField();
    queryOptionField.setCaption(caption);
    queryOptionField.setName(name);
    queryOptionField.setMandatory(mandatory);
    queryOptionField.setQueryPage(queryPage);
    queryOptionField.setArchived(Boolean.FALSE);
    getEntityManager().persist(queryOptionField);
    return queryOptionField;
  }

  public QueryOptionField update(QueryOptionField queryOptionField, QueryPage queryPage, String name, Boolean mandatory, String caption) {
    queryOptionField.setCaption(caption);
    queryOptionField.setName(name);
    queryOptionField.setMandatory(mandatory);
    queryOptionField.setQueryPage(queryPage);
    getEntityManager().persist(queryOptionField);
    return queryOptionField;
  }

}
