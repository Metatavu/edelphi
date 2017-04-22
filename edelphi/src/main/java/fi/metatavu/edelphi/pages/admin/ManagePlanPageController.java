package fi.metatavu.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.LocaleUtils;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.base.LocalizedEntryDAO;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ManagePlanPageController extends DelfoiPageController {

  public ManagePlanPageController() {
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
    LocalizedEntryDAO localizedEntryDAO = new LocalizedEntryDAO();

    Locale locale = pageRequestContext.getRequest().getLocale();
    Messages messages = Messages.getInstance();
    
    String[] supportedLocales = LocalizationUtils.getSupportedLocales();
    Plan plan = null;
    
    if (pageRequestContext.getBoolean("create")) {
      LocalizedEntry name = localizedEntryDAO.create();
      LocalizedEntry description = localizedEntryDAO.create();
      
      for (String supportedLocale : supportedLocales) {
        LocalizationUtils.updateText(name, LocaleUtils.toLocale(supportedLocale), messages.getText(locale, "admin.managePlan.name", new Object[] { supportedLocale }));
        LocalizationUtils.updateText(description, LocaleUtils.toLocale(supportedLocale), messages.getText(locale, "admin.managePlan.description", new Object[] { supportedLocale }));
      }
      
      plan = planDAO.create(SubscriptionLevel.PLUS, 0d, "EUR", 365, name, description);
    } else {
      Long planId = pageRequestContext.getLong("planId");
      if (planId != null) {
        plan = planDAO.findById(planId);
      }
    }

    if (plan == null) {
      throw new PageNotFoundException(locale);
    }
    
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    List<String> subscriptionLevels = new ArrayList<>();
    subscriptionLevels.add(SubscriptionLevel.PLUS.name());
    subscriptionLevels.add(SubscriptionLevel.PREMIUM.name());

    Map<String, String> nameLocales = new HashMap<>();
    Map<String, String> descriptionLocales = new HashMap<>();
    Map<String, String> nameValues = new HashMap<>();
    Map<String, String> descriptionValues = new HashMap<>();
    Map<String, String> subscriptionLevelTitles = new HashMap<>();
    
    for (String supportedLocale : supportedLocales) {
      nameLocales.put(supportedLocale, messages.getText(locale, "admin.managePlan.name", new Object[] { supportedLocale }));
      descriptionLocales.put(supportedLocale, messages.getText(locale, "admin.managePlan.description", new Object[] { supportedLocale }));
      nameValues.put(supportedLocale, LocalizationUtils.getLocalizedText(plan.getName(), LocaleUtils.toLocale(supportedLocale)));
      descriptionValues.put(supportedLocale, LocalizationUtils.getLocalizedText(plan.getDescription(), LocaleUtils.toLocale(supportedLocale)));
    }
    
    for (String subscriptionLevel : subscriptionLevels) {
      subscriptionLevelTitles.put(subscriptionLevel, messages.getText(locale, String.format("generic.subscriptionLevels.%s", subscriptionLevel)));
    }
    
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("subscriptionLevels", subscriptionLevels.toArray(new String[0]));
    pageRequestContext.getRequest().setAttribute("locales", supportedLocales);
    pageRequestContext.getRequest().setAttribute("nameLocales", nameLocales);
    pageRequestContext.getRequest().setAttribute("descriptionLocales", descriptionLocales);;
    pageRequestContext.getRequest().setAttribute("nameValues", nameValues);
    pageRequestContext.getRequest().setAttribute("descriptionValues", descriptionValues);
    pageRequestContext.getRequest().setAttribute("subscriptionLevelTitles", subscriptionLevelTitles);
    pageRequestContext.getRequest().setAttribute("plan", plan);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/manageplan.jsp");
  }

}