package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ReportIssuePageController extends PanelPageController {

  public ReportIssuePageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/reportissue.jsp");
  }
}
