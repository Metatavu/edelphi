package fi.metatavu.edelphi.reports.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * Report locale strings controller
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ReportMessages {
  
  private Map<Locale, ResourceBundle> bundles;
  
  /**
   * Post construct method
   */
  @PostConstruct
  public void init() {
    bundles = new HashMap<>();
  }
  
  /**
   * Returns localized text for given key and locale
   * 
   * @param locale locale
   * @param key key
   * @return localized text
   */
  public String getText(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  /**
   * Returns localized text for given key and locale
   * 
   * @param locale locale
   * @param key key
   * @param params parameters
   * @return localized text
   */
  public String getText(Locale locale, String key, Object... params) {
    Locale.setDefault(locale);
    return MessageFormat.format(getText(locale, key), params);
  }
  
  /**
   * Locates a resource bundle for given locale
   * 
   * @param locale locale
   * @return resource bundle
   */
  private ResourceBundle getResourceBundle(Locale locale) {
    if (!bundles.containsKey(locale)) {
      ResourceBundle localeBundle = ResourceBundle.getBundle("fi.metatavu.edelphi.reports.i18n.reportlocale", locale); 
      bundles.put(locale, localeBundle);
    }

    return bundles.get(locale);
  }

}

