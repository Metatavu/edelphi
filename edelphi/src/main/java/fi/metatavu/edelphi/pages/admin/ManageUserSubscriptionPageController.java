package fi.metatavu.edelphi.pages.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;

public class ManageUserSubscriptionPageController extends DelfoiPageController {

  public ManageUserSubscriptionPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_SUBSCRIPTIONS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Locale locale = pageRequestContext.getRequest().getLocale();

    PlanDAO planDAO = new PlanDAO();
    UserDAO userDAO = new UserDAO();
    
    Long userId = pageRequestContext.getLong("user-id");
    if (userId == null) {
      throw new PageNotFoundException(locale);
    }
    
    User user = userDAO.findById(userId);
    if (user == null) {
      throw new PageNotFoundException(locale);
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    List<Plan> plans = planDAO.listAll();
    Map<Long, String> planNames = new HashMap<>(plans.size());
    for (Plan plan : plans) {
      planNames.put(plan.getId(), LocalizationUtils.getLocalizedText(plan.getName(), locale));
    }

    pageRequestContext.getRequest().setAttribute("user", user);
    pageRequestContext.getRequest().setAttribute("plans", plans);
    pageRequestContext.getRequest().setAttribute("planNames", planNames);
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/manageusersubscription.jsp");
  }

}