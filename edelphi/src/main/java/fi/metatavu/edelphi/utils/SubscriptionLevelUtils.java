package fi.metatavu.edelphi.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.features.SubscriptionLevelFeatureDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.features.SubscriptionLevelFeature;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;

public class SubscriptionLevelUtils {

  private static Logger logger = Logger.getLogger(SubscriptionLevelUtils.class.getName());
  private static final Map<SubscriptionLevel, SubscriptionLevelSettings> DEFAULTS;
  
  private SubscriptionLevelUtils() {
  }
  
  public static SubscriptionLevelSettings getSubscriptionLevelSettings(SubscriptionLevel subscriptionLevel) {
    return DEFAULTS.get(subscriptionLevel);
  }

  public static SubscriptionLevel getMinimumLevelFor(Feature feature) {
    for (SubscriptionLevel subscriptionLevel : SubscriptionLevel.values()) {
      if (isFeatureEnabled(subscriptionLevel, feature)) {
        return subscriptionLevel;
      }
    }
    
    return null;
  }

  public static long getDaysRemaining(Date subscriptionEnds) {
    if (subscriptionEnds != null) {
      OffsetDateTime oldEnd = OffsetDateTime.ofInstant(subscriptionEnds.toInstant(), ZoneId.systemDefault());
      return Math.max(ChronoUnit.DAYS.between(OffsetDateTime.now().minusHours(1), oldEnd), 0);
    }
    
    return 0;
  }

  public static Date getNewSubscriptionEnd(Date oldSubscriptionEnds, Plan oldPlan, Plan newPlan) {
    OffsetDateTime result = OffsetDateTime.now()
      .plusDays(newPlan.getDays());
    
    long daysRemaining = getDaysRemaining(oldSubscriptionEnds);
    if ((oldPlan != null && oldPlan.getSubscriptionLevel() != SubscriptionLevel.BASIC) && (comparePlans(oldPlan, newPlan) == SubscriptionCompareResult.EQUAL) && daysRemaining > 0) {
      result = result.plusDays(daysRemaining);
    }
    
    return Date.from(result.toInstant());
  }
  
  public static Double calculateCompensation(Plan oldPlan, Plan newPlan, Date subscriptionEnds) {
    if (comparePlans(newPlan, oldPlan) != SubscriptionCompareResult.HIGHER) {
      return null;
    }
    
    if (oldPlan == null || newPlan == null) {
      return null;
    }
    
    if (subscriptionEnds == null) {
      return null;
    }
    
    if (!StringUtils.equals(oldPlan.getCurrency(), newPlan.getCurrency())) {
      logger.log(Level.SEVERE, () -> String.format("Could not calculate compensation because plans have different currencies (%s, %s)", oldPlan.getCurrency(), newPlan.getCurrency()));
      return null;
    }

    double dailyPrice = oldPlan.getPrice() / oldPlan.getDays();
    OffsetDateTime oldEnd = OffsetDateTime.ofInstant(subscriptionEnds.toInstant(), ZoneId.systemDefault());
    double daysLeft = ChronoUnit.DAYS.between(OffsetDateTime.now(), oldEnd);
    
    return Math.min(Math.max(dailyPrice * daysLeft, 0), newPlan.getPrice());
  }
  
  public static SubscriptionCompareResult comparePlans(Plan plan1, Plan plan2) {
    SubscriptionLevel subscriptionLevel1 = plan1 != null ? plan1.getSubscriptionLevel() : SubscriptionLevel.BASIC;
    SubscriptionLevel subscriptionLevel2 = plan2 != null ? plan2.getSubscriptionLevel() : SubscriptionLevel.BASIC;
    
    return compareSubscriptionLevels(subscriptionLevel1, subscriptionLevel2);
  }
  
