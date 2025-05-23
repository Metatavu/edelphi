package fi.metatavu.edelphi.panels;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.comments.QueryQuestionCommentController;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.panels.*;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.domainmodel.panels.*;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.resources.ResourceController;
import fi.metatavu.edelphi.settings.SettingsController;
import fi.metatavu.edelphi.users.UserController;

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
  private ResourceController resourceController;

  @Inject
  private UserController userController;
  
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

  @Inject
  private PanelAuthDAO panelAuthDAO;

  @Inject
  private PanelBulletinDAO panelBulletinDAO;

  @Inject
  private PanelUserRoleActionDAO panelUserRoleActionDAO;

  @Inject
  private PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO;

  @Inject
  private QueryReplyDAO queryReplyDAO;

  @Inject
  private QueryQuestionCommentController queryQuestionCommentController;

  @Inject
  private QueryReplyController queryReplyController;


  /**
   * This function is used by a scheduled job to archive panels
   *
   * @param panel panel to archive
   */
  public void archivePanel(Panel panel) {
    panelInvitationDAO.listAllByPanel(panel).forEach(panelInvitationDAO::delete);
    panelDAO.archivePanel(panel);
  }
  
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
   * Deletes a panel and all of its contents. This operation is not reversible.
   *
   * @param panel panel
   */
  public void deletePanel(Panel panel) {
    Folder rootFolder = panel.getRootFolder();

    panelDAO.updateRootFolder(panel, null, panel.getLastModifier());
    panelAuthDAO.listByPanel(panel).forEach(panelAuthDAO::delete);
    panelUserRoleActionDAO.listByPanel(panel).forEach(panelUserRoleActionDAO::delete);
    panelBulletinDAO.listAllByPanel(panel).forEach(panelBulletinDAO::delete);
    panelInvitationDAO.listAllByPanel(panel).forEach(panelInvitationDAO::delete);
    resourceController.deleteResource(rootFolder);
    panelUserExpertiseGroupDAO.listAllByPanel(panel).forEach(this::deletePanelUserExpertiseGroup);
    panelUserDAO.listAllByPanel(panel).forEach(panelUserDAO::delete);
    panelUserGroupDAO.listAllByPanel(panel).forEach(panelUserGroupDAO::delete);
    panelUserExpertiseClassDAO.listByPanel(panel).forEach(panelUserExpertiseClassDAO::delete);
    panelUserIntressClassDAO.listByPanel(panel).forEach(panelUserIntressClassDAO::delete);

    deletePanelStamps(panel);

    panelDAO.delete(panel);
  }

  /**
   * Delete panel auths
   *
   * @param panel panel
   */
  public void deletePanelAuths(Panel panel) {
    panelAuthDAO.listByPanel(panel).forEach(panelAuthDAO::delete);
  }

  /**
   * Delete panel user role actions
   *
   * @param panel panel
   */
  public void deletePanelUserRoleActions(Panel panel) {
    panelUserRoleActionDAO.listByPanel(panel).forEach(panelUserRoleActionDAO::delete);
  }

  /**
   * Delete panel bulletins
   *
   * @param panel panel
   */
  public void deletePanelBulletins(Panel panel) {
    panelBulletinDAO.listAllByPanel(panel).forEach(panelBulletinDAO::delete);
  }

  /**
   * Delete panel invitations
   *
   * @param panel panel
   */
  public void deletePanelInvitations(Panel panel) {
    panelInvitationDAO.listAllByPanel(panel).forEach(panelInvitationDAO::delete);
  }

  /**
   * Delete panel resource
   *
   * @param panel panel
   */
  public void deletePanelResource(Panel panel) {
    Folder rootFolder = panel.getRootFolder();
    panelDAO.updateRootFolder(panel, null, panel.getLastModifier());

    if (rootFolder != null) {
      resourceController.deleteResource(rootFolder);
    }
  }

  /**
   * Delete panel stamps
   *
   * @param panel panel
   */
  public void deletePanelStamps(Panel panel) {
    List<PanelStamp> stamps = panelStampDAO.listAllByPanel(panel);
    for (PanelStamp panelStamp : stamps) {
      List<QueryReply> replies = queryReplyDAO.listAllByStamp(panelStamp);
      for (QueryReply reply : replies) {
        List<QueryQuestionComment> comments = queryQuestionCommentController.listAllByQuery(reply.getQuery());
        comments.forEach(queryQuestionCommentController::removeParent);
        comments.forEach(queryQuestionCommentController::deleteQueryQuestionComment);
        queryReplyController.deleteQueryReplyAnswers(reply);
        queryReplyDAO.delete(reply);
      }

      panelStampDAO.delete(panelStamp);
    }
  }

  /**
   * Delete panel user expertise groups
   *
   * @param panel panel
   */
  public void deletePanelUserExpertiseGroups(Panel panel) {
    panelUserExpertiseGroupDAO.listAllByPanel(panel).forEach(this::deletePanelUserExpertiseGroup);
  }

  /**
   * Delete panel users
   *
   * @param panel panel
   */
  public void deletePanelUsers(Panel panel) {
    panelUserDAO.listAllByPanel(panel).forEach(panelUserDAO::delete);
  }

  /**
   * Delete panel user groups
   *
   * @param panel panel
   */
  public void deletePanelUserGroups(Panel panel) {
    panelUserGroupDAO.listAllByPanel(panel).forEach(panelUserGroupDAO::delete);
  }

  /**
   * Delete panel user expertise classes
   *
   * @param panel panel
   */
  public void deletePanelUserExpertiseClasses(Panel panel) {
    panelUserExpertiseClassDAO.listByPanel(panel).forEach(panelUserExpertiseClassDAO::delete);
  }

  /**
   * Delete panel user intress classes
   *
   * @param panel panel
   */
  public void deletePanelUserIntressClasses(Panel panel) {
    panelUserIntressClassDAO.listByPanel(panel).forEach(panelUserIntressClassDAO::delete);
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
   * Lists panels to archive
   *
   * @param waitDays wait this amount of days before archiving
   * @param maxResults max results
   *
   * @return panels
   */
  public List<Panel> listPanelsToArchive(long waitDays, int maxResults) {
    Date before = Date.from(OffsetDateTime.now().minusDays(waitDays).toInstant());
    return panelDAO.listPanelsByStateEndedAndLastModifiedBefore(before, maxResults);
  }

  /**
   * Lists panels to delete
   *
   * @param waitDays wait this amount of days before deleting
   * @param maxResults max results
   *
   * @return panels
   */
  public List<Panel> listPanelsToDelete(long waitDays, int maxResults) {
    Date before = Date.from(OffsetDateTime.now().minusDays(waitDays).toInstant());
    return panelDAO.listPanelsByStateArchivedAndLastModifiedBefore(before, maxResults);
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
    return panel.getArchived();
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

  /**
   * Deletes user expertise group
   *
   * @param panelUserExpertiseGroup panel user expertise group
   */
  private void deletePanelUserExpertiseGroup(PanelUserExpertiseGroup panelUserExpertiseGroup) {
    panelExpertiseGroupUserDAO.listByGroup(panelUserExpertiseGroup).forEach(panelExpertiseGroupUserDAO::delete);
    panelUserExpertiseGroupDAO.delete(panelUserExpertiseGroup);
  }
  
}
