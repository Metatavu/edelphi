package fi.metatavu.edelphi.invitations.batch;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemReader;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.panels.PanelController;

/**
 * Batch item reader for reading panel invitations
 * 
 * @author Antti Leppä
 */
@Named
public class PanelInvitationReader extends TypedItemReader<PanelInvitation> {
  
  @Inject
  private Logger logger;

  @Inject
  private PanelController panelController; 

  @Inject
  @JobProperty
  private Long[] panelInvitationIds;
  
  private int index;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    index = 0;
    logger.info("Reading {} panel invitations", panelInvitationIds != null ? panelInvitationIds.length : 0);
  }

  @Override
  public PanelInvitation read() throws Exception {
    if (this.panelInvitationIds == null) {
      return null;
    }
    
    try {
      if (this.index < this.panelInvitationIds.length) {
        Long panelInvitationId = this.panelInvitationIds[this.index];
        PanelInvitation panelInvitation = panelController.findPanelInvitationById(panelInvitationId);
        if (panelInvitation == null) {
          throw new RuntimeException(String.format("Could not find panel invitation by id %d", panelInvitationId));
        } else {
          return panelInvitation;
        }
      }
      
      return null;
    } finally {
      this.index++;
    }
  }
  
}
