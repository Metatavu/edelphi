package fi.metatavu.edelphi.domainmodel.querymeta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.base.ArchivableEntity;

@Entity
public class QueryOptionFieldOptionGroup implements ArchivableEntity {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }
  
  public void setOptionField(QueryOptionField optionField) {
    this.optionField = optionField;
  }

  public QueryOptionField getOptionField() {
    return optionField;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryOptionFieldOptionGroup")  
  @TableGenerator(name="QueryOptionFieldOptionGroup", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private QueryOptionField optionField;
  
  private String name;

  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
}
