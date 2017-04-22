package fi.metatavu.edelphi.jsons.admin;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManagePlanJSONRequestController extends JSONController {

  public ManagePlanJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PLANS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    PlanDAO planDAO = new PlanDAO();
    
    Locale locale = jsonRequestContext.getRequest().getLocale();
    String[] supportedLocales = LocalizationUtils.getSupportedLocales();

    Plan plan = null;
    
    Long planId = jsonRequestContext.getLong("planId");
    if (planId != null) {
      plan = planDAO.findById(planId);
    }
    
    if (plan == null) {
      throw new PageNotFoundException(locale);
    }
    
    for (String supportedLocale : supportedLocales) {
      String name = jsonRequestContext.getString(String.format("name-%s", supportedLocale));
      String description = jsonRequestContext.getString(String.format("description-%s", supportedLocale));
      LocalizationUtils.updateText(plan.getName(), LocaleUtils.toLocale(supportedLocale), name);
      LocalizationUtils.updateText(plan.getDescription(), LocaleUtils.toLocale(supportedLocale), description);
    }

    planDAO.updateDays(plan, jsonRequestContext.getInteger("days"));
    planDAO.updateSubscriptionLevel(plan, (SubscriptionLevel) jsonRequestContext.getEnum("subscriptionLevel", SubscriptionLevel.class));
    planDAO.updatePrice(plan, jsonRequestContext.getDouble("price"));
  }

}
