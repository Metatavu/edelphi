package fi.metatavu.edelphi.pages.panel.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.MaterialBean;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class DashboardPageController extends PanelPageController {

  public DashboardPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    // TODO: Move query and documents counts to settings
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    while (queries.size() > 5) {
      queries.remove(queries.size() - 1);
    }
    Long queryCount = queryDAO.countByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);

    // TODO: Include Reports into materials list
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queries", queries);
    pageRequestContext.getRequest().setAttribute("queryCount", queryCount);
    try {
      List<MaterialBean> materials = MaterialUtils.listPanelMaterials(panel, true);
      Collections.sort(materials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      while (materials.size() > 5) {
        materials.remove(materials.size() - 1);
      }
      pageRequestContext.getRequest().setAttribute("materials", materials);
      pageRequestContext.getRequest().setAttribute("materialCount", MaterialUtils.countPanelMaterials(panel, false));
    } catch (IOException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
 
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/dashboard.jsp");
  }

}