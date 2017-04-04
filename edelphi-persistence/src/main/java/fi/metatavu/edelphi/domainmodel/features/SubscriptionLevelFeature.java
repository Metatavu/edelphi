package fi.metatavu.edelphi.domainmodel.features;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;

@Entity
@Cacheable
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class SubscriptionLevelFeature {

  @Id
  @TableGenerator(name="SubscriptionLevelFeature", table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy=GenerationType.TABLE, generator="SubscriptionLevelFeature")  
  private Long id;
  
  @Column (nullable=false)
  @NotNull
  @Enumerated (EnumType.STRING)
  private SubscriptionLevel subscriptionLevel;
  
  @Column (nullable=false)
  @NotNull
  @Enumerated (EnumType.STRING)
  private Feature feature;
  
  public Feature getFeature() {
    return feature;
  }
  
  public void setFeature(Feature feature) {
    this.feature = feature;
  }
  
  public SubscriptionLevel getSubscriptionLevel() {
    return subscriptionLevel;
  }
  
  public void setSubscriptionLevel(SubscriptionLevel subscriptionLevel) {
    this.subscriptionLevel = subscriptionLevel;
  }
  
}