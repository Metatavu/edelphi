package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.Severity;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelBulletinDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdatePanelBulletinJSONRequestController extends JSONController {

  public UpdatePanelBulletinJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_BULLETINS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();
    UserDAO userDAO = new UserDAO();

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    Long bulletinId = jsonRequestContext.getLong("bulletinId");
    String title = jsonRequestContext.getString("title");
    String message = jsonRequestContext.getString("message");

    PanelBulletin bulletin = panelBulletinDAO.findById(bulletinId);
    panelBulletinDAO.updateTitle(bulletin, title, loggedUser);
    panelBulletinDAO.updateMessage(bulletin, message, loggedUser);
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.managePanelBulletins.bulletinUpdated"));
    jsonRequestContext.addResponseParameter("bulletinId", bulletin.getId());
  }

}
