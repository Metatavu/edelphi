package fi.metatavu.edelphi.pages.admin;

import java.util.Map;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.pages.DelfoiPageController;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public class ManageSubscriptionLevelsPageController extends DelfoiPageController {

  public ManageSubscriptionLevelsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_SUBSCRIPTION_LEVELS, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    Map<SubscriptionLevel, Map<Feature, Boolean>> subscriptionLevelFeatureMap = SubscriptionLevelUtils.getSubscriptionLevelFeatureMap();

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("subscriptionLevelFeatureMap", subscriptionLevelFeatureMap);
    pageRequestContext.getRequest().setAttribute("features", Feature.values());
    pageRequestContext.getRequest().setAttribute("subscriptionLevels", SubscriptionLevel.values());
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/managesubscriptionlevels.jsp");
  }

}