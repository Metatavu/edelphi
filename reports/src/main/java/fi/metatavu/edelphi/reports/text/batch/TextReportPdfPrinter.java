package fi.metatavu.edelphi.reports.text.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.batch.api.AbstractBatchlet;
import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.reports.pdf.PdfPrinter;
import fi.metatavu.edelphi.reports.text.TextReportController;

/**
 * Batchlet for printing and delivering text reports as PDFs
 * 
 * @author Antti Leppä
 */
@Named
@Stateless
@AccessTimeout (unit = TimeUnit.HOURS, value = 4)
public class TextReportPdfPrinter extends AbstractBatchlet {
  
  @Inject
  private TextReportBatchContext reportHtmlBatchContext;

  @Inject
  private TextReportController htmlReportController;

  @Inject
  private PdfPrinter pdfPrinter;

  @Inject
  private Mailer mailer;
  
  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private Long[] pageIds;

  @Inject
  @JobProperty
  private String deliveryEmail;
  
  @Override
  public String process() throws Exception { 
    String html = htmlReportController.getHtmlReport(baseUrl, reportHtmlBatchContext.getPageHtmls());
    
    try (InputStream htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8")); ByteArrayOutputStream pdfStream = new ByteArrayOutputStream()) {
      pdfPrinter.printHtmlAsPdf(htmlStream, pdfStream);
    
      Email email = EmailBuilder.startingBlank()
        .from("noreply@edelphi.org")
        .to(deliveryEmail)
        .withSubject("The report")
        .withPlainText("The report")
        .withAttachment("report.pdf", pdfStream.toByteArray(), "application/pdf")
        .buildEmail();
      
      mailer.sendMail(email);
    }
    
    return "DONE";
  }
  
}
