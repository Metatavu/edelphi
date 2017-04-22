package fi.metatavu.edelphi.dao.orders;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;

public class PlanDAO extends GenericDAO<Plan> {

  public Plan create(SubscriptionLevel subscriptionLevel, Double price, String currency, Integer days, LocalizedEntry name, LocalizedEntry description) {
    Plan plan = new Plan();
    plan.setCurrency(currency);
    plan.setDays(days);
    plan.setDescription(description);
    plan.setName(name);
    plan.setPrice(price);
    plan.setSubscriptionLevel(subscriptionLevel);
    return persist(plan);
  }
  
  public Plan updatePrice(Plan plan, Double price) {
    plan.setPrice(price);
    return persist(plan);
  }
  
  public Plan updateCurrency(Plan plan, String currency) {
    plan.setCurrency(currency);
    return persist(plan);
  }

  public Plan updateDays(Plan plan, Integer days) {
    plan.setDays(days);
    return persist(plan);
  }

  public Plan updateSubscriptionLevel(Plan plan, SubscriptionLevel subscriptionLevel) {
    plan.setSubscriptionLevel(subscriptionLevel);
    return persist(plan);
  }
  
}
