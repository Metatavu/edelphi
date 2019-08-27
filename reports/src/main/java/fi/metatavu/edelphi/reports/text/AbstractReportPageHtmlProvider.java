package fi.metatavu.edelphi.reports.text;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

/**
 * Abstract base class for all report page html providers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractReportPageHtmlProvider implements ReportPageHtmlProvider {

  @Inject
  private Logger logger;

  /**
   * Formats date into localized date time string
   * 
   * @param locale locale
   * @param date date
   * @return localized date time string
   */
  protected String formatDateTime(Locale locale, Date date) {
    if (date == null) {
      return "";
    }
    
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
    return formatter.format(OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
  }

  /**
   * Returns byte array as a data URL
   * 
   * @param data data
   * @param contentType content type
   * @return data URL
   */
  protected String getAsDataURL(byte[] data, String contentType) {
    StringBuilder dataUrlBuilder = new StringBuilder();
    dataUrlBuilder.append("data:");
    dataUrlBuilder.append(getBaseContentType(contentType));
    dataUrlBuilder.append(";base64,");
    dataUrlBuilder.append(Base64.encodeBase64String(data));
    return dataUrlBuilder.toString();
  }
  
  /**
   * Resolves base content type from a content type string
   * 
   * @param contentType content type string
   * @return base content type
   */
  protected String getBaseContentType(String contentType) {
    try {
      MimeType mimeType = new MimeType(contentType);
      return mimeType.getBaseType();
    } catch (MimeTypeParseException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Failed to parse mime type {}", contentType, e);
      }
    }

    return contentType;
  }
  
}
