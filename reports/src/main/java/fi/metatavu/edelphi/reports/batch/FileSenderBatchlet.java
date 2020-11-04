package fi.metatavu.edelphi.reports.batch;

import java.util.Date;
import java.util.Locale;

import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.resources.ResourceController;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Batchlet for sending files via email
 * 
 * @author Antti Leppä
 */
@Named
public class FileSenderBatchlet extends AbstractPrinter {

  @Inject
  private Logger logger;

  @Inject
  private SettingsController settingsController;

  @Inject
  private QueryController queryController;

  @Inject
  private ResourceController resourceController;

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private Mailer mailer;

  @Inject
  private JobContext jobContext;

  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long queryId;
  
  @Inject
  @JobProperty
  private String deliveryEmail;
  
  @Inject
  @JobProperty
  private String emailSubjectLocale;

  @Inject
  @JobProperty
  private String emailContentLocale;

  @Override
  public String process() throws Exception { 
    Object transientUserData = jobContext.getTransientUserData();
    
    Query query = queryController.findQueryById(queryId);
    Panel panel = resourceController.getResourcePanel(query);
    
    if (transientUserData instanceof BinaryFile) {
      BinaryFile file = (BinaryFile) transientUserData;

      String panelName = panel.getName();
      String queryName = query.getName();
      Date now = new Date();
      String filters = getFilters();
      String settings = getOptions();
      
      String subject = reportMessages.getText(locale, emailSubjectLocale, panelName, queryName);
      String content = reportMessages.getText(locale, emailContentLocale, now, filters, settings);
    
      Email email = EmailBuilder.startingBlank()
        .from(settingsController.getEmailFromAddress())
        .to(deliveryEmail)
        .withSubject(subject)
        .withHTMLText(content)
        .withAttachment(file.getName(), file.getData(), file.getContentType())
        .buildEmail();
      
      mailer.sendMail(email);
      
      logger.info(String.format("Report sent via email into address %s", deliveryEmail));
    } else {
      logger.error("Could not find file to send");
      jobContext.setExitStatus("Failed");
    }

    return "DONE";
  }
  
}
