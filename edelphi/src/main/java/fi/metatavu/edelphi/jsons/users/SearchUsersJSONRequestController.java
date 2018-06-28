package fi.metatavu.edelphi.jsons.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;

public class SearchUsersJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    
    // Search string preparation
    
    String text = jsonRequestContext.getLowercaseString("text");
    if (!StringUtils.isBlank(text)) {
      text = text.replace("*", "").replace("%", "");
    }
    
    List<Map<String, Object>> jsonResults = new ArrayList<Map<String, Object>>();
    if (!StringUtils.isBlank(text)) {
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      List<User> users = userEmailDAO.listUsersByAddressLike(String.format("%s%%", text), 0, 50);

      for (User user : users) {
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("email", user.getDefaultEmail() == null ? null : user.getDefaultEmail().getObfuscatedAddress());
        jsonResults.add(userInfo);
      }
      
    }
    
    jsonRequestContext.addResponseParameter("results", jsonResults);
  }

}
