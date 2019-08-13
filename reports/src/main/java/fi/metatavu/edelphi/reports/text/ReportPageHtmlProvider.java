package fi.metatavu.edelphi.reports.text;

import fi.metatavu.edelphi.reports.ReportException;

/**
 * Interface that describes a single HTML provider for a text report page
 * 
 * @author Antti Leppä
 */
public interface ReportPageHtmlProvider {
  
  /**
   * Generates a report HTML page
   * 
   * @param context report generate context
   * @return generated HTML page
   * @throws ReportException thrown when report generation fails
   */
  public String getPageHtml(TextReportPageContext context) throws ReportException;

}
