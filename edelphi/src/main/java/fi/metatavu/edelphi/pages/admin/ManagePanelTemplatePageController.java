package fi.metatavu.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.metatavu.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.metatavu.edelphi.dao.users.UserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.metatavu.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.utils.LocalizationUtils;

public class ManagePanelTemplatePageController extends PageController {

  public ManagePanelTemplatePageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelSettingsTemplateDAO panelSettingsTemplateDAO = new PanelSettingsTemplateDAO();
    PanelSettingsTemplateRoleDAO panelSettingsTemplateRoleDAO = new PanelSettingsTemplateRoleDAO();
    UserRoleDAO userRoleDAO = new UserRoleDAO();

    Long templateId = pageRequestContext.getLong("templateId");
    PanelSettingsTemplate template = panelSettingsTemplateDAO.findById(templateId);
    
    // Populate role list
    List<UserRole> roleList = userRoleDAO.listAll();
    
    List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.PANEL);
    List<PanelSettingsTemplateRole> rolesByTemplate = panelSettingsTemplateRoleDAO.listByTemplate(template);
    
    Map<Long, Map<Long, Boolean>> roleMapForActions = new HashMap<Long, Map<Long, Boolean>>();

    for (UserRole role : roleList) {
      roleMapForActions.put(role.getId(), new HashMap<Long, Boolean>());
    }

    for (PanelSettingsTemplateRole templateRole : rolesByTemplate) {
      Map<Long, Boolean> actionsEnabledForRole = roleMapForActions.get(templateRole.getUserRole().getId());
      
      actionsEnabledForRole.put(templateRole.getDelfoiAction().getId(), Boolean.TRUE);
    }
    
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (UserRole userRole : roleList) {
      String name = LocalizationUtils.getLocalizedText(userRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(userRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("roleList", userRoleBeans);
    pageRequestContext.getRequest().setAttribute("template", template);
    pageRequestContext.getRequest().setAttribute("actionList", actionList);
    pageRequestContext.getRequest().setAttribute("actionStatus", roleMapForActions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/managepaneltemplate.jsp");
  }

  public class UserRoleBean {
    
    public UserRoleBean(Long id, String name) {
      this.id = id;
      this.name = name;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getName() {
      return name;
    }
    
    private Long id;
    private String name;
  }

}