package fi.metatavu.edelphi.domainmodel.actions;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.metatavu.edelphi.domainmodel.base.Delfoi;

/**
 * Entity implementation class for Entity: DelfoiUserRoleAction
 *
 */
@Entity
@PrimaryKeyJoinColumn(name="id")
public class DelfoiUserRoleAction extends UserRoleAction {

  public void setDelfoi(Delfoi delfoi) {
    this.delfoi = delfoi;
  }

  public Delfoi getDelfoi() {
    return delfoi;
  }

  @ManyToOne
  private Delfoi delfoi;
}
