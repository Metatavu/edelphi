package fi.metatavu.edelphi.smvcj;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.i18n.Messages;

/** An exception that's thrown whenever a non-existing page is requested.
 */
public class PageNotFoundException extends SmvcRuntimeException {

  /** The serial version UID of the class */
  private static final long serialVersionUID = -7202576899357656774L;

  public PageNotFoundException(Locale locale) {
    super(StatusCode.PAGE_NOT_FOUND, Messages.getInstance().getText(locale, "exception.102.pageNotFound"));
  }

}
