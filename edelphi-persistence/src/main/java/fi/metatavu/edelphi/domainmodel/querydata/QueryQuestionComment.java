package fi.metatavu.edelphi.domainmodel.querydata;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.base.ArchivableEntity;
import fi.metatavu.edelphi.domainmodel.base.ModificationTrackedEntity;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.users.User;

@Entity
@Cacheable
public class QueryQuestionComment implements ArchivableEntity, ModificationTrackedEntity {

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryQuestionComment")  
  @TableGenerator(name="QueryQuestionComment", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private QueryReply queryReply;
  
  @ManyToOne
  private QueryPage queryPage;
  
  @ManyToOne
  private QueryQuestionCommentCategory category;
  
  @ManyToOne
  private QueryQuestionComment parentComment;
  
  @Column (length=1073741824)
  private String comment;
  
  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
  
  @NotNull
  @Column(nullable = false)
  private Boolean hidden;
  
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
  
  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public QueryReply getQueryReply() {
    return queryReply;
  }

  public void setQueryReply(QueryReply queryReply) {
    this.queryReply = queryReply;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public QueryPage getQueryPage() {
    return queryPage;
  }

  public void setQueryPage(QueryPage queryPage) {
    this.queryPage = queryPage;
  }
  
  public QueryQuestionCommentCategory getCategory() {
    return category;
  }
  
  public void setCategory(QueryQuestionCommentCategory category) {
    this.category = category;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public Boolean getArchived() {
    return archived;
  }

  @Override
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }
  
  public Boolean getHidden() {
    return hidden;
  }
  
  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }
  
  public QueryQuestionComment getParentComment() {
    return parentComment;
  }

  public void setParentComment(QueryQuestionComment parentComment) {
    this.parentComment = parentComment;
  }
}
