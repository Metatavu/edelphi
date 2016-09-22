package fi.metatavu.edelphi.domainmodel.querylayout;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class QueryPageTemplateLocalizedSetting extends QueryPageTemplateSetting {

  public LocalizedEntry getValue() {
    return value;
  }
  
  public void setValue(LocalizedEntry value) {
    this.value = value;
  }

  @ManyToOne 
  private LocalizedEntry value;
}
