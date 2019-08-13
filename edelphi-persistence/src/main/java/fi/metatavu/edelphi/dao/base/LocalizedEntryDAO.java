package fi.metatavu.edelphi.dao.base;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;

@ApplicationScoped
public class LocalizedEntryDAO extends GenericDAO<LocalizedEntry> {

  public LocalizedEntry create() {
    LocalizedEntry localizedEntry = new LocalizedEntry();
    getEntityManager().persist(localizedEntry);
    return localizedEntry;
  }
  
}
