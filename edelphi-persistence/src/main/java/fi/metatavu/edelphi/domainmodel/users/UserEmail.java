package fi.metatavu.edelphi.domainmodel.users;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable
@Indexed
public class UserEmail {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  @Transient
  public String getObfuscatedAddress() {
    int i = address == null ? 0 : address.indexOf('@');
    if (address == null || i <= 2) {
      return address;
    }
    return (i < 9 ? address.substring(0, 3) : address.substring(0, 8)) + "..." + address.substring(i);
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  @Id
  @DocumentId
  @GeneratedValue(strategy=GenerationType.TABLE, generator="UserEmail")  
  @TableGenerator(name="UserEmail", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;

  @ManyToOne
  private User user;
  
  @Email
  @NotNull
  @Column (nullable = false, unique=true)
  @NotEmpty
  @Field(index = Index.YES, store = Store.NO)
  private String address;
}
