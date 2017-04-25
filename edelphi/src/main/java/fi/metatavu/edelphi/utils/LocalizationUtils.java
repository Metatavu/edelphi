package fi.metatavu.edelphi.utils;

import java.util.Locale;

import fi.metatavu.edelphi.dao.base.LocalizedValueDAO;
import fi.metatavu.edelphi.domainmodel.base.LocalizedEntry;
import fi.metatavu.edelphi.domainmodel.base.LocalizedValue;

public class LocalizationUtils {
  
  private LocalizationUtils() {
  }
  
  private static final String[] SUPPORTED_LOCALES = {"en", "fi"};
  
  public static String getLocalizedText(LocalizedEntry entry, Locale locale) {
    // TODO: Default value
    LocalizedValueDAO localizedValueDAO = new LocalizedValueDAO();
    LocalizedValue localizedValue = localizedValueDAO.findByEntryAndLocale(entry, locale);
    if (localizedValue == null)
      localizedValue = localizedValueDAO.findByEntryAndLocale(entry, new Locale(locale.getLanguage()));
    
    return localizedValue != null ? localizedValue.getText() : null;
  }
  
  public static String[] getSupportedLocales() {
    return SUPPORTED_LOCALES;
  }

  public static void updateText(LocalizedEntry entry, Locale locale, String text) {
    LocalizedValueDAO localizedValueDAO = new LocalizedValueDAO();
    LocalizedValue localizedValue = localizedValueDAO.findByEntryAndLocale(entry, locale);
    if (localizedValue == null) {
      localizedValueDAO.create(entry, locale, text);
    } else {
      localizedValueDAO.updateText(localizedValue, text);
    }
  }
  
}
