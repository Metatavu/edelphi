package fi.metatavu.edelphi.pages.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.MaterialBean;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManageMaterialsPageController extends DelfoiPageController {

  public ManageMaterialsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    Folder folder = null;
    String category = pageRequestContext.getString("cat");
    String language = pageRequestContext.getString("lang");
    if (StringUtils.isEmpty(language))
      language = locale.getLanguage();
    
    try {
      if ("help".equals(category)) {
        folder = MaterialUtils.getDelfoiHelpFolder(delfoi, language, loggedUser);
      } else {
        if ("materials".equals(category))
          folder = MaterialUtils.getDelfoiMaterialFolder(delfoi, language, loggedUser);
      }
    } catch (Exception ex) {
    }
    
    if (folder == null)
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    
    try {
      pageRequestContext.getRequest().setAttribute("parentFolder", folder);
      pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
      try {
        List<MaterialBean> materials = MaterialUtils.listFolderMaterials(folder, true, true);
        Collections.sort(materials, new Comparator<MaterialBean>() {
          @Override
          public int compare(MaterialBean o1, MaterialBean o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
          }
        });
        pageRequestContext.getRequest().setAttribute("materials", materials);
        pageRequestContext.getRequest().setAttribute("materialTrees", MaterialUtils.listMaterialTrees(folder, true, true));
      } catch (IOException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
      }
      
      pageRequestContext.getRequest().setAttribute("dashboardLang", pageRequestContext.getString("lang"));
      pageRequestContext.getRequest().setAttribute("dashboardCategory", category);
      pageRequestContext.setIncludeJSP("/jsp/pages/admin/managematerials.jsp");
    }
    catch (Exception e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
  }

}