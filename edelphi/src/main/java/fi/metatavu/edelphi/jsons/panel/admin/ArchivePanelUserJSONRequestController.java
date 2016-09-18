package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.UserUtils;

public class ArchivePanelUserJSONRequestController extends JSONController {

  public ArchivePanelUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUser panelUser = panelUserDAO.findById(panelUserId);

    if (!panelUser.getUser().getId().equals(jsonRequestContext.getLoggedUserId())) {
      UserUtils.archivePanelUser(panelUser, RequestUtils.getUser(jsonRequestContext));
    }
    else {
      // Cannot archive yourself
      throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_ARCHIVE_SELF_FROM_PANEL, messages.getText(locale, "exception.1021.cannotArchiveSelfFromPanel"));
    }
    
    jsonRequestContext.addMessage(Severity.OK, messages.getText(
        locale, "panel.admin.managePanelUsers.msgUserRemovedSuccessfully"));
  }
  
}
