package fi.metatavu.edelphi.reports.image;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.reports.ReportException;

/**
 * Controller for text reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ImageReportController {
  
  @Inject
  private LegacyReportPageChartImageProvider legacyReportPageChartImageProvider;

  @Inject
  private Live2dReportPageChartImageProvider live2dReportPageChartImageProvider;
  
  /**
   * Generates a report chart as PNG
   * 
   * @param context context
   * @return generated chart
   * @throws ReportException thrown when report chart generation fails
   */
  public byte[] getPagePng(ImageReportPageContext exportContext) throws ReportException {
    switch (exportContext.getPage().getPageType()) {
      case LIVE_2D:
        return live2dReportPageChartImageProvider.getPng(exportContext);
      default:
    }
    
    return legacyReportPageChartImageProvider.getPng(exportContext);
  }

}
