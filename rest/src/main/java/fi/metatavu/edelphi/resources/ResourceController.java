package fi.metatavu.edelphi.resources;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.panels.PanelController;

/**
 * Controller for resources
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ResourceController {

  @Inject
  private PanelController panelController;
  
  @Inject
  private PanelDAO panelDAO;
  
  /**
   * Returns a panel for given resource
   * 
   * @param resource resource
   * @return panel or null if not found
   */
  public Panel getResourcePanel(Resource resource) {
    List<Folder> resourceFolders = new ArrayList<Folder>();
    Resource current = resource;
    Folder folder = current instanceof Folder ? (Folder) current : current == null ? null : current.getParentFolder();
    while (folder != null) {
      resourceFolders.add(folder);
      current = folder;
      folder = current.getParentFolder();
    }
    
    Folder panelFolder = null;
    int panelIndex = resourceFolders.size() - 2;

    if (panelIndex >= 0) {
      panelFolder = resourceFolders.get(panelIndex);
    }

    if (panelFolder != null) {
      return panelDAO.findByRootFolder(panelFolder);
    }
    
    return null;
  }
  
  /**
   * Returns whether folder is archived
   * 
   * @param folder folder
   * @return whether folder is archived
   */
  public boolean isFolderArchived(Folder folder) {
    if (folder.getArchived()) {
      return true;
    }
    
    Folder parentFolder = folder.getParentFolder();
    if (parentFolder != null) {
      return isFolderArchived(parentFolder);
    } else {
      Panel panel = panelDAO.findByRootFolder(parentFolder);
      if (panel != null) {
        return panelController.isPanelArchived(panel);
      }
    }

    return false;
  }
  
}
