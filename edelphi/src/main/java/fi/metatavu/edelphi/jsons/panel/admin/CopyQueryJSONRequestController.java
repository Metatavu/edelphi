package fi.metatavu.edelphi.jsons.panel.admin;

import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;

import java.util.Locale;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CopyQueryJSONRequestController extends JSONController {

  public CopyQueryJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    PanelDAO panelDAO = new PanelDAO();
    
    Query query = queryDAO.findById(jsonRequestContext.getLong("query"));
    Panel targetPanel = panelDAO.findById(jsonRequestContext.getLong("panel"));
    String newName = jsonRequestContext.getString("name");
    boolean copyData = jsonRequestContext.getBoolean("copyData");
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    QueryUtils.copyQuery(loggedUser, locale, query, newName, targetPanel, copyData, copyData);
  }

}
