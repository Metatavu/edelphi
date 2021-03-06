package fi.metatavu.edelphi.domainmodel.panels;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.metatavu.edelphi.domainmodel.base.Bulletin;

@Entity
@PrimaryKeyJoinColumn(name="id")
public class PanelBulletin extends Bulletin {
  
  @ManyToOne
  private Panel panel;
  
  public Panel getPanel() {
    return panel;
  }
  
  public void setPanel(Panel panel) {
    this.panel = panel;
  }
}
