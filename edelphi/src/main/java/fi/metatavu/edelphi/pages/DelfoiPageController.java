package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public abstract class DelfoiPageController extends PageController {
  
  public abstract void processPageRequest(PageRequestContext pageRequestContext);

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    if (delfoi == null) {
      throw new IllegalStateException("DelfoiPageController has no delfoi");
    }
    setJsDataVariable(pageRequestContext, "securityContextId", delfoi.getId().toString());
    setJsDataVariable(pageRequestContext, "securityContextType", "DELFOI");
    
    processPageRequest(pageRequestContext);
  }
}
