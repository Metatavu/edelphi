package fi.metatavu.edelphi.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.resources.ResourceType;
import fi.metatavu.edelphi.domainmodel.users.User;

public class MaterialUtils {

  private static final String INDEX_PAGE_URLNAME = "index";
  private static final String HELP_ROOT_FOLDER = "help";
  private static final String MATERIAL_ROOT_FOLDER = "material";

  public static Long countPanelMaterials(Panel panel, boolean countFolders) {
    return getMaterialCount(panel.getRootFolder(), countFolders);
  }

  public static Long countDelfoiMaterials(Delfoi delfoi, boolean countFolders) {
    return getMaterialCount(delfoi.getRootFolder(), countFolders);
  }

  public static Long getMaterialCount(Folder folder, boolean countFolders) {
    ResourceDAO resourceDAO = new ResourceDAO();
    if (!countFolders)
      return resourceDAO.countByTypeAndFolderAndArchived(singularMaterialTypes, folder, Boolean.FALSE);
    else
      return resourceDAO.countByTypeAndFolderAndArchived(singularMaterialTypesWithFolder, folder, Boolean.FALSE);
  }
  
  public static List<MaterialBean> listPanelMaterials(Panel panel, boolean listHidden) throws IOException {
    return listFolderMaterials(panel.getRootFolder(), listHidden, false);
  }

  public static Folder getDelfoiHelpFolder(Delfoi delfoi, String language, User loggedUser) throws IOException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Resource helpResource = resourceDAO.findByUrlNameAndParentFolder(HELP_ROOT_FOLDER, delfoi.getRootFolder());
    
    if (helpResource == null) {
      FolderDAO folderDAO = new FolderDAO();
      Integer indexNumber = ResourceUtils.getNextIndexNumber(delfoi.getRootFolder());
      helpResource = folderDAO.create(loggedUser, MATERIAL_ROOT_FOLDER, HELP_ROOT_FOLDER, delfoi.getRootFolder(), indexNumber);
    }
    
    if (helpResource instanceof Folder) {
      Resource languageHelp = resourceDAO.findByUrlNameAndParentFolder(language, (Folder) helpResource);

      if (languageHelp == null) {
        FolderDAO folderDAO = new FolderDAO();
        Integer indexNumber = ResourceUtils.getNextIndexNumber((Folder) helpResource);
        languageHelp = folderDAO.create(loggedUser, language, language, (Folder) helpResource, indexNumber);
      }
      
      return (Folder) languageHelp;
    }

