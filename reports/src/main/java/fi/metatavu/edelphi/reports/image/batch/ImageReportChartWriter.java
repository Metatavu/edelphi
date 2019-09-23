package fi.metatavu.edelphi.reports.image.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.batch.BinaryFile;
import fi.metatavu.edelphi.reports.image.ImageReportController;
import fi.metatavu.edelphi.reports.image.ImageReportPageContext;

/**
 * Batch item writer for writing report chart images
 * 
 * @author Antti Leppä
 */
@Named
public class ImageReportChartWriter extends TypedItemWriter<QueryPage> {

  @Inject
  private Logger logger;

  @Inject
  private ImageReportController imageReportController;

  @Inject
  private PanelController panelController;

  @Inject
  private JobContext jobContext;
  
  @Inject
  @JobProperty
  private String baseUrl;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long stampId;

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;
  
  @Inject
  @JobProperty
  private Long[] queryReplyIds;
  
  private List<BinaryFile> images;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    
    images = new ArrayList<>();
  }

  @Override
  public void close() throws Exception {
    jobContext.setTransientUserData(images);
    
    super.close();
  }
  
  @Override
  public void write(List<QueryPage> items) throws Exception {
    logger.info("Writing {} report chart images", items.size());
    
    for (QueryPage queryPage : items) {
      images.add(createPageReportImage(queryPage));
    }
  }
  
  /**
   * Creates a report image for a query page
   * 
   * @param queryPage query page
   * @return report image
   * @throws ReportException thrown when image creation fails
   */
  private BinaryFile createPageReportImage(QueryPage queryPage) throws ReportException {
    PanelStamp stamp = panelController.findPanelStampById(stampId);
    if (stamp == null) {
      throw new ReportException(String.format("Could not find panel stamp %d", stampId));
    }
    
    ImageReportPageContext exportContext = new ImageReportPageContext(baseUrl, locale, stamp, expertiseGroupIds, queryReplyIds, queryPage);
    byte[] pngData = imageReportController.getPagePng(exportContext);
    
    if (pngData == null || pngData.length == 0) {
      throw new ReportException(String.format("Failed to create PNG report queryPage %d", queryPage.getId()));
    }
    
    return new BinaryFile(String.format("%s.png", queryPage.getTitle()), "image/png", pngData);
  }

}
