package fi.metatavu.edelphi.panels;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Controller for panels
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PanelController {
  
  @Inject
  private SettingsController settingsController;
  
  @Inject
  private PanelDAO panelDAO;

  @Inject
  private PanelStampDAO panelStampDAO;

  @Inject
  private PanelUserExpertiseClassDAO panelUserExpertiseClassDAO; 

  @Inject
  private PanelUserIntressClassDAO panelUserIntressClassDAO;
  
  @Inject
  private PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO;
  
  @Inject
  private PanelUserGroupDAO panelUserGroupDAO;

  @Inject
  private PanelInvitationDAO panelInvitationDAO;

  @Inject
  private PanelUserDAO panelUserDAO;
  
  /**
   * Finds a panel by id
   * 
   * @param id id
   * @return panel or null if not found
   */
  public Panel findPanelById(Long id) {
    return panelDAO.findById(id);
  }
  
  /**
   * Lists user's panels
   * 
   * @param user user
   * @return panels
   */
  public List<Panel> listUserPanels(User user) {
    return panelDAO.listByDelfoiAndUser(settingsController.getDelfoi(), user);
  }

  /**
   * Finds a panel stamp by id
   * 
   * @param stampId stamp id
   * @return panel stamp or null if not found
   */
  public PanelStamp findPanelStampById(Long stampId) {
    return panelStampDAO.findById(stampId);
  }
  
  /**
   * Returns whether panel is archived or not
   * 
   * @param panel panel
   * @return whether panel is archived or not
   */
  public boolean isPanelArchived(Panel panel) {
    if (panel.getArchived()) {
      return true;
    }
    
    return false;
  }

  /**
   * Returns whether panel stamp is archived or not
   * 
   * @param stamp panel stamp
   * @return whether panel stamp is archived or not
   */
  public boolean isPanelStampArchived(PanelStamp stamp) {
    return stamp.getArchived();
  }

  /**
   * Returns whether stamp is from given panel
   * 
   * @param panel panel
   * @param stamp stamp
   * @return whether stamp is from given panel
   */
  public boolean isPanelsStamp(Panel panel, PanelStamp stamp) {
    if (stamp == null || panel == null) {
      return false;
    }
    
    return stamp.getPanel().getId().equals(panel.getId());
  }

  /**
   * Returns list of panel expertise classes
   * 
   * @param panel panel
   * @return list of panel expertise classes
   */
  public List<PanelUserExpertiseClass> listPanelUserExpertiseClasses(Panel panel) {
    return panelUserExpertiseClassDAO.listByPanel(panel);
  }
  
  /**
   * Returns list of panel interest classes
   * 
   * @param panel panel
   * @return list of panel interest classes
   */
  public List<PanelUserIntressClass> listPanelUserInterestClasses(Panel panel) {
    return panelUserIntressClassDAO.listByPanel(panel);
  }

  /**
   * Returns panel expertise group
   * 
   * @param id id
   * @return Panel expertise group
   */
  public PanelUserExpertiseGroup findPanelUserExpertiseGroup(Long id) {
    return panelUserExpertiseGroupDAO.findById(id);
  }

  /**
   * Returns list of panel expertise groups
   * 
   * @param panel panel
   * @param stamp stamp
   * @return list of panel expertise groups
   */
  public List<PanelUserExpertiseGroup> listPanelUserExpertiseGroups(Panel panel, PanelStamp stamp) {
    return panelUserExpertiseGroupDAO.listByPanelAndStamp(panel, stamp);
  }

  /**
   * Returns panel user group
   * 
   * @param id id
   * @return Panel user group
   */
  public PanelUserGroup findPanelUserGroup(Long id) {
    return panelUserGroupDAO.findById(id);
  }

  /**
   * Returns list of panel user groups
   * 
   * @param panel panel
   * @param stamp stamp
   * @return list of panel user groups
   */
  public List<PanelUserGroup> listPanelUserGroups(Panel panel, PanelStamp stamp) {
    return panelUserGroupDAO.listByPanelAndStamp(panel, stamp);
  }
  
  /**
   * Finds a panel invitation by id
   * 
   * @param id id
   * @return panel invitation or null if not found
   */
  public PanelInvitation findPanelInvitationById(Long id) {
    return panelInvitationDAO.findById(id);
  }
  
  /**
   * Returns list of panel invitations
   * 
   * @param panel panel
   * @param state state
   * @param firstResult first result
   * @param maxResults max results
   * @return list of panel invitations
   */
  public List<PanelInvitation> listPanelInvitations(Panel panel, PanelInvitationState state, Integer firstResult, Integer maxResults) {
    return panelInvitationDAO.listByPanelAndState(panel, state, firstResult, maxResults);
  }

  /**
   * Counts panel invitations
   *
   * @param panel panel
   * @param state state
   * @return count of panel invitations
   */
  public Long countPanelInvitations(Panel panel, PanelInvitationState state) {
    return panelInvitationDAO.countByPanelAndState(panel, state);
  }

  /**
   * Creates an panel invitation
   * 
   * @param panel panel
   * @param targetQuery target query or null if not specified 
   * @param email email address
   * @param creator creator user
   * @return created panel invitation
   */
  public PanelInvitation createPanelInvitation(Panel panel, Query targetQuery, String email, User creator) {
    // See if the user already has an existing invitation that points to the invitation target,
    // i.e. the front page of the panel or the front page of a single query within the panel 
    PanelInvitation invitation = panelInvitationDAO.findByPanelAndQueryAndEmail(panel, targetQuery, email);
    
    // Reuse the hash of an existing invitation, if we have one 
    String invitationHash = invitation == null ? UUID.randomUUID().toString() : invitation.getHash();
    
    if (invitation == null) {
      return panelInvitationDAO.create(panel, targetQuery, email, invitationHash, panel.getDefaultPanelUserRole(), PanelInvitationState.IN_QUEUE, creator);
    } else {
      return panelInvitationDAO.updateState(invitation, PanelInvitationState.IN_QUEUE, creator);
    }
  }
  
  /**
   * Creates new panel user
   * 
   * @param panel panel
   * @param user user
   * @param role role
   * @param joinType join type
   * @param creator creator
   * @return created panel user
   */
  public PanelUser createPanelUser(Panel panel, User user, PanelUserRole role, PanelUserJoinType joinType, User creator) {
    return panelUserDAO.create(panel, user, role, joinType, panel.getCurrentStamp(), creator);
  }

  /**
   * Finds a user by panel, user and stamp
   * 
   * @param panel panel
   * @param user user
   * @param stamp stamp
   * @return panel user or null if not found
   */
  public PanelUser findByPanelAndUserAndStamp(Panel panel, User user, PanelStamp stamp) {   
    return panelUserDAO.findByPanelAndUserAndStamp(panel, user, stamp);
  }
  
}
