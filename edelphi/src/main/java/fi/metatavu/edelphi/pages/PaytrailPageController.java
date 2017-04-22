package fi.metatavu.edelphi.pages;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.dao.orders.OrderHistoryDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.orders.OrderHistory;
import fi.metatavu.edelphi.domainmodel.orders.OrderStatus;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.paytrail.PaytrailServiceFactory;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.paytrail.PaytrailService;

public class PaytrailPageController extends PageController {

  private static final String ORDER_NUMBER = "ORDER_NUMBER";
  private Logger logger = Logger.getLogger(PaytrailPageController.class.getName());

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String action = pageRequestContext.getString("action");
    
    if (StringUtils.isNotBlank(action)) {
      switch (action) {
        case "success":
        case "notify":
          handleSuccess(pageRequestContext);
        break;
        case "failure":
          handleFailure(pageRequestContext);
        break;
        default:
        break;
      }
    } else {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale()); 
    }
  }

  private void handleFailure(PageRequestContext pageRequestContext) {
    String orderNumber = pageRequestContext.getString(ORDER_NUMBER);
    Long orderHistoryId = NumberUtils.createLong(orderNumber);
    OrderHistoryDAO orderHistoryDAO = new OrderHistoryDAO();
    
    OrderHistory orderHistory = orderHistoryDAO.findById(orderHistoryId);
    if (orderHistory != null) {
      orderHistoryDAO.updateStatus(orderHistory, OrderStatus.CANCELED);
      pageRequestContext.setRedirectURL("/profile.page");
    } else {
      logger.log(Level.WARNING, () -> String.format("Could not find order history by id %s", orderHistoryId));
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
  }

  private void handleSuccess(PageRequestContext pageRequestContext) {
    if (validate(pageRequestContext)) {
      OrderHistoryDAO orderHistoryDAO = new OrderHistoryDAO();
      
      String orderNumber = pageRequestContext.getString(ORDER_NUMBER);
      Long orderHistoryId = NumberUtils.createLong(orderNumber);
      OrderHistory orderHistory = orderHistoryDAO.findById(orderHistoryId);

      if (orderHistory != null) {
        if (orderHistory.getStatus() != OrderStatus.WAITING_PAYMENT) {
          logger.log(Level.WARNING, () -> String.format("Tried to use old payment link for order %s", orderHistoryId));
          throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
        }
        
        pageRequestContext.setRedirectURL("/profile.page");
        updateOrder(orderHistory);
      } else {
        logger.log(Level.WARNING, () -> String.format("Could not find order history by id %s", orderHistoryId));
        throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
      }
    } else {
      logger.log(Level.WARNING, "Invalid Paytrail request");
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
  }

  private void updateOrder(OrderHistory orderHistory) {
    OrderHistoryDAO orderHistoryDAO = new OrderHistoryDAO();
    UserDAO userDAO = new UserDAO();

    User user = orderHistory.getUser();
    
    if (!user.getSubscriptionLevel().equals(orderHistory.getSubscriptionLevel())) {
      OffsetDateTime started = OffsetDateTime.now();
      OffsetDateTime ends = OffsetDateTime.now().plusDays(orderHistory.getDays());
      userDAO.updateSubscriptionLevel(user, orderHistory.getSubscriptionLevel());
      userDAO.updateSubscriptionStarted(user, Date.from(started.toInstant()));
      userDAO.updateSubscriptionEnds(user, Date.from(ends.toInstant()));
    } else {
      OffsetDateTime currentEnd = OffsetDateTime.ofInstant(user.getSubscriptionEnds().toInstant(), ZoneId.systemDefault());
      OffsetDateTime now = OffsetDateTime.now();
      OffsetDateTime ends = currentEnd.isAfter(now) ? currentEnd.plusDays(orderHistory.getDays()) : now.plusDays(orderHistory.getDays());
      userDAO.updateSubscriptionEnds(user, Date.from(ends.toInstant()));
      userDAO.updatePlan(user, orderHistory.getPlan());
    }

    orderHistoryDAO.updateStatus(orderHistory, OrderStatus.PAID);
  }

  private boolean validate(PageRequestContext pageRequestContext) {
    String orderNumber = pageRequestContext.getString(ORDER_NUMBER);
    String timestamp = pageRequestContext.getString("TIMESTAMP");
    String paid = pageRequestContext.getString("PAID");
    String method = pageRequestContext.getString("METHOD");
    String authCode = pageRequestContext.getString("RETURN_AUTHCODE");
    PaytrailService paytrailService = PaytrailServiceFactory.createPaytrailService();
    return paytrailService.confirmPayment(orderNumber, timestamp, paid, method, authCode);
  }

}
