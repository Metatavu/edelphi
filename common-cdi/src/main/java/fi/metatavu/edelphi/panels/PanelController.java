package fi.metatavu.edelphi.panels;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.RestMessages;
import fi.metatavu.edelphi.resources.ResourceController;
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
  private ResourceController resourceController;
  
  @Inject
  private RestMessages restMessages;

  @Inject
  private DelfoiDAO delfoiDAO;

  @Inject
  private FolderDAO folderDAO;

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
  private PanelSettingsTemplateDAO panelSettingsTemplateDAO;

  @Inject
  private PanelUserDAO panelUserDAO;

  @Inject
  private DelfoiDefaultsDAO delfoiDefaultsDAO;
  
  @Inject
  private PanelUserRoleActionDAO panelUserRoleActionDAO;
  
  @Inject
  private PanelSettingsTemplateRoleDAO panelSettingsTemplateRoleDAO;
  
  /**
   * Creates new panel
   * 
   * @param locale locale
   * @param name name
   * @param description description
   * @param panelSettingsTemplate settings template
   * @param creator creator
   * 
   * @return created panel
   */
  public Panel createPanel(Locale locale, String name, String description, PanelSettingsTemplate panelSettingsTemplate, User creator) {
    Delfoi delfoi = getDelfoi();
    DelfoiDefaults defaults = delfoiDefaultsDAO.findByDelfoi(delfoi);
    
    String urlName = resourceController.getUniqueUrlName(name, delfoi.getRootFolder());
    Integer indexNumber = resourceController.getNextIndexNumber(delfoi.getRootFolder());
    Folder rootFolder = folderDAO.create(creator, name, urlName, delfoi.getRootFolder(), indexNumber);

    PanelState state = panelSettingsTemplate.getState();
    PanelAccessLevel accessLevel = panelSettingsTemplate.getAccessLevel();
    PanelUserRole defaultPanelUserRole = panelSettingsTemplate.getDefaultPanelUserRole();
    
    Panel panel = panelDAO.create(delfoi, name, description, rootFolder, state, accessLevel, defaultPanelUserRole, creator);
    
    // Create panel stamp
    PanelStamp panelStamp = panelStampDAO.create(panel, restMessages.getText(locale, "createPanel.server.defaultStampName"), null, null, creator);
    panelDAO.updateCurrentStamp(panel, panelStamp, creator);
    
    // Add Creator to panel default creator role
    panelUserDAO.create(panel, creator, defaults.getDefaultPanelCreatorRole(), PanelUserJoinType.ADDED, panelStamp, creator);

    // Create RoleActions from template role settings
    List<PanelSettingsTemplateRole> templateRoles = panelSettingsTemplateRoleDAO.listByTemplate(panelSettingsTemplate);
    for (PanelSettingsTemplateRole templateRole : templateRoles) {
      panelUserRoleActionDAO.create(panel, templateRole.getDelfoiAction(), templateRole.getUserRole());
    }
    
    return panel;
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
   * Finds a panel by UrlName
   * 
   * @param urlName UrlName
   * @return panel or null if not found
   */
  public Panel findPanelByUrlName(String urlName) {
    Delfoi delfoi = getDelfoi();
    
    Folder delfoiRootFolder = delfoi.getRootFolder();
    if (delfoiRootFolder == null) {
      return null;
    }
    
    Folder panelRootFolder = folderDAO.findByUrlNameAndParentFolderAndArchived(urlName, delfoiRootFolder, Boolean.FALSE);
    if (panelRootFolder == null) {
      return null;
    }
    
    return panelDAO.findByRootFolder(panelRootFolder);
  }
  
  /**
   * Lists panels
   * 
   * @param urlName filter list by UrlName. Ignored if null given
   * @return list of panels
   */
  public List<Panel> listPanels(String urlName) {
    List<Panel> result = null;
    
    if (urlName != null) {
      Panel panel = findPanelByUrlName(urlName); 
      result = panel != null ? Collections.singletonList(panel) : Collections.emptyList();
    } else {
      result = panelDAO.listAll();
    }
    
    return result.stream()
      .filter(this::isPanelNotArchived)
      .collect(Collectors.toList());
  }

  /**
   * Updates panel
   * 
   * @param panel panel
   * @param name panel name
   * @param description panel description
   * @param accessLevel panel access level
   * @param state panel state
   * @param modifier modifier
   * @return updated panel
   */
  public Panel updatePanel(Panel panel, String name, String description, PanelAccessLevel accessLevel, PanelState state, User modifier) {
    return panelDAO.update(panel, name, description, accessLevel, state, modifier);
  }

  /**
   * Delete panel
   * 
   * If system is running in test mode panel is deleted permanently otherwise panel is archived
   * 
   * @param panel panel to be deleted
   */
  public void deletePanel(Panel panel) {
    if (settingsController.isInTestMode()) {
      panelUserRoleActionDAO.listByPanel(panel).forEach(panelUserRoleActionDAO::delete);
      panelUserDAO.listByPanel(panel).forEach(this::deletePanelUser);
      panelStampDAO.listByPanel(panel).forEach(panelStampDAO::delete);
      panelDAO.delete(panel);
    } else {
      panelDAO.archive(panel);
    }
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
   * Returns panel settings template by id
   * 
   * @param id id
   * @return panel settings template or null if not found
   */
  public PanelSettingsTemplate findPanelSettingsTemplateById(Long id) {
    return panelSettingsTemplateDAO.findById(id);
  }

  /**
   * Returns default panel settings template
   * 
   * @return default panel settings template
   */
  public PanelSettingsTemplate findDefaultPanelSettingsTemplate() {
    return panelSettingsTemplateDAO.findFirst();
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
   * Returns whether panel is archived or not
   * 
   * @param panel panel
   * @return whether panel is archived or not
   */
  public boolean isPanelNotArchived(Panel panel) {
    return !isPanelArchived(panel);
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
   * Deletes panel user 
   * 
   * @param panelUser panel user
   */
  private void deletePanelUser(PanelUser panelUser) {
    panelUserDAO.delete(panelUser);
  }
  
  /**
   * Returns Delfoi instance
   * 
   * @return Delfoi instance
   */
  private Delfoi getDelfoi() {
    return delfoiDAO.findById(settingsController.getDelfoiId());
  }
  
}
