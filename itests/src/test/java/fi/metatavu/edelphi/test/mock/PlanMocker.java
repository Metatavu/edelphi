package fi.metatavu.edelphi.test.mock;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

public class PlanMocker extends AbstractMocker {

  private List<Long> planIds = new ArrayList<>();
    
  public PlanMocker mock() {
    return this;
  }
  
  public PlanMocker cleanup() {
    for (Long planId : planIds) {
      deletePlan(planId);
    }
    
    super.cleanup();
    
    return this;
  }

  public Long getPlanId(int index) {
    Long result = planIds.get(index);
    assertNotNull(result);
    return result;
  }
  
  public long createPlan(String name, String description, Integer days, String subscriptionLevel, String currency, Double price, Boolean visible) {
    Long nameId = createLocalizedEntry();
    Long descriptionId = createLocalizedEntry();
    Long id = getNextId("Plan");
    
    createLocalizedValue(name, "en", nameId);
    createLocalizedValue(description, "en", descriptionId);
    
    String sql = 
        "INSERT INTO " + 
        "  Plan (id, name_id, description_id, days, subscriptionLevel, currency, price, visible) " + 
        "VALUES " + 
        "  (?, ?, ?, ?, ?, ?, ?, ?)";
      
    executeSql(sql, id, nameId, descriptionId, days, subscriptionLevel, currency, price, visible);
    
    planIds.add(id);
    
    return id;
  }

  private void deletePlan(Long planId) {
    executeSql("UPDATE User SET plan_id = null WHERE plan_id = ?", planId);
    executeSql("DELETE FROM Plan WHERE id = ?", planId);
  }
}
