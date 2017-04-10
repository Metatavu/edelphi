package fi.metatavu.edelphi.pages.panel;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.utils.ActionUtils;

public class ManagePanelFolderPageController extends PanelPageController {

  public ManagePanelFolderPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    FolderDAO folderDAO = new FolderDAO();

    if (!pageRequestContext.getBoolean("newFolder")) {
      Long folderId = pageRequestContext.getLong("folderId");
      Folder folder = folderDAO.findById(folderId);
      
      pageRequestContext.getRequest().setAttribute("folder", folder);
      pageRequestContext.getRequest().setAttribute("folderId", folder.getId());

    } else {
      pageRequestContext.getRequest().setAttribute("folderId", "NEW");
    }
      
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepanelfolder.jsp");
  }
  
}
