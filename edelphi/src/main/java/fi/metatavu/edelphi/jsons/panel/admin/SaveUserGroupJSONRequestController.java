package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserGroupDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class SaveUserGroupJSONRequestController extends JSONController {

  public SaveUserGroupJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    UserDAO userDAO = new UserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    User currentUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    Long userGroupId = jsonRequestContext.getLong("userGroupId");
    String userGroupName = jsonRequestContext.getString("name");
    String[] userIds = StringUtils.split(jsonRequestContext.getString("userIds"), ","); 
    List<User> users = new ArrayList<User>();
    if (userIds != null) {
      for (String userId : userIds) {
        users.add(userDAO.findById(new Long(userId)));
      }
    }
    
    PanelUserGroup panelUserGroup = null;
    if (userGroupId == null) {
      panelUserGroup = panelUserGroupDAO.create(panel, userGroupName, users, panel.getCurrentStamp(), currentUser);
    }
    else {
      panelUserGroup = panelUserGroupDAO.findById(userGroupId);
      panelUserGroupDAO.update(panelUserGroup, userGroupName, users, currentUser);
    }

    JSONObject jsonObj = new JSONObject();
    jsonObj.put("id", panelUserGroup.getId());
    jsonObj.put("name", panelUserGroup.getName());
    jsonObj.put("created", panelUserGroup.getCreated().getTime());
    jsonObj.put("modified", panelUserGroup.getLastModified().getTime());
    jsonRequestContext.addResponseParameter("userGroup", jsonObj.toString());
  }
  
}
