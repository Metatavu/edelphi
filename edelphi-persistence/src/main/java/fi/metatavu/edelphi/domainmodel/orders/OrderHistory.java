package fi.metatavu.edelphi.domainmodel.orders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;

@Entity
public class OrderHistory {

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="OrderHistory")  
  @TableGenerator(name="OrderHistory", initialValue=1, allocationSize=1, table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
  private Long id;
  
  @Lob
  private String text;
 
  @Column (nullable = false)
  @NotNull
  private String currency;
  
  @Column (nullable = false)
  @NotNull
  private Double price;
  
  @Column (nullable = false)
  @NotNull
  @Enumerated (EnumType.STRING)
  private OrderStatus status;
  
  @Column (nullable = false)
  @NotNull
  private Integer days;
  
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private SubscriptionLevel subscriptionLevel;

  @ManyToOne 
  private User user;
  
  @ManyToOne 
  private Plan plan;
  

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
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
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public OrderStatus getStatus() {
    return status;
  }
  
  public void setStatus(OrderStatus status) {
    this.status = status;
  }
  
  public void setDays(Integer days) {
    this.days = days;
  }
  
  public Integer getDays() {
    return days;
  }
  
  public void setSubscriptionLevel(SubscriptionLevel subscriptionLevel) {
    this.subscriptionLevel = subscriptionLevel;
  }
  
  public SubscriptionLevel getSubscriptionLevel() {
    return subscriptionLevel;
  }

  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Plan getPlan() {
    return plan;
  }
  
  public void setPlan(Plan plan) {
    this.plan = plan;
  }
  
}