    return null;
  }
  
  /**
   * Finds delfoi index page document
   * 
   * @param delfoi delfoi
   * @param locale document locale
   * @return index page document or null if not defined
   */
  public static LocalDocument findIndexPageDocument(Delfoi delfoi, Locale locale) {
    String urlName = String.format("%s-%s", INDEX_PAGE_URLNAME, locale.getLanguage());
    return findLocalDocumentByParentAndUrlName(delfoi.getRootFolder(), urlName);
  }
  
  /**
   * Creates index page document
   * 
   * @param delfoi delfoi
   * @param locale locale
   * @param user logged user
   * @return created document
   */
  public static LocalDocument createIndexPageDocument(Delfoi delfoi, Locale locale, User user) {
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    String urlName = String.format("%s-%s", INDEX_PAGE_URLNAME, locale.getLanguage());
    return localDocumentDAO.create(urlName, urlName, delfoi.getRootFolder(), user, 0);
  }
  
  /**
   * Returns local document by parent folder and URL name
   * 
   * @param parentFolder parent folder
   * @param urlName URL name
   * @return Document or null if not found
   */
  public static LocalDocument findLocalDocumentByParentAndUrlName(Folder parentFolder, String urlName) {
    ResourceDAO resourceDAO = new ResourceDAO();
    
    Resource resource = resourceDAO.findByUrlNameAndParentFolder(urlName, parentFolder);
    if (resource instanceof LocalDocument) {
      return (LocalDocument) resource;
    }
    
    return null;
  }

  public static Folder getDelfoiMaterialFolder(Delfoi delfoi, String language, User loggedUser) throws IOException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Resource resource = resourceDAO.findByUrlNameAndParentFolder(MATERIAL_ROOT_FOLDER, delfoi.getRootFolder());
    
    if (resource == null) {
      FolderDAO folderDAO = new FolderDAO();
      Integer indexNumber = ResourceUtils.getNextIndexNumber(delfoi.getRootFolder());
      resource = folderDAO.create(loggedUser, MATERIAL_ROOT_FOLDER, MATERIAL_ROOT_FOLDER, delfoi.getRootFolder(), indexNumber);
    }
    
    if (resource instanceof Folder) {
      Resource languageMaterials = resourceDAO.findByUrlNameAndParentFolder(language, (Folder) resource);

      if (languageMaterials == null) {
        FolderDAO folderDAO = new FolderDAO();
        Integer indexNumber = ResourceUtils.getNextIndexNumber((Folder) resource);
        languageMaterials = folderDAO.create(loggedUser, language, language, (Folder) resource, indexNumber);
      }
      
      return (Folder) languageMaterials;
    }

    return null;
  }
  
  public static synchronized List<MaterialBean> listFolderMaterials(Folder folder, boolean listHidden, boolean listfolders) throws IOException {
    ResourceDAO resourceDAO = new ResourceDAO();
    
    List<Resource> resources = null;
        
    List<ResourceType> materialTypes = MaterialUtils.singularMaterialTypes;
    if (listfolders)
      materialTypes = MaterialUtils.singularMaterialTypesWithFolder;
    
    if (listHidden) {
      resources = resourceDAO.listByTypesAndFolderAndArchived(materialTypes, folder, Boolean.FALSE);
    } else {
      resources = resourceDAO.listByTypesAndFolderAndVisibleAndArchived(materialTypes, folder, Boolean.TRUE, Boolean.FALSE);
    }
    Collections.sort(resources, new Comparator<Resource>() {
      @Override
      public int compare(Resource o1, Resource o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    
    // TODO: Reports

    List<MaterialBean> materialBeans = new ArrayList<>();
    for (Resource resource : resources) {
      String name = resource.getName();
      Date created = resource.getCreated();
      Date modified = resource.getLastModified();
      
      materialBeans.add(new MaterialBean(resource.getId(), resource.getType(), name, resource.getFullPath(), resource.getVisible(), created, modified, resource.getIndexNumber()));
    }
    
    Collections.sort(materialBeans, new Comparator<MaterialBean>() {
      @Override
      public int compare(MaterialBean o1, MaterialBean o2) {
        return o1.getIndexNumber() - o2.getIndexNumber();
      }
    });
    
    return materialBeans;
  }

  public static Map<Long, List<MaterialBean>> listMaterialTrees(Folder parentFolder, boolean listHidden, boolean listFolders) throws IOException {
    ResourceDAO resourceDAO = new ResourceDAO();
    List<Resource> folders;
        
    List<ResourceType> materialTypes = new ArrayList<ResourceType>();
    materialTypes.add(ResourceType.FOLDER);
    
    if (listHidden) {
      folders = resourceDAO.listByTypesAndFolderAndArchived(materialTypes, parentFolder, Boolean.FALSE);
    } else {
      folders = resourceDAO.listByTypesAndFolderAndVisibleAndArchived(materialTypes, parentFolder, Boolean.TRUE, Boolean.FALSE);
    }

    Map<Long, List<MaterialBean>> result = new HashMap<Long, List<MaterialBean>>();
    
    for (Resource folder : folders) {
      List<MaterialBean> materialBeans = listFolderMaterials((Folder) folder, listHidden, listFolders);
      Collections.sort(materialBeans, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      result.put(folder.getId(), materialBeans);
    }
    
    return result;
  }
  
  private static List<ResourceType> singularMaterialTypes = Arrays.asList(new ResourceType[]{
    ResourceType.GOOGLE_DOCUMENT, 
    ResourceType.GOOGLE_IMAGE, 
    ResourceType.LINKED_IMAGE, 
    ResourceType.LOCAL_DOCUMENT, 
    ResourceType.LOCAL_IMAGE, 
    ResourceType.VIDEO
  });
  
  private static List<ResourceType> singularMaterialTypesWithFolder = Arrays.asList(new ResourceType[]{
    ResourceType.GOOGLE_DOCUMENT, 
    ResourceType.GOOGLE_IMAGE, 
    ResourceType.LINKED_IMAGE, 
    ResourceType.LOCAL_DOCUMENT, 
    ResourceType.LOCAL_IMAGE, 
    ResourceType.VIDEO,
    ResourceType.FOLDER
  });
  
}
