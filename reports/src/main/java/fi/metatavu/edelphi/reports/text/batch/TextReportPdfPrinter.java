package fi.metatavu.edelphi.reports.text.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;
import javax.inject.Named;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.pdf.PdfPrinter;
import fi.metatavu.edelphi.reports.text.TextReportController;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Batchlet for printing and delivering text reports as PDFs
 * 
 * @author Antti Leppä
 */
@Named
public class TextReportPdfPrinter extends AbstractBatchlet {

  @Inject
  private Logger logger;
  
  @Inject
  private TextReportBatchContext reportHtmlBatchContext;

  @Inject
  private TextReportController htmlReportController;

  @Inject
  private PdfPrinter pdfPrinter;

  @Inject
  private Mailer mailer;

  @Inject
  private QueryController queryController;

  @Inject
  private ResourceController resourceController;

  @Inject
  private ReportMessages reportMessages;

  @Inject
  private PanelController panelController;
  
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

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;
  
  @Override
  public String process() throws Exception { 
    List<String> pageHtmls = reportHtmlBatchContext.getPageHtmls();
    logger.info("Creating PDF from {} html pages", pageHtmls.size());
    
    Query query = queryController.findQueryById(queryId);
    Panel panel = resourceController.getResourcePanel(query);
    String html = htmlReportController.getHtmlReport(baseUrl, pageHtmls);
    
    try (InputStream htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8")); ByteArrayOutputStream pdfStream = new ByteArrayOutputStream()) {
      pdfPrinter.printHtmlAsPdf(htmlStream, pdfStream);
      
      String panelName = panel.getName();
      String queryName = query.getName();
      Date now = new Date();
      String filters = getFilters();
      String settings = getOptions();
      
      String subject = reportMessages.getText(locale, "reports.pdf.mail.subject", panelName, queryName);
      String contents = reportMessages.getText(locale, "reports.pdf.mail.contents", now, filters, settings);
      String file = String.format("%s-%s.pdf", panel.getUrlName(), query.getUrlName());
    
      Email email = EmailBuilder.startingBlank()
        .from("noreply@edelphi.org")
        .to(deliveryEmail)
        .withSubject(subject)
        .withHTMLText(contents)
        .withAttachment(file, pdfStream.toByteArray(), "application/pdf")
        .buildEmail();
      
      mailer.sendMail(email);
      
      logger.info(String.format("Report sent via email into address %s", deliveryEmail));
    }
    
    return "DONE";
  }
  
  /**
   * Returns export filters as human readable text
   * 
   * @return export filters as human readable text
   */
  private String getFilters() {
    if (expertiseGroupIds != null) {    
      String groups = Arrays.stream(expertiseGroupIds)
        .map(panelController::findPanelUserExpertiseGroup)
        .map(this::getExpertiseGroupName)
        .collect(Collectors.joining(", "));
      
      return reportMessages.getText(locale, "reports.pdf.mail.expertiseFilter", groups);
    }
    
    return reportMessages.getText(locale, "reports.pdf.mail.noFilters");
  }
  
  /**
   * Returns export options as human readable text
   * 
   * @return export options as human readable text
   */
  private String getOptions() {
    return reportMessages.getText(locale, "reports.pdf.mail.noSpecifiedOptions");
  }

  /**
   * Return name for given expertise group
   * 
   * @param expertiseGroup expertise group
   * @return name for the group
   */
  private String getExpertiseGroupName(PanelUserExpertiseGroup expertiseGroup) {
    PanelUserIntressClass intressClass = expertiseGroup.getIntressClass();
    PanelUserExpertiseClass expertiseClass = expertiseGroup.getExpertiseClass();
    return String.format("%s / %s", intressClass.getName(), expertiseClass.getName());
  }
  
  
}
