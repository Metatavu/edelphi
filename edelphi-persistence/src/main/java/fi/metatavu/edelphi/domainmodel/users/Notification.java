package fi.metatavu.edelphi.domainmodel.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;

@Entity
public class Notification {

  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Notification")
  @TableGenerator(name="Notification", initialValue=1, allocationSize=1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @NotNull
  @Column (nullable = false)
  private Long millisBefore;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String name;
  
  @NotNull
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private NotificationType type;
  
  @ManyToOne
  private LocalizedEntry subjectTemplate;

  @ManyToOne
  private LocalizedEntry contentTemplate;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getMillisBefore() {
    return millisBefore;
  }
  
  public void setMillisBefore(Long millisBefore) {
    this.millisBefore = millisBefore;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public LocalizedEntry getContentTemplate() {
    return contentTemplate;
  }
  
  public void setContentTemplate(LocalizedEntry contentTemplate) {
    this.contentTemplate = contentTemplate;
  }
  
  public LocalizedEntry getSubjectTemplate() {
    return subjectTemplate;
  }
  
  public void setSubjectTemplate(LocalizedEntry subjectTemplate) {
    this.subjectTemplate = subjectTemplate;
  }
  
  public NotificationType getType() {
    return type;
  }
  
  public void setType(NotificationType type) {
    this.type = type;
  }

}
