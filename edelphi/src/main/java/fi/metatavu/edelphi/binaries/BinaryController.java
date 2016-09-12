package fi.metatavu.edelphi.binaries;

import fi.metatavu.edelphi.smvc.AccessDeniedException;
import fi.metatavu.edelphi.smvc.LoginRequiredException;
import fi.metatavu.edelphi.smvc.controllers.BinaryRequestController;
import fi.metatavu.edelphi.smvc.controllers.RequestContext;

public abstract class BinaryController implements BinaryRequestController {

  @Deprecated
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
  }

}
