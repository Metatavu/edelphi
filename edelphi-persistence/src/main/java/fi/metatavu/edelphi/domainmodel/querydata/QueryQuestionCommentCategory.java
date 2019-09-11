package fi.metatavu.edelphi.domainmodel.querydata;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;

@Entity
@Cacheable
public class QueryQuestionCommentCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Query query;

  @ManyToOne
  private QueryPage queryPage;
  
  @NotEmpty
  @NotNull
  @Column(nullable = false)
  private String name;

  @ManyToOne
  private User creator;

  @NotNull
  @Column(updatable = false, nullable = false)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User lastModifier;

  @NotNull
  @Column(nullable = false)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  public Query getQuery() {
    return query;
  }
  
  public void setQuery(Query query) {
    this.query = query;
  }

  public QueryPage getQueryPage() {
    return queryPage;
  }

  public void setQueryPage(QueryPage queryPage) {
    this.queryPage = queryPage;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public User getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

}
