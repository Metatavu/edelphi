package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.metatavu.edelphi.utils.RequestUtils;

public class CreatePanelExpertIntressJSONRequestController extends JSONController {

  public CreatePanelExpertIntressJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    String name = jsonRequestContext.getString("newIntressName");
    PanelUserIntressClass panelUserIntressClass = panelUserIntressClassDAO.create(panel, name);

    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);

    // Create groups
    Map<String, Long> expertiseClassMap = new HashMap<String, Long>();
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      PanelUserExpertiseGroup panelUserExpertiseGroup = panelUserExpertiseGroupDAO.create(panel, expertiseClass, panelUserIntressClass, null, panel.getCurrentStamp());
      expertiseClassMap.put(expertiseClass.getId().toString(), panelUserExpertiseGroup.getId());
    }
    
    jsonRequestContext.addResponseParameter("id", panelUserIntressClass.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelUserIntressClass.getName());
    jsonRequestContext.addResponseParameter("newExpertiseGroups", expertiseClassMap);

    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(panel.getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      ExpertiseQueryPageHandler pageHandler = (ExpertiseQueryPageHandler) QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.EXPERTISE);
      pageHandler.synchronizedFields(expertisePage);
    }
  
  }
  
}
