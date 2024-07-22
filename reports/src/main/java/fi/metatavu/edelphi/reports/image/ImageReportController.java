package fi.metatavu.edelphi.reports.image;

import java.util.List;

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

  @Inject
  private MultipleScale1dReportPageChartImageProvider multipleScale1dReportPageChartImageProvider;
  
  /**
   * Generates a report chart as PNG
   *
   * @return generated chart
   * @throws ReportException thrown when report chart generation fails
   */
  public List<ChartData> getPageCharts(ImageReportPageContext exportContext) throws ReportException {
    switch (exportContext.getPage().getPageType()) {
      case LIVE_2D:
        return live2dReportPageChartImageProvider.getPageCharts(exportContext);
      case THESIS_MULTIPLE_1D_SCALES:
        return multipleScale1dReportPageChartImageProvider.getPageCharts(exportContext);
      default:
    }
    
    return legacyReportPageChartImageProvider.getPageCharts(exportContext);
  }

}
