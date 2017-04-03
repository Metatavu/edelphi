package fi.metatavu.edelphi.jsons.admin;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public class ManageSubscriptionLevelsJSONRequestController extends JSONController {

  public ManageSubscriptionLevelsJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_SUBSCRIPTION_LEVELS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    for (SubscriptionLevel subscriptionLevel : SubscriptionLevel.values()) {
      for (Feature feature : Feature.values()) {
        Boolean enabled = jsonRequestContext.getBoolean(String.format("%s.%s", subscriptionLevel, feature));
        SubscriptionLevelUtils.setFeatureEnabled(subscriptionLevel, feature, enabled);
      }
    }
    
  }

}
