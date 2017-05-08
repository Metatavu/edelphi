package fi.metatavu.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.MaterialBean;
import fi.metatavu.edelphi.utils.MaterialBeanNameComparator;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ImportMaterialsGDocsPageController extends DelfoiPageController {

  private static final Logger logger = Logger.getLogger(ImportMaterialsGDocsPageController.class.getName());

  public ImportMaterialsGDocsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Drive drive = GoogleDriveUtils.getAuthenticatedService(pageRequestContext);
		if (drive != null) {
		  Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
	    if (delfoi == null) {
	      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
	    }

	    Messages messages = Messages.getInstance();
	    Locale locale = pageRequestContext.getRequest().getLocale();
	    User loggedUser = RequestUtils.getUser(pageRequestContext);

      String language = pageRequestContext.getString("lang");
      if (StringUtils.isEmpty(language)) {
        language = locale.getLanguage();
      }

		  Folder folder = null;
	    String category = pageRequestContext.getString("cat");
	    folder = resolveFolder(delfoi, loggedUser, language, category);
	    
	    if (folder == null) {
	      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
	    }
		  
			try {
      	List<GoogleDocumentBean> googleDocuments = new ArrayList<>();
        
      	FileList files = GoogleDriveUtils.listFiles(drive, "mimeType != 'application/vnd.google-apps.folder' and trashed != true");
      	for (File file : files.getFiles()) {
      	  String iconLink = GoogleDriveUtils.getIconLink(file);
          googleDocuments.add(new GoogleDocumentBean(file, iconLink));
      	}

      	List<MaterialBean> materials = MaterialUtils.listFolderMaterials(folder, true, true);
        Collections.sort(materials, new MaterialBeanNameComparator());
      	
        pageRequestContext.getRequest().setAttribute("googleDocuments", googleDocuments);
        pageRequestContext.getRequest().setAttribute("parentFolderId", folder.getId());
      	pageRequestContext.getRequest().setAttribute("dashboardCategory", category);
        pageRequestContext.getRequest().setAttribute("dashboardLang", pageRequestContext.getString("lang"));
        pageRequestContext.getRequest().setAttribute("materials", materials);
        pageRequestContext.getRequest().setAttribute("materialTrees", MaterialUtils.listMaterialTrees(folder, true, true));

        ActionUtils.includeRoleAccessList(pageRequestContext);
        pageRequestContext.setIncludeJSP("/jsp/pages/admin/importmaterialsgdocs.jsp");
      } catch (Exception e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
      }
      
		}
  }

  private Folder resolveFolder(Delfoi delfoi, User loggedUser, String language, String category) {
    try {
      if ("help".equals(category)) {
        return MaterialUtils.getDelfoiHelpFolder(delfoi, language, loggedUser); 
      } else {
        if ("materials".equals(category)) {
          return MaterialUtils.getDelfoiMaterialFolder(delfoi, language, loggedUser);
        }
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, String.format("Error occurred while resolving category %s folder", category), ex);
    }
    
    return null;
  }

  public class GoogleDocumentBean {

    private String resourceId;
    private String title;
    private String iconUrl;
    private String kind;

    public GoogleDocumentBean(File file, String iconLink) {
      this.resourceId = file.getId();
      this.title = file.getName();
      this.iconUrl = iconLink;
      this.kind = file.getKind();
    }

    public String getResourceId() {
      return resourceId;
    }

    public String getTitle() {
      return title;
    }
    
    public String getIconUrl() {
			return iconUrl;
		}

    public String getKind() {
      return kind;
    }
  }

}