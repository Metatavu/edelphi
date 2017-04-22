package fi.metatavu.edelphi.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.orders.OrderHistoryDAO;
import fi.metatavu.edelphi.dao.orders.PlanDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.OrderHistory;
import fi.metatavu.edelphi.domainmodel.orders.OrderStatus;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.paytrail.PaytrailCommunicationError;
import fi.metatavu.edelphi.paytrail.PaytrailServiceFactory;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.Country;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SubscriptionCompareResult;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;
import fi.metatavu.edelphi.utils.SystemUtils;
import fi.metatavu.paytrail.PaytrailException;
import fi.metatavu.paytrail.PaytrailService;
import fi.metatavu.paytrail.rest.Address;
import fi.metatavu.paytrail.rest.Contact;
import fi.metatavu.paytrail.rest.OrderDetails;
import fi.metatavu.paytrail.rest.Payment;
import fi.metatavu.paytrail.rest.Product;
import fi.metatavu.paytrail.rest.Result;
import fi.metatavu.paytrail.rest.UrlSet;

public class OrderPlanPageController extends PageController {

  private static final Double DEFAULT_VAT_PERCENT = 24d;
  private static Logger logger = Logger.getLogger(OrderPlanPageController.class.getName());

  public OrderPlanPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    if (StringUtils.equalsIgnoreCase("POST", pageRequestContext.getRequest().getMethod())) {
      doPost(pageRequestContext);
    } else {
      doGet(pageRequestContext);
    }
  }

  private void doPost(PageRequestContext pageRequestContext) {
    PlanDAO planDAO = new PlanDAO();
    OrderHistoryDAO orderHistoryDAO = new OrderHistoryDAO();
    UserDAO userDAO = new UserDAO();
    
    Locale locale = pageRequestContext.getRequest().getLocale();

    String firstName = pageRequestContext.getString("firstName");
    String lastName = pageRequestContext.getString("lastName");
    String email = pageRequestContext.getString("email");
    String mobileNumber = pageRequestContext.getString("mobileNumber");
    String telephoneNumber = pageRequestContext.getString("telephoneNumber");
    String streetAddress = pageRequestContext.getString("streetAddress");
    String postalCode = pageRequestContext.getString("postalCode");
    String postalOffice = pageRequestContext.getString("postalOffice");
    String country = pageRequestContext.getString("country");
    String company = pageRequestContext.getString("company");
    Double discount = 0d;
    Plan plan = null;
    Long loggedUserId = pageRequestContext.getLoggedUserId();
    
    Long planId = pageRequestContext.getLong("planId");
    if (planId != null) {
      plan = planDAO.findById(planId);
    }
    
    if (plan == null) {
      throw new PageNotFoundException(locale);
    }
    
    String name = LocalizationUtils.getLocalizedText(plan.getName(), locale);
    User user = userDAO.findById(loggedUserId);
    OrderHistory orderHistory = orderHistoryDAO.create(user, plan, plan.getSubscriptionLevel(), OrderStatus.WAITING_PAYMENT, "EUR", plan.getPrice(), name, plan.getDays());
    String orderNumber = orderHistory.getId().toString();
    String baseUrl = RequestUtils.getBaseUrl(pageRequestContext.getRequest());

    UrlSet urlSet = new UrlSet(
      String.format("%s/paytrail.page?action=success", baseUrl), 
      String.format("%s/paytrail.page?action=failure", baseUrl), 
      String.format("%s/paytrail.page?action=notify", baseUrl), 
      String.format("%s/paytrail.page?action=pending", baseUrl)
    );
    
    Address address = new fi.metatavu.paytrail.rest.Address(streetAddress, postalCode, postalOffice, country);
    Contact contact = new Contact(firstName, lastName, email, address, telephoneNumber, mobileNumber, company);
    OrderDetails orderDetails = new OrderDetails(1, contact);
    
    Payment payment = new Payment(orderNumber, orderDetails, urlSet);
    
    PaytrailService paytrailService = PaytrailServiceFactory.createPaytrailService();
    
    try {
      paytrailService.addProduct(payment, name, 
          String.format("#%d", plan.getId()), 
          1d, 
          plan.getPrice(), 
          getVatPercent(), 
          discount, 
          Product.TYPE_NORMAL);
    } catch (PaytrailException e) {
      throw new PaytrailCommunicationError(locale, e);
    }
    
    try {
      Result result = paytrailService.processPayment(payment);
      if (result != null) {
        pageRequestContext.setRedirectURL(result.getUrl());
      }
    } catch (PaytrailException e) {
      throw new PaytrailCommunicationError(locale, e);
    }
  }

  private void doGet(PageRequestContext pageRequestContext) {

    PlanDAO planDAO = new PlanDAO();
    
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    Plan newPlan = null;
    
    Long planId = pageRequestContext.getLong("planId");
    if (planId != null) {
      newPlan = planDAO.findById(planId);
    }
    
    if (newPlan == null) {
      throw new PageNotFoundException(locale);
    }
    
    if (SubscriptionLevelUtils.comparePlans(loggedUser.getPlan(), newPlan) == SubscriptionCompareResult.LOWER) {
      logger.log(Level.SEVERE, "Tried to order lower plan that existing plan");
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Double compensation = null;
    
    Plan oldPlan = loggedUser.getPlan();
    if (oldPlan != null) {
      compensation = SubscriptionLevelUtils.calculateCompensation(oldPlan, newPlan, loggedUser.getSubscriptionEnds());        
    }
    
    double totalPrice = newPlan.getPrice();
    if (compensation != null) {
      totalPrice =- compensation;
    }
    
    Date subscriptionEnds = SubscriptionLevelUtils.getNewSubscriptionEnd(loggedUser.getSubscriptionEnds(), oldPlan, newPlan); 
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.getRequest().setAttribute("compensation", compensation);
    pageRequestContext.getRequest().setAttribute("subscriptionEnds", subscriptionEnds);
    pageRequestContext.getRequest().setAttribute("totalPrice", totalPrice);
    
    pageRequestContext.getRequest().setAttribute("planName", LocalizationUtils.getLocalizedText(newPlan.getName(), locale));
    pageRequestContext.getRequest().setAttribute("planDescription", LocalizationUtils.getLocalizedText(newPlan.getDescription(), locale));
    pageRequestContext.getRequest().setAttribute("loggedUser", loggedUser);
    pageRequestContext.getRequest().setAttribute("oldPlan", oldPlan);
    pageRequestContext.getRequest().setAttribute("newPlan", newPlan);
    pageRequestContext.getRequest().setAttribute("countryCodes", getCountryCodes(Country.values()));
    pageRequestContext.getRequest().setAttribute("countries", Country.values());
    
    pageRequestContext.setIncludeJSP("/jsp/pages/orderplan.jsp");
  }
  
  private Double getVatPercent() {
    String value = SystemUtils.getSettingValue("paytrail.defaultVat");
    if (StringUtils.isNumeric(value)) {
      return NumberUtils.createDouble(value);
    }
    
    return DEFAULT_VAT_PERCENT;
  }

  private String getCountryCodes(Country[] countries) {
    List<String> result = new ArrayList<>(countries.length);
    
    for (Country country : countries) {
      result.add(country.name());
    }
    
    return StringUtils.join(result, ",");
  }

}
