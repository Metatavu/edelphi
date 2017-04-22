package fi.metatavu.edelphi.domainmodel.orders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;

@Entity
public class Plan {

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Plan")  
  @TableGenerator(name="Plan", initialValue=1, allocationSize=100, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @ManyToOne 
  private LocalizedEntry name;
  
  @ManyToOne 
  private LocalizedEntry description;
  
  @Column (nullable = false)
  @NotNull
  private Integer days;
  
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private SubscriptionLevel subscriptionLevel;
  
  @Column (nullable = false)
  @NotNull
  private String currency;
  
  @Column (nullable = false)
  @NotNull
  private Double price;
  
  @Column (nullable = false)
  @NotNull
  private Boolean visible;

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  public LocalizedEntry getName() {
    return name;
  }
  
  public void setName(LocalizedEntry name) {
    this.name = name;
  }
  
  public LocalizedEntry getDescription() {
    return description;
  }
  
  public void setDescription(LocalizedEntry description) {
    this.description = description;
  }
  
  public Integer getDays() {
    return days;
  }
  
  public void setDays(Integer days) {
    this.days = days;
  }
  
  public String getCurrency() {
    return currency;
  }
  
  public void setCurrency(String currency) {
    this.currency = currency;
  }
  
  public Double getPrice() {
    return price;
  }
  
  public void setPrice(Double price) {
    this.price = price;
  }
  
  public SubscriptionLevel getSubscriptionLevel() {
    return subscriptionLevel;
  }
  
  public void setSubscriptionLevel(SubscriptionLevel subscriptionLevel) {
    this.subscriptionLevel = subscriptionLevel;
  }

  public Boolean getVisible() {
    return visible;
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
  
}
