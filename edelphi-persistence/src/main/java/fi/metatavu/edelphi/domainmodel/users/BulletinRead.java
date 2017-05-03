package fi.metatavu.edelphi.domainmodel.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.base.Bulletin;

@Entity
public class BulletinRead {
  
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private User user;

  @ManyToOne
  private Bulletin bulletin;
  
  @Temporal (TemporalType.TIMESTAMP)
  @Column (nullable = false)
  @NotNull
  private Date readTime;
  
  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
  
  public Bulletin getBulletin() {
    return bulletin;
  }
  
  public void setBulletin(Bulletin bulletin) {
    this.bulletin = bulletin;
  }
  
  public Date getReadTime() {
    return readTime;
  }
  
  public void setReadTime(Date readTime) {
    this.readTime = readTime;
  }
  
}
