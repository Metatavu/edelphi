package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.MaterialUtils;

public class ImportMaterialsGDocsPageController extends PanelPageController {

  public ImportMaterialsGDocsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_MATERIALS;
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
  	Drive drive = GoogleDriveUtils.getAuthenticatedService(pageRequestContext);
		if (drive != null) {
			PanelDAO panelDAO = new PanelDAO();
      Long panelId = pageRequestContext.getLong("panelId");
      Panel panel = panelDAO.findById(panelId);
      
      try {
      	List<GoogleDocumentBean> googleDocuments = new ArrayList<GoogleDocumentBean>();
        
      	// TODO: Navigation (next, prev, etc)
      	// TODO: Support folders 
      	FileList files = GoogleDriveUtils.listFiles(drive, "mimeType != 'application/vnd.google-apps.folder' and trashed != true");
      	for (File file : files.getFiles()) {
          String iconLink = GoogleDriveUtils.getIconLink(file);
          googleDocuments.add(new GoogleDocumentBean(file, iconLink));
      	}
      	
        pageRequestContext.getRequest().setAttribute("googleDocuments", googleDocuments);
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("materials", MaterialUtils.listPanelMaterials(panel, true));

        ActionUtils.includeRoleAccessList(pageRequestContext);
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/importmaterialsgdocs.jsp");
      } catch (Exception e) {
        Messages messages = Messages.getInstance();
        Locale locale = pageRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
      }
      
		}
  }

  public class GoogleDocumentBean {

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

    private String resourceId;
    private String title;
    private String iconUrl;
    private String kind;
  }

}