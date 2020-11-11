package fi.metatavu.edelphi.reports.spreadsheet.batch;

import java.util.Date;
import java.util.Locale;

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
 * Batchlet for printing and delivering spreadsheet reports as CSVs
 * 
 * @author Antti Leppä
 */
@Named
public class SpreadsheetCsvPrinter extends AbstractSpreadsheetPrinter {

  @Inject
  private Logger logger;

  @Inject
  private SettingsController settingsController;
  
  @Inject
  private Mailer mailer;

  @Inject
  private QueryController queryController;

  @Inject
  private ResourceController resourceController;

  @Inject
  private ReportMessages reportMessages;
  
  @Inject
  @JobProperty
  private Long queryId;

  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private Long[] pageIds;

  @Inject
  @JobProperty
  private String deliveryEmail;

  @Inject
  @JobProperty
  private Locale locale;
  
  @Override
  public String process() throws Exception { 
    byte[] csvData = exportCsvData();
    
    Query query = queryController.findQueryById(queryId);
    Panel panel = resourceController.getResourcePanel(query);

    String panelName = panel.getName();
    String queryName = query.getName();
    Date now = new Date();
    String filters = getFilters();
    String settings = getOptions();
    
    String subject = reportMessages.getText(locale, "reports.csv.mail.subject", panelName, queryName);
    String contents = reportMessages.getText(locale, "reports.csv.mail.contents", now, filters, settings);
    String file = String.format("%s-%s.csv", panel.getUrlName(), query.getUrlName());
  
    Email email = EmailBuilder.startingBlank()
      .from(settingsController.getEmailFromAddress())
      .to(deliveryEmail)
      .withSubject(subject)
      .withHTMLText(contents)
      .withAttachment(file, csvData, "text/csv")
      .buildEmail();
    
    mailer.sendMail(email);
    
    logger.info(String.format("CSV Report sent via email into address %s", deliveryEmail));
  
    return "DONE";
  }
  
}
