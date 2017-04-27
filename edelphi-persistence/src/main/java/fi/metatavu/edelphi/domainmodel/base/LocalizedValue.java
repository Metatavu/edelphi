package fi.metatavu.edelphi.domainmodel.base;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class LocalizedValue {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private LocalizedEntry entry;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  @Lob
  private String text;
  
  @Column (nullable = false)
  @NotNull
  private Locale locale;
  
  public Long getId() {
    return id;
  }
  
  public LocalizedEntry getEntry() {
    return entry;
  }
  
  public void setEntry(LocalizedEntry entry) {
    this.entry = entry;
  }
  
  public Locale getLocale() {
    return locale;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
}
