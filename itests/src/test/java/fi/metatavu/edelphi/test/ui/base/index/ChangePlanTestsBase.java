package fi.metatavu.edelphi.test.ui.base.index;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.metatavu.edelphi.test.mock.PlanMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class ChangePlanTestsBase extends AbstractUITest {
  
  private static final String CHANGEPLAN_PAGE = "/changeplan.page";
  
  private PlanMocker planMocker = new PlanMocker();
  
  @Before
  public void beforeTest() {
    planMocker.createPlan("Plus 100", "Plus plan for 100 days with 100 euros", 100, "PLUS", "EUR", 100d, true);
    planMocker.createPlan("Plus 200", "Plus plan for 200 days with 180 euros", 200, "PLUS", "EUR", 180d, true);
    planMocker.createPlan("Premium 100", "Premium plan for 100 days with 200 euros", 100, "PREMIUM", "EUR", 200d, true);
    planMocker.createPlan("Premium 200", "Premium plan for 200 days with 360 euros", 200, "PREMIUM", "EUR", 360d, true);
    planMocker.mock();
  }
  
  @After
  public void afterTest() {
   deleteOrderHistories();
    planMocker.cleanup();
  }
  
  @Test
  public void testBasicToPlus() {
    testPlanChange(null, "BASIC", "Basic", null, 1, 100, "PLUS", "Plus", 100, null, null);
    testPlanChange(null, "BASIC", "Basic", null, 2, 200, "PLUS", "Plus", 180, null, null);
  }

  @Test
  public void testBasicToPremium() {
    testPlanChange(null, "BASIC", "Basic", null, 3, 100, "PREMIUM", "Premium", 200, null, null);
    testPlanChange(null, "BASIC", "Basic", null, 4, 200, "PREMIUM", "Premium", 360, null, null);
  }
  
  @Test
  public void testPlusToPlus() {
    OffsetDateTime oldEnds = OffsetDateTime.now().plusDays(30);
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", oldEnds, 1, 100, "PLUS", "Plus", 100, null, 30);
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", oldEnds, 2, 200, "PLUS", "Plus", 180, null, 30);
  }
  
  @Test
  public void testPlusToPremiumNoCompensation() {
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", null, 3, 100, "PREMIUM", "Premium", 200, null, null);
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", null, 4, 200, "PREMIUM", "Premium", 360, null, null);
  }
  
  @Test
  public void testPlusToPremiumWithCompensation() {
    OffsetDateTime oldEnds = OffsetDateTime.now().plusDays(30);
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", oldEnds, 3, 100, "PREMIUM", "Premium", 200, 26.1d, null);
    testPlanChange(planMocker.getPlanId(1), "PLUS", "Plus", oldEnds, 4, 200, "PREMIUM", "Premium", 360, 26.1d, null);
  }
  
  @Test
  public void testPremiumToPremium() {
    OffsetDateTime oldEnds = OffsetDateTime.now().plusDays(30);
    testPlanChange(planMocker.getPlanId(3), "PREMIUM", "Premium", oldEnds, 3, 100, "PREMIUM", "Premium", 200, null, 30);
    testPlanChange(planMocker.getPlanId(3), "PREMIUM", "Premium", oldEnds, 4, 200, "PREMIUM", "Premium", 360, null, 30);
  }
  
  @Test
  public void testPremiumToPlus() {
    updateUserSubscription(1l, "PREMIUM", null, Date.from(OffsetDateTime.now().plusDays(30).toInstant()));
    updateUserPlan(1l, planMocker.getPlanId(3));
    
    login(ADMIN_EMAIL);
    navigate(CHANGEPLAN_PAGE);

    assertText("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(1) .planHint", "You may not change your order to lower level when subscription is active");
    assertInputDisabled("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(1) input");
    
    assertText("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(2) .planHint", "You may not change your order to lower level when subscription is active");
    assertInputDisabled("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(2) input");
    
    assertText("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(3) .planHint", "Remaining days (30) of your order will be added to your new order");
    assertInputEnabled("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(3) input");
    
    assertText("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(4) .planHint", "Remaining days (30) of your order will be added to your new order");
    assertInputEnabled("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(4) input");
  }

  private void testPlanChange(Long oldPlanId, String oldPlanType, String oldPlanName, OffsetDateTime fromEnds, int newPlanIndex, int newPlanDays, String newPlanType, String newPlanName, double newPlanPrice, Double compensation, Integer extraDays) {
    Date oldSubscriptionEnd = fromEnds != null ? Date.from(fromEnds.toInstant()) : null;
    OffsetDateTime newEndDate = OffsetDateTime.now().plusDays(newPlanDays);
    
    if (extraDays != null) {
      newEndDate = newEndDate.plusDays(extraDays);
    }
    
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    String newEndDateFormatted = dateFormat.format(Date.from(newEndDate.toInstant()));
    String oldEndDateFormatted = oldSubscriptionEnd != null ? dateFormat.format(oldSubscriptionEnd) : null;
    Double totalPrice = Math.max(newPlanPrice - (compensation != null ? compensation : 0d), 0);
    
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    currencyFormatter.setCurrency(Currency.getInstance("EUR"));
    String priceFormatted = currencyFormatter.format(newPlanPrice);
    String compensationFormatted = compensation != null ? currencyFormatter.format(compensation) : null;
    String totalFormatted = currencyFormatter.format(totalPrice);
    
    if (oldPlanId != null) {
      updateUserPlan(1l, oldPlanId);
    }
    
    updateUserSubscription(1l, oldPlanType, null, oldSubscriptionEnd);
    login(ADMIN_EMAIL);
   
    navigate(CHANGEPLAN_PAGE);
    waitPresent("#currentPlanGenericBlockContent");
    assertText("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(1) label", "SUBSCRIPTION LEVEL");
    assertText("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(1) span", String.format("Your current subscription level is %s", oldPlanName));
    
    if (oldSubscriptionEnd == null) {
      assertNotPresent("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(2)");
    } else {
      assertText("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(2) span", String.format("Your subscription ends %s", oldEndDateFormatted));
    }
    
    if (compensation != null) {
      String compensationText = String.format("Remaining time of your old order will be compensated in your new order. Your compensation will be %s", compensationFormatted);
      assertText(String.format("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(%d) .planHint", newPlanIndex), compensationText);
    }
    
    if (extraDays != null) {
      String extraDaysText = String.format("Remaining days (%d) of your order will be added to your new order", extraDays);
      assertText(String.format("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(%d) .planHint", newPlanIndex), extraDaysText);
    }
    
    waitPresent("#changePlanGenericBlockContent");
    assertCount("#changePlanGenericBlockContent .planFieldContainer", 4);
    waitAndClick(String.format("#changePlanGenericBlockContent .planFieldContainer:nth-of-type(%d) input", newPlanIndex));
    waitAndClick("input[name='change']");
    
    String summaryText = String.format("%s days of subscription level %s. New subscription will end at %s", newPlanDays, newPlanType, newEndDateFormatted);
    waitPresent("#orderPlanSummaryBlockContent");
    assertText("#orderPlanSummaryBlockContent .planDescription", summaryText);
    assertText("#orderPlanSummaryBlockContent .planPrice", priceFormatted);
    
    if (compensation == null) {
      assertNotPresent("#orderPlanSummaryBlockContent .compensation");
    } else {
      assertText("#orderPlanSummaryBlockContent .compensation", compensationFormatted);  
    }
    
    assertText("#orderPlanSummaryBlockContent .total", totalFormatted);
    
    waitAndType("input[name='mobileNumber']", "+358 12 345 6789");
    waitAndType("input[name='streetAddress']", "Tester's street 1");
    waitAndType("input[name='postalCode']", "12345");
    waitAndType("input[name='postalOffice']", "Test");
    waitAndClick("input[name='order']");

    acceptPaytrailPayment(totalPrice);
    
    waitPresent("#GUI_indexProfilePanel");
    
    navigate(CHANGEPLAN_PAGE);

    waitAndAssertText("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(1) span", String.format("Your current subscription level is %s", newPlanName));
    waitAndAssertText("#currentPlanGenericBlockContent .currentPlanRow:nth-of-type(2) span", String.format("Your subscription ends %s", newEndDateFormatted));
  }
  

}
