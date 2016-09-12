package fi.metatavu.edelphi.jsons.panel;

import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class CreatePanelJSONRequestController extends JSONController {

  public CreatePanelJSONRequestController() {
    super();
    
    setAccessAction(DelfoiActionName.CREATE_PANEL, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    FolderDAO folderDAO = new FolderDAO();
    PanelSettingsTemplateDAO panelSettingsTemplateDAO = new PanelSettingsTemplateDAO();
    DelfoiDefaultsDAO delfoiDefaultsDAO = new DelfoiDefaultsDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelSettingsTemplateRoleDAO templateRoleDAO = new PanelSettingsTemplateRoleDAO();
    PanelUserRoleActionDAO roleActionDAO = new PanelUserRoleActionDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
   
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    DelfoiDefaults defaults = delfoiDefaultsDAO.findByDelfoi(delfoi);
    
    String name = jsonRequestContext.getString("panelName");
    String urlName = ResourceUtils.getUrlName(name);
    String description = jsonRequestContext.getString("panelDescription");
    Long settingsTemplateId = jsonRequestContext.getLong("panelSettingsTemplateId");
    PanelSettingsTemplate panelSettingsTemplate = panelSettingsTemplateDAO.findById(settingsTemplateId);

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    if (ResourceUtils.isUrlNameAvailable(urlName, delfoi.getRootFolder())) {
      // Create Panel Root Folder
      // TODO: Would it be better if parentFolder was null?
      Integer indexNumber = ResourceUtils.getNextIndexNumber(delfoi.getRootFolder());
      Folder rootFolder = folderDAO.create(loggedUser, name, urlName, delfoi.getRootFolder(), indexNumber);
      
      // Create Panel
      Panel panel = panelDAO.create(
          delfoi, name, description, rootFolder,
          panelSettingsTemplate.getState(), 
          panelSettingsTemplate.getAccessLevel(),
          panelSettingsTemplate.getDefaultPanelUserRole(),
          loggedUser);
      
      // Create panel stamp
      PanelStamp panelStamp = panelStampDAO.create(panel, messages.getText(locale, "createPanel.server.defaultStampName"), null, null, loggedUser);
      panelDAO.updateCurrentStamp(panel, panelStamp, loggedUser);
      
      // Add Creator to panel default creator role
      panelUserDAO.create(panel, loggedUser, defaults.getDefaultPanelCreatorRole(), PanelUserJoinType.ADDED, panelStamp, loggedUser);

      // Create RoleActions from template role settings
      List<PanelSettingsTemplateRole> templateRoles = templateRoleDAO.listByTemplate(panelSettingsTemplate);
      for (PanelSettingsTemplateRole templateRole : templateRoles) {
        roleActionDAO.create(panel, templateRole.getDelfoiAction(), templateRole.getUserRole());
      }

      jsonRequestContext.addResponseParameter("panelId", panel.getId());
    }
    else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_PANEL_NAME, messages.getText(locale, "exception.1004.panelNameInUse"));
    }
  }
  
}
