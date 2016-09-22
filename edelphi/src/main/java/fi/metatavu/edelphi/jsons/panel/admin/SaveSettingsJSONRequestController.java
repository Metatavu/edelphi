package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class SaveSettingsJSONRequestController extends JSONController {

  public SaveSettingsJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    Long id = jsonRequestContext.getLong("panelId");
    String name = jsonRequestContext.getString("panelName");
    String urlName = ResourceUtils.getUrlName(name);
    String description = jsonRequestContext.getString("panelDescription");
    PanelAccessLevel accessLevel = PanelAccessLevel.valueOf(jsonRequestContext.getString("panelAccess"));
    PanelState state = PanelState.valueOf(jsonRequestContext.getString("panelState"));
    
    PanelDAO panelDAO = new PanelDAO();
    UserDAO userDAO = new UserDAO();
    FolderDAO folderDAO = new FolderDAO();

    User currentUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Panel panel = panelDAO.findById(id);
    Folder folder = panel.getRootFolder();
    
    Folder nameFolder = folderDAO.findByUrlNameAndParentFolderAndArchived(urlName, folder.getParentFolder(), Boolean.FALSE);
    if (nameFolder != null && !nameFolder.getId().equals(folder.getId())) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_PANEL_NAME, messages.getText(locale, "exception.1004.panelNameInUse"));
    }
    else {
      panelDAO.update(panel, name, description, accessLevel, state, currentUser);
      folderDAO.updateName(folder, name, urlName, currentUser);
      jsonRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "panel.admin.dashboard.panelSettings.saved"));
    }
    
  }

}
