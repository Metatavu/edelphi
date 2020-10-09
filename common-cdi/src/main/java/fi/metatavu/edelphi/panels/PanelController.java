package fi.metatavu.edelphi.panels;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
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
  
}