  public static SubscriptionCompareResult compareSubscriptionLevels(SubscriptionLevel subscriptionLevel1, SubscriptionLevel subscriptionLevel2) {
    if (subscriptionLevel1.ordinal() == subscriptionLevel2.ordinal()) {
      return SubscriptionCompareResult.EQUAL;
    }
    
    return subscriptionLevel1.ordinal() < subscriptionLevel2.ordinal() ? SubscriptionCompareResult.LOWER : SubscriptionCompareResult.HIGHER;    
  }

  public static boolean isFeatureEnabled(SubscriptionLevel subscriptionLevel, Feature feature) {
    SubscriptionLevelFeatureDAO subscriptionLevelFeatureDAO = new SubscriptionLevelFeatureDAO();
    return subscriptionLevelFeatureDAO.findBySubscriptionLevelAndFeature(subscriptionLevel, feature) != null;
  }
  
  public static void setFeatureEnabled(SubscriptionLevel subscriptionLevel, Feature feature, boolean enabled) {
    SubscriptionLevelFeatureDAO subscriptionLevelFeatureDAO = new SubscriptionLevelFeatureDAO();
    SubscriptionLevelFeature subscriptionLevelFeature = subscriptionLevelFeatureDAO.findBySubscriptionLevelAndFeature(subscriptionLevel, feature);
    if (subscriptionLevelFeature != null) {
      if (!enabled) {
        subscriptionLevelFeatureDAO.delete(subscriptionLevelFeature);
      }
    } else {
      if (enabled) {
        subscriptionLevelFeatureDAO.create(subscriptionLevel, feature);
      }
    }
  }
  
  public static Map<SubscriptionLevel, Map<Feature, Boolean>> getSubscriptionLevelFeatureMap() {
    EnumMap<SubscriptionLevel, Map<Feature, Boolean>> result = new EnumMap<>(SubscriptionLevel.class);
    
    for (SubscriptionLevel subscriptionLevel : SubscriptionLevel.values()) {
      EnumMap<Feature, Boolean> featureMap = new EnumMap<>(Feature.class);
      
      for (Feature feature : Feature.values()) {
        featureMap.put(feature, isFeatureEnabled(subscriptionLevel, feature));
      }
      
      result.put(subscriptionLevel, featureMap);
    }
    
    return result;
  }
  
  public static Long countManagedActivePanels(User user) {
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();

    DelfoiAction delfoiAction = delfoiActionDAO.findByActionName(DelfoiActionName.MANAGE_PANEL.toString());
    
    return panelUserDAO.countByPanelStateUserAndRole(user, delfoiAction, PanelState.IN_PROGRESS);
  }
  
  public static class SubscriptionLevelSettings {
  
    private SubscriptionLevel level;
    private boolean allowClosed;
    private int panels;
    private int managers;
    private int panelists;
    
    public SubscriptionLevelSettings(SubscriptionLevel level, boolean allowClosed, int panels, int managers, int panelists) {
      super();
      this.level = level;
      this.allowClosed = allowClosed;
      this.panels = panels;
      this.managers = managers;
      this.panelists = panelists;
    }
    
    public SubscriptionLevel getLevel() {
      return level;
    }
    
    public boolean getAllowClosed() {
      return allowClosed;
    }

    public int getManagers() {
      return managers;
    }
    
    public int getPanelists() {
      return panelists;
    }
    
    public int getPanels() {
      return panels;
    }
    
  }
  
  static {
    DEFAULTS = new EnumMap<>(SubscriptionLevel.class);
    DEFAULTS.put(SubscriptionLevel.BASIC, new SubscriptionLevelSettings(SubscriptionLevel.BASIC, true, 1, Integer.MAX_VALUE, Integer.MAX_VALUE));
    DEFAULTS.put(SubscriptionLevel.PLUS, new SubscriptionLevelSettings(SubscriptionLevel.PLUS, true, 5, Integer.MAX_VALUE, Integer.MAX_VALUE));
    DEFAULTS.put(SubscriptionLevel.PREMIUM, new SubscriptionLevelSettings(SubscriptionLevel.PREMIUM, true, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
  }
  
}
