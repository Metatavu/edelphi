package fi.metatavu.edelphi.binaries.user;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.dao.users.UserPictureDAO;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserPicture;

public class ViewUserPictureBinaryRequestController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    UserDAO userDAO = new UserDAO();
    UserPictureDAO pictureDAO = new UserPictureDAO();

    Long userId = binaryRequestContext.getLong("userId");
    
    User user = userDAO.findById(userId);
    UserPicture picture = pictureDAO.findByUser(user);
  
    if (picture != null) {
      binaryRequestContext.setResponseContent(picture.getData(), picture.getContentType());
    } else {
      throw new PageNotFoundException(binaryRequestContext.getRequest().getLocale());
    }
  }
  
}
