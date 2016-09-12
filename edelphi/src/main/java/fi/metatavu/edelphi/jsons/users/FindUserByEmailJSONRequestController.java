package fi.metatavu.edelphi.jsons.users;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.jsons.JSONController;

public class FindUserByEmailJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserEmail userEmail = userEmailDAO.findByAddress(jsonRequestContext.getString("email"));
    if (userEmail != null) {
      jsonRequestContext.addResponseParameter("userId", userEmail.getUser().getId());
      jsonRequestContext.addResponseParameter("userEmailId", userEmail.getId());
    }
  }

}
