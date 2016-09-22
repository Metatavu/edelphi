package fi.metatavu.edelphi.jsons.profile;

import java.util.Locale;

import org.apache.commons.fileupload.FileItem;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.users.UserPictureDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserPicture;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdateUserPictureJSONRequestController extends JSONController {

  public UpdateUserPictureJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserPictureDAO pictureDAO = new UserPictureDAO();
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    FileItem file = jsonRequestContext.getFile("imageData");
    if (file.getSize() > 102400) {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.PROFILE_IMAGE_TOO_LARGE, messages.getText(locale, "exception.1029.profileImageTooLarge"));
    }
    byte[] data = file.get();
    String contentType = file.getContentType();
    
    UserPicture picture = pictureDAO.findByUser(loggedUser);
    if (picture != null)
      pictureDAO.updateData(picture, contentType, data);
    else
      pictureDAO.create(loggedUser, contentType, data);

    jsonRequestContext.getRequest().getSession(true).setAttribute("loggedUserHasPicture", Boolean.TRUE);
  }
  
}
