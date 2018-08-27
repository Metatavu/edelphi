package fi.metatavu.edelphi.pages.admin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.LocalDocumentComparator;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceLockUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EditIndexPageDocumentPageController extends DelfoiPageController {

  public EditIndexPageDocumentPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    User loggedUser = RequestUtils.getUser(pageRequestContext);
  
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    LocalDocument indexPageDocument = MaterialUtils.findIndexPageDocument(delfoi, locale);
    if (indexPageDocument == null) {
      indexPageDocument = MaterialUtils.createIndexPageDocument(delfoi, locale, loggedUser);
    }
  
    if (ResourceLockUtils.isLocked(loggedUser, indexPageDocument)) {
      User lockCreator = ResourceLockUtils.getResourceLockCreator(indexPageDocument);
      pageRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panelAdmin.block.localDocumentEditor.lockedMessage", new Object[] { lockCreator.getFullName(false, true) }));
    } else {
      ResourceLockUtils.lockResource(loggedUser, indexPageDocument);

      List<LocalDocumentPage> localDocumentPages = localDocumentPageDAO.listByDocument(indexPageDocument).stream()
        .sorted(new LocalDocumentComparator())
        .collect(Collectors.toList());

      pageRequestContext.getRequest().setAttribute("localDocument", indexPageDocument);
      pageRequestContext.getRequest().setAttribute("localDocumentPages", localDocumentPages);
      
      JSONArray localDocumentPagesJs = new JSONArray();
      for (LocalDocumentPage page : localDocumentPages) {
        JSONObject pageJs = new JSONObject();
        pageJs.put("id", page.getId().toString());
        pageJs.put("title", page.getTitle());
        pageJs.put("content", page.getContent() != null ? page.getContent() : "");
        localDocumentPagesJs.add(pageJs);
      }
      
      setJsDataVariable(pageRequestContext, "localDocumentPages", localDocumentPagesJs.toString());
    }
  

    pageRequestContext.getRequest().setAttribute("parentFolder", delfoi.getRootFolder());
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("dashboardLang", pageRequestContext.getString("lang"));
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/editindexpagedocument.jsp");
  }

}