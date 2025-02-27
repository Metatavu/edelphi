package fi.metatavu.edelphi.dao.orders;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.orders.OrderHistory;
import fi.metatavu.edelphi.domainmodel.orders.OrderHistory_;
import fi.metatavu.edelphi.domainmodel.orders.OrderStatus;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class OrderHistoryDAO extends GenericDAO<OrderHistory> {

  @SuppressWarnings ("squid:S00107")
  public OrderHistory create(Date created, User user, Plan plan, SubscriptionLevel subscriptionLevel, OrderStatus status, String currency, Double price, String text, Integer days) {
    OrderHistory orderHistory = new OrderHistory();
    
    orderHistory.setCurrency(currency);
    orderHistory.setPrice(price);
    orderHistory.setText(text);
    orderHistory.setStatus(status);
    orderHistory.setDays(days);
    orderHistory.setSubscriptionLevel(subscriptionLevel);
    orderHistory.setUser(user);
    orderHistory.setPlan(plan);
    orderHistory.setCreated(created);
    
    return persist(orderHistory);
  }

  public OrderHistory updateStatus(OrderHistory orderHistory, OrderStatus status) {
    orderHistory.setStatus(status);
    return persist(orderHistory);
  }

  public List<OrderHistory> listAllByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<OrderHistory> criteria = criteriaBuilder.createQuery(OrderHistory.class);
    Root<OrderHistory> root = criteria.from(OrderHistory.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(OrderHistory_.user), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  
}
