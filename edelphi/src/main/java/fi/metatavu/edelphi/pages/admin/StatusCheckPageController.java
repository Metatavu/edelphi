package fi.metatavu.edelphi.pages.admin;

import java.io.IOException;

import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiDefaults;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class StatusCheckPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    DelfoiDefaultsDAO delfoiDefaultsDAO = new DelfoiDefaultsDAO();
    DelfoiDefaults delfoiDefaults = delfoiDefaultsDAO.findByDelfoi(delfoi);
    if (delfoiDefaults != null) {
      try {
        pageRequestContext.getResponse().getWriter().print("OK");
      }
      catch (IOException ioe) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, "Response I/O error", ioe);
      }
    }
    else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, "Missing DelfoiDefaults");
    }
  }

}