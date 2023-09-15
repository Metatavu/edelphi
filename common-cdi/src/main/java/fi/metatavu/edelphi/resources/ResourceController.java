package fi.metatavu.edelphi.resources;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.dao.resources.ResourceLockDAO;
import fi.metatavu.edelphi.domainmodel.resources.*;
import fi.metatavu.edelphi.queries.QueryController;
import org.apache.commons.lang3.StringUtils;

import com.github.slugify.Slugify;

import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
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
  private QueryController queryController;
  
  @Inject
  private PanelDAO panelDAO;
  
  @Inject
  private ResourceDAO resourceDAO;

  @Inject
  private ResourceLockDAO resourceLockDAO;

  @Inject
  private LocalDocumentPageDAO localDocumentPageDAO;
  
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
  
  /**
   * Returns next available index number for given parent folder
   * 
   * @param parentFolder parent folder
   * @return next available index number
   */
  public Integer getNextIndexNumber(Folder parentFolder) {
    Integer num = resourceDAO.findMaxIndexNumber(parentFolder);
    if (num != null)
      return num.intValue() + 1;
    else
      return new Integer(0);
  }

  /**
   * Creates an URL friendly version of given string
   * 
   * @param string string
   * @return an URL friendly version of given string
   */
  public String getUrlName(String string) {
    Slugify slugify = new Slugify();
    return slugify.slugify(string);
  }
  
  /**
   * Returns whether a URL name is available in given folder
   * 
   * @param urlName URL name
   * @param parentFolder parent folder
   * @return whether a URL name is available in given folder
   */
  public boolean isUrlNameAvailable(String urlName, Folder parentFolder) {
    if (StringUtils.isEmpty(urlName)) {
      return false;
    }

    Resource resource = resourceDAO.findByUrlNameAndParentFolder(urlName, parentFolder);
    return resource == null;
  }

  /**
   * Returns whether a URL name is available in given folder
   * 
   * @param urlName URL name
   * @param parentFolder parent folder
   * @param ownerResource owner resource
   * @return whether a URL name is available in given folder
   */
  public boolean isUrlNameAvailable(String urlName, Folder parentFolder, Resource ownerResource) {
    if (StringUtils.isEmpty(urlName)) {
      return false;
    }
    
    Resource resource = resourceDAO.findByUrlNameAndParentFolder(urlName, parentFolder);
    return resource == null || resource.getId().equals(ownerResource.getId());
  }
  
  /**
   * Generates a unique name for resource in given folder
   * 
   * @param name resource name
   * @param parentFolder parent folder
   * @return a unique name for resource 
   */
  public String getUniqueUrlName(String name, Folder parentFolder) {
    int i = 1;
    String urlName = getUrlName(name);
    while (!isUrlNameAvailable(urlName, parentFolder)) {
      urlName = getUrlName(name == null ? ++i + "" : name + " (" + (++i) + ")");
    }
    return urlName;
  }

  /**
   * Deletes a resource, including all child-resources and queries and query datas
   *
   * @param resource resource
   */
  public void deleteResource(Resource resource) {
    ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
    if (resourceLock != null) {
      resourceLockDAO.delete(resourceLock);
    }

    if (resource instanceof Folder) {
      Folder folder = (Folder) resource;
      List<Resource> resources = resourceDAO.listAllByParentFolder(folder);
      for (Resource childResource : resources) {
        deleteResource(childResource);
      }
    }

    if (resource instanceof LocalDocument) {
      localDocumentPageDAO.listByDocument((LocalDocument) resource).forEach(localDocumentPageDAO::delete);
    }

    if (resource instanceof Query) {
      queryController.deleteQuery((Query) resource);
    } else {
      resourceDAO.delete(resource);
    }
  }
  
}