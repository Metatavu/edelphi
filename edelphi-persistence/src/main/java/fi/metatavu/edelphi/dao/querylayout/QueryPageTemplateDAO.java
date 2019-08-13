package fi.metatavu.edelphi.dao.querylayout;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplate;
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

}
