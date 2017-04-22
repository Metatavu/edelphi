package fi.metatavu.edelphi.pages.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;

public class ManagePlansPageController extends DelfoiPageController {

  public ManagePlansPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PLANS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PlanDAO planDAO = new PlanDAO();
    
    Locale locale = pageRequestContext.getRequest().getLocale();
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    List<Plan> plans = planDAO.listAll();
    Map<Long, String> planNames = new HashMap<>(plans.size());
    for (Plan plan : plans) {
      planNames.put(plan.getId(), LocalizationUtils.getLocalizedText(plan.getName(), locale));
    }
    
    pageRequestContext.getRequest().setAttribute("plans", plans);
    pageRequestContext.getRequest().setAttribute("planNames", planNames);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/manageplans.jsp");
  }

}