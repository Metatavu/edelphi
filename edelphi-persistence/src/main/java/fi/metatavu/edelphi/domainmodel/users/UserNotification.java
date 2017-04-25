package fi.metatavu.edelphi.domainmodel.users;

import java.util.Date;

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

@Entity
public class UserNotification {
  
  @Id
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserNotification")  
  @TableGenerator(name="UserNotification", initialValue=1, allocationSize=1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Notification notification;
  
  @Temporal (TemporalType.TIMESTAMP)
  @Column (nullable = false)
  @NotNull
  private Date notificationSent;
  
  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
  
  public Notification getNotification() {
    return notification;
  }
  
  public void setNotification(Notification notification) {
    this.notification = notification;
  }
  
}
