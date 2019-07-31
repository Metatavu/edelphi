package fi.metatavu.edelphi.dao.orders;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.orders.Plan;
import fi.metatavu.edelphi.domainmodel.orders.Plan_;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;

@ApplicationScoped
public class PlanDAO extends GenericDAO<Plan> {

  public Plan create(SubscriptionLevel subscriptionLevel, Double price, String currency, Integer days, LocalizedEntry name, LocalizedEntry description, Boolean visible) {
    Plan plan = new Plan();
    plan.setCurrency(currency);
    plan.setDays(days);
    plan.setDescription(description);
    plan.setName(name);
    plan.setPrice(price);
    plan.setSubscriptionLevel(subscriptionLevel);
    plan.setVisible(visible);
    return persist(plan);
  }

  public List<Plan> listByVisible(Boolean visible) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Plan> criteria = criteriaBuilder.createQuery(Plan.class);
    Root<Plan> root = criteria.from(Plan.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Plan_.visible), visible));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Plan updatePrice(Plan plan, Double price) {
    plan.setPrice(price);
    return persist(plan);
  }
  
  public Plan updateCurrency(Plan plan, String currency) {
    plan.setCurrency(currency);
    return persist(plan);
  }

  public Plan updateDays(Plan plan, Integer days) {
    plan.setDays(days);
    return persist(plan);
  }

  public Plan updateSubscriptionLevel(Plan plan, SubscriptionLevel subscriptionLevel) {
    plan.setSubscriptionLevel(subscriptionLevel);
    return persist(plan);
  }

  public Plan updateVisible(Plan plan, Boolean visible) {
    plan.setVisible(visible);
    return persist(plan);
  }

}
