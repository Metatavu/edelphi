package fi.metatavu.edelphi.jsons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.BulletinUtils;
import fi.metatavu.edelphi.utils.SessionUtils;

public class GetImportantBulletinsJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    
    ArrayList<Map<String, Object>> result = new ArrayList<>();
    
    if (jsonRequestContext.isLoggedIn()) {
      User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
      List<DelfoiBulletin> importantBulletins = BulletinUtils.getUnreadImportantBulletins(loggedUser);
       
      for (DelfoiBulletin importantBulletin : importantBulletins) {
        Map<String, Object> resultObject = new HashMap<>();
        resultObject.put("title", importantBulletin.getTitle());
        resultObject.put("message", importantBulletin.getMessage());
        resultObject.put("created", importantBulletin.getCreated().getTime());
        result.add(resultObject);
        BulletinUtils.markBulletinRead(importantBulletin, loggedUser);
      }
    }
    
    SessionUtils.setHasImportantBulletins(jsonRequestContext.getRequest().getSession(false), false);
    
    jsonRequestContext.addResponseParameter("results", result);
  }

}
