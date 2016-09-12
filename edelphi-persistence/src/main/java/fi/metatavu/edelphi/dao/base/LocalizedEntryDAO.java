package fi.metatavu.edelphi.dao.base;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;

public class LocalizedEntryDAO extends GenericDAO<LocalizedEntry> {

  public LocalizedEntry create() {
    LocalizedEntry localizedEntry = new LocalizedEntry();
    getEntityManager().persist(localizedEntry);
    return localizedEntry;
  }
  
}
