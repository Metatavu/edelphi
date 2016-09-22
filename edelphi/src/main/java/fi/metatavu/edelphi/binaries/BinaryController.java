package fi.metatavu.edelphi.binaries;

import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.LoginRequiredException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;

public abstract class BinaryController implements BinaryRequestController {

  @Deprecated
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
  }

}
