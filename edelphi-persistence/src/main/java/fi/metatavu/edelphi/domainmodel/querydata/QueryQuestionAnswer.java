package fi.metatavu.edelphi.domainmodel.querydata;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class QueryQuestionAnswer {

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="QueryQuestionAnswer")  
  @TableGenerator(name="QueryQuestionAnswer", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne
  private QueryReply queryReply;
  
  @ManyToOne
  private QueryField queryField;
  
  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;
  
  @Column (nullable = false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date lastModified;

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setQueryReply(QueryReply queryReply) {
    this.queryReply = queryReply;
  }

  public QueryReply getQueryReply() {
    return queryReply;
  }

  public void setQueryField(QueryField queryField) {
    this.queryField = queryField;
  }

  public QueryField getQueryField() {
    return queryField;
  }
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
}
