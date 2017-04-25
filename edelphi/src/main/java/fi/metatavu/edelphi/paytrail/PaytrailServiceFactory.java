package fi.metatavu.edelphi.paytrail;

import fi.metatavu.edelphi.utils.SystemUtils;
import fi.metatavu.paytrail.PaytrailService;
import fi.metatavu.paytrail.io.IOHandler;
import fi.metatavu.paytrail.json.Marshaller;

public class PaytrailServiceFactory {
  
  private PaytrailServiceFactory() {
  }
  
  /**
   * Creates a paytrail service instance
   * 
   * @return created paytrail service instance
   */
  public static PaytrailService createPaytrailService() {
		IOHandler ioHandler = new HttpClientIOHandler();
		Marshaller marshaller = new JacksonMarshaller();
    String merchantId = SystemUtils.getSettingValue("paytrail.merchantId");
    String merchantSecret = SystemUtils.getSettingValue("paytrail.merchantSecret");
		return new PaytrailService(ioHandler, marshaller, merchantId, merchantSecret);
	}
	
}
