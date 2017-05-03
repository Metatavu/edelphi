package fi.metatavu.edelphi.domainmodel.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import fi.metatavu.edelphi.domainmodel.users.User;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Bulletin implements ArchivableEntity, ModificationTrackedEntity {
  
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @NotEmpty
  @Column (nullable = false)
  private String title;

  @NotBlank
  @NotEmpty
  @Column (nullable = false, length = 1073741824)
  private String message;

  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;

  @ManyToOne 
  private User creator;
  
  @NotNull
  @Column (updatable=false, nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date created;
  
  @ManyToOne  
  private User lastModifier;
  
  @NotNull
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date lastModified;
  
  @NotNull
  @Column (nullable=false)
  private Boolean important;

  @Temporal (value=TemporalType.TIMESTAMP)
  private Date importantEnds;
  
  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getCreator() {
    return creator;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Boolean getArchived() {
    return archived;
  }
  
  public Boolean getImportant() {
    return important;
  }
  
  public void setImportant(Boolean important) {
    this.important = important;
  }
  
  public Date getImportantEnds() {
    return importantEnds;
  }
  
  public void setImportantEnds(Date importantEnds) {
    this.importantEnds = importantEnds;
  }
  
  @Transient
  public String getSummary() {
    if (StringUtils.isNotBlank(getMessage())) {
      String plainMessage = StringEscapeUtils.unescapeHtml4(getMessage().replaceAll("\\<.*?>",""));
      if (StringUtils.isNotBlank(plainMessage)) {
        if (plainMessage.length() >= 255) {
          return plainMessage.substring(0, 252) + "...";
        } else {
          return plainMessage;
        }
      }
    }
    
    return null;
  }
}
