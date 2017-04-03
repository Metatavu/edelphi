package fi.metatavu.edelphi.utils;

import java.util.EnumMap;
import java.util.Map;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;

public class SubscriptionLevelUtils {

  private static final Map<SubscriptionLevel, SubscriptionLevelSettings> DEFAULTS;
  
  private SubscriptionLevelUtils() {
  }
  
  public static SubscriptionLevelSettings getSubscriptionLevelSettings(SubscriptionLevel subscriptionLevel) {
    return DEFAULTS.get(subscriptionLevel);
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
