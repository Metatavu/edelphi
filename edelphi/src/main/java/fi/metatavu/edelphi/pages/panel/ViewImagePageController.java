package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.ImageDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Image;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ViewImagePageController extends PanelPageController {

  public ViewImagePageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) { 
    // TODO: If query is hidden only users with manage material rights should be able to enter
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    ImageDAO imageDAO = new ImageDAO();
    Long imageId = pageRequestContext.getLong("imageId");
    Image image = imageDAO.findById(imageId);
    if (image == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("image", image);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewimage.jsp");
  }
}
