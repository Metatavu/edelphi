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
import fi.metatavu.edelphi.reports.text.live2d.Live2dReportPageHtmlProvider;

/**
 * Controller for text reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class TextReportController {
  
  @Inject
  private LegacyReportPageHtmlProvider legacyReportPageHtmlProvider;

  @Inject
  private Live2dReportPageHtmlProvider live2dReportPageHtmlProvider;
  
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
      
      addStylesheet(document, String.format("%s/_themes/default/css/theme.css", baseUrl));
      addStylesheet(document, String.format("%s/_themes/default/css/report_overrides.css", baseUrl));
      
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
   * Adds a StyleSheet into document
   * 
   * @param document document
   * @param href StyleSheet URL
   */
  private void addStylesheet(Document document, String href) {
    document.head().appendElement("link")
      .attr("type", "text/css")
      .attr("rel", "stylesheet")
      .attr("href", href);
  }


  /**
   * Generates a report HTML page 
   * 
   * @param context context
   * @return generated HTML page
   * @throws ReportException thrown when report HTML generation fails
   */
  public String getPageHtml(TextReportPageContext context) throws ReportException {
    switch (context.getPage().getPageType()) {
      case LIVE_2D:
        return live2dReportPageHtmlProvider.getPageHtml(context);
      default:
    }
    
    return legacyReportPageHtmlProvider.getPageHtml(context);
  }

}
