package fi.metatavu.edelphi.jsons.resources;

import java.util.Date;
import java.util.Locale;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.FeatureNotAvailableOnSubscriptionLevelException;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.GoogleDocumentDAO;
import fi.metatavu.edelphi.dao.resources.GoogleImageDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public class ImportGDocsJSONRequestController extends JSONController {

  @Override
  public void authorize(RequestContext requestContext) {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long parentFolderId = requestContext.getLong("parentFolderId");
    if (parentFolderId != null) {
      Resource resource = resourceDAO.findById(parentFolderId);
      Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
      if (resourcePanel != null) {
        authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
      } else {
        Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
        authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
      }
    }
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();
    GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
    GoogleImageDAO googleImageDAO = new GoogleImageDAO();
    
    Locale locale = jsonRequestContext.getRequest().getLocale();

    Drive drive = GoogleDriveUtils.getAuthenticatedService(jsonRequestContext);
    if (drive == null) {
      throw new AccessDeniedException(jsonRequestContext.getRequest().getLocale());
    }
    
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    if (parentFolderId == null) {
      throw new PageNotFoundException(locale);
    }
    
    String[] selectedGDocs = jsonRequestContext.getStrings("selectedgdoc");

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    boolean isFeatureEnabled = SubscriptionLevelUtils.isFeatureEnabled(loggedUser.getSubscriptionLevel(), Feature.MANAGE_PANEL_MATERIALS);

    if (!isFeatureEnabled) {
      SubscriptionLevel minimumSubscriptionLevel = SubscriptionLevelUtils.getMinimumLevelFor(Feature.MANAGE_PANEL_MATERIALS);
      throw new FeatureNotAvailableOnSubscriptionLevelException(jsonRequestContext.getRequest().getLocale(), loggedUser.getSubscriptionLevel(), minimumSubscriptionLevel);
    }

    try {
      Folder parentFolder = folderDAO.findById(parentFolderId);

      for (String resourceId : selectedGDocs) {
      	String googleDriveAccountId = GoogleDriveUtils.getAdminAccountId();

        GoogleDriveUtils.insertUserPermission(drive, resourceId, googleDriveAccountId, "reader");
        File file = GoogleDriveUtils.getFile(drive, resourceId);
        boolean isImage = file.getImageMediaMetadata() != null;
        Date created = new Date(file. getCreatedTime().getValue());
        Date lastModified = new Date(file.getModifiedTime().getValue());

        String name = file.getName();
        String urlName = ResourceUtils.getUniqueUrlName(name, parentFolder);
        
        Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
        if (isImage) {
          googleImageDAO.create(name, urlName, parentFolder, resourceId, indexNumber, new Date(), loggedUser, created, loggedUser, lastModified);
        } else {
          googleDocumentDAO.create(name, urlName, parentFolder, resourceId, indexNumber, new Date(), loggedUser, created, loggedUser, lastModified);
        }
      }
    }
    catch (Exception e) {
      Messages messages = Messages.getInstance();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
  }

}
