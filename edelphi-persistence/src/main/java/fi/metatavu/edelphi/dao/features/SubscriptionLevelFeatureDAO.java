package fi.metatavu.edelphi.dao.features;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.features.SubscriptionLevelFeature;
import fi.metatavu.edelphi.domainmodel.features.SubscriptionLevelFeature_;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;

@ApplicationScoped
public class SubscriptionLevelFeatureDAO extends GenericDAO<SubscriptionLevelFeature> {

  public SubscriptionLevelFeature create(SubscriptionLevel subscriptionLevel, Feature feature) {
    SubscriptionLevelFeature subscriptionLevelFeature = new SubscriptionLevelFeature();
    subscriptionLevelFeature.setFeature(feature);
    subscriptionLevelFeature.setSubscriptionLevel(subscriptionLevel);
    return persist(subscriptionLevelFeature);
  }
  
  public SubscriptionLevelFeature findBySubscriptionLevelAndFeature(SubscriptionLevel subscriptionLevel, Feature feature) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SubscriptionLevelFeature> criteria = criteriaBuilder.createQuery(SubscriptionLevelFeature.class);
    Root<SubscriptionLevelFeature> root = criteria.from(SubscriptionLevelFeature.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(SubscriptionLevelFeature_.feature), feature), 
          criteriaBuilder.equal(root.get(SubscriptionLevelFeature_.subscriptionLevel), subscriptionLevel)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

}
