package fi.metatavu.edelphi.pages;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SubscriptionCompareResult;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public class ChangePlanPageController extends PageController {

  public ChangePlanPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    PlanDAO planDAO = new PlanDAO();
    
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    List<Plan> plans = planDAO.listAll();
    Map<Long, String> planNames = new HashMap<>();
    Map<Long, String> planDescriptions = new HashMap<>();
    Map<Long, SubscriptionCompareResult> planCompareResults = new HashMap<>();
    Map<Long, String> planCompensiotions = new HashMap<>();
    long planDaysRemaining = SubscriptionLevelUtils.getDaysRemaining(loggedUser.getSubscriptionEnds());
    
    for (Plan plan : plans) {
      Long planId = plan.getId();
      Plan oldPlan = loggedUser.getPlan();
      Date subscriptionEnds = loggedUser.getSubscriptionEnds();
      
      SubscriptionCompareResult compareResult = SubscriptionLevelUtils.comparePlans(plan, loggedUser.getPlan());
      
      planNames.put(planId, LocalizationUtils.getLocalizedText(plan.getName(), locale));
      planDescriptions.put(planId, LocalizationUtils.getLocalizedText(plan.getDescription(), locale));
      planCompareResults.put(planId, compareResult);
      
      if (compareResult == SubscriptionCompareResult.HIGHER) {
        Double compensation = SubscriptionLevelUtils.calculateCompensation(oldPlan, plan, subscriptionEnds);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(Currency.getInstance(plan.getCurrency()));
        planCompensiotions.put(planId, formatter.format(compensation));
      }
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("plans", plans);
    pageRequestContext.getRequest().setAttribute("loggedUser", loggedUser);
    pageRequestContext.getRequest().setAttribute("planNames", planNames);
    pageRequestContext.getRequest().setAttribute("planLevels", planCompareResults);
    pageRequestContext.getRequest().setAttribute("planDescriptions", planDescriptions);
    pageRequestContext.getRequest().setAttribute("planDaysRemaining", planDaysRemaining);
    pageRequestContext.getRequest().setAttribute("planCompensiotions", planCompensiotions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/changeplan.jsp");
  }

}
