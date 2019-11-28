package fi.metatavu.edelphi.domainmodel.actions;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import fi.metatavu.edelphi.domainmodel.users.UserRole;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Cacheable
public abstract class UserRoleAction {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private DelfoiAction delfoiAction;
  
  @ManyToOne
  private UserRole userRole;

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public void setDelfoiAction(DelfoiAction delfoiAction) {
    this.delfoiAction = delfoiAction;
  }

  public DelfoiAction getDelfoiAction() {
    return delfoiAction;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public UserRole getUserRole() {
    return userRole;
  }
}
