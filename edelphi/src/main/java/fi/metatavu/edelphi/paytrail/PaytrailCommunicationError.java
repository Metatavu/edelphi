package fi.metatavu.edelphi.paytrail;

import java.util.Locale;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;

/**
 * Exception that is thrown when communication fails with Paytrail
 */
public class PaytrailCommunicationError extends SmvcRuntimeException {

  private static final long serialVersionUID = -4699891139273170011L;

  public PaytrailCommunicationError(Locale locale, Exception e) {
    super(EdelfoiStatusCode.PAYTRAIL_COMMUNICATION_ERROR, Messages.getInstance().getText(locale, "exception.1045.paytrailCommunicationError"), e);
  }

}
