package fi.metatavu.edelphi.pages;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ReportIssuePageController extends PageController {

  public ReportIssuePageController() {
    super();
    // TODO: User should be at least guest to access this page
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    pageRequestContext.setIncludeJSP("/jsp/pages/reportissue.jsp");
  }
}
