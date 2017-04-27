package fi.metatavu.edelphi.domainmodel.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LocalizedEntry {
  
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  public Long getId() {
    return id;
  }
  
}
