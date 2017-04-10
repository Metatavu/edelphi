package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserRole;
import fi.metatavu.edelphi.utils.LocalizationUtils;

public class CreateUserPageController extends PanelPageController {

  public CreateUserPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();
    
    List<PanelUserRole> panelUserRoles = panelUserRoleDAO.listAll();
    List<UserRoleBean> userRoleBeans = new ArrayList<>();
    for (PanelUserRole panelUserRole : panelUserRoles) {
      String name = LocalizationUtils.getLocalizedText(panelUserRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(panelUserRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("panelUserRoles", userRoleBeans);
    
    pageRequestContext.setIncludeJSP("/jsp/panels/createuser.jsp");
  }

  public class UserRoleBean {
    
    private Long id;
    private String name;

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
    
  }

}
