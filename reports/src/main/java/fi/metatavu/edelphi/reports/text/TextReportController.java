package fi.metatavu.edelphi.reports.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.text.legacy.LegacyReportPageHtmlProvider;

/**
 * Controller for text reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TextReportController {
  
  @Inject
  private LegacyReportPageHtmlProvider legacyReportPageHtmlProvider;
  
  /**
   * Returns a report HTML for given report pages
   * 
   * @param baseUrl base URL
   * @param bodyContents pages
   * @return report HTML
   * @throws ReportException thrown when report HTML generation fails
   */
  public String getHtmlReport(String baseUrl, List<String> bodyContents) throws ReportException {
    try (InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("report.html")) {
      Document document = Jsoup.parse(htmlStream, "UTF-8", baseUrl);

      for (String bodyContent : bodyContents) {
        document.body().append(bodyContent);
      }
      
      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);    
      document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

      return document.html();
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Generates a report HTML page 
   * 
   * @param context context
   * @return generated HTML page
   * @throws ReportException thrown when report HTML generation fails
   */
  public String getPageHtml(TextReportPageContext context) throws ReportException {
    return legacyReportPageHtmlProvider.getPageHtml(context);
  }

}
