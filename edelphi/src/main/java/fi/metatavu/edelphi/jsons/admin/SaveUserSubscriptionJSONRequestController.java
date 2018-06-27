package fi.metatavu.edelphi.jsons.admin;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;

public class SaveUserSubscriptionJSONRequestController extends JSONController {

  public SaveUserSubscriptionJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_SUBSCRIPTIONS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    PlanDAO planDAO = new PlanDAO();

    Locale locale = jsonRequestContext.getRequest().getLocale();
    String planParam = jsonRequestContext.getString("plan");
    Long userId = jsonRequestContext.getLong("user-id");
    Date subscriptionStarted = jsonRequestContext.getDate("subscription-started");
    Date subscriptionEnds = jsonRequestContext.getDate("subscription-ends");
    
    User user = userDAO.findById(userId);
    if (user == null) {
      throw new PageNotFoundException(locale);
    }
    
    if ("CURRENT".equals(planParam)) {
      // Keeping current plan
    } else if ("BASIC".equals(planParam)) {
      userDAO.updateSubscriptionLevel(user, SubscriptionLevel.BASIC);
      userDAO.updateSubscriptionEnds(user, null);
      userDAO.updateSubscriptionStarted(user, null);
      userDAO.updatePlan(user, null);            
    } else {
      Long planId = NumberUtils.createLong(planParam);
      Plan plan = planDAO.findById(planId);
      if (plan == null) {
        throw new PageNotFoundException(locale);
      }

      SubscriptionLevel subscriptionLevel = plan.getSubscriptionLevel();
      userDAO.updateSubscriptionLevel(user, subscriptionLevel);
      userDAO.updateSubscriptionEnds(user, subscriptionEnds);
      userDAO.updateSubscriptionStarted(user, subscriptionStarted);
      userDAO.updatePlan(user, plan);      
    }
    
    Messages messages = Messages.getInstance();
    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "admin.managePanelBulletins.bulletinUpdated"));
  }

}
