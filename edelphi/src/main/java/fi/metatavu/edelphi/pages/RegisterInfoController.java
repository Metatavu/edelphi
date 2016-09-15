package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;

public class RegisterInfoController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/registerinfo.jsp");
  }

}
