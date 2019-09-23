package fi.metatavu.edelphi.reports.image;

import fi.metatavu.edelphi.reports.ReportException;

/**
 * Interface that describes a single chart provider for a image report page
 * 
 * @author Antti Leppä
 */
public interface ReportPageChartImageProvider {
  
  /**
   * Generates a report page chart as PNG
   * 
   * @param context report generate context
   * @return image data
   * @throws ReportException thrown when report generation fails
   */
  public byte[] getPng(ImageReportPageContext context) throws ReportException;

}
