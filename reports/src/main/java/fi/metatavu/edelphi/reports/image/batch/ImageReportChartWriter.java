package fi.metatavu.edelphi.reports.image.batch;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.batch.BinaryFile;
import fi.metatavu.edelphi.reports.image.ChartData;
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
  private Long[] panelUserGroupIds;
  
  @Inject
  @JobProperty
  private Long[] queryReplyIds;
  
  private List<BinaryFile> images;
  
  private int index;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    index = 1;
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
   
    for (QueryPage queryPage : items.stream().filter(this::isSupportingCharts).collect(Collectors.toList())) {
      List<BinaryFile> pageImages = createPageReportImages(queryPage);
      if (!pageImages.isEmpty()) {
        images.addAll(pageImages);
        index++;
      }
    }
  }
  
  /**
   * Creates a report image for a query page
   * 
   * @param index page index
   * @param queryPage query page
   * @return report image
   * @throws ReportException thrown when image creation fails
   */
  private List<BinaryFile> createPageReportImages(QueryPage queryPage) throws ReportException {
    PanelStamp stamp = panelController.findPanelStampById(stampId);
    if (stamp == null) {
      throw new ReportException(String.format("Could not find panel stamp %d", stampId));
    }
    
    ImageReportPageContext exportContext = new ImageReportPageContext(baseUrl, locale, stamp, panelUserGroupIds, expertiseGroupIds, queryReplyIds, queryPage);
    List<ChartData> chartDatas = imageReportController.getPageCharts(exportContext);
    List<BinaryFile> result = new ArrayList<>(chartDatas.size());
    
    for (int imageIndex = 0; imageIndex < chartDatas.size(); imageIndex++) {
      ChartData chartData = chartDatas.get(imageIndex);
      if (chartData != null && chartData.getData() != null && chartData.getData().length > 0) {
        String fileName = getFileName(chartData.getTitle());
        result.add(new BinaryFile(String.format("%d-%d-%s.png", index, imageIndex, fileName), chartData.getContentType(), chartData.getData()));
      }
    }
    
    return result;
  }
  
  /**
   * Returns a file name for given page name
   * 
   * @param name name
   * @return URL name
   */
  private String getFileName(String name) {
    if (name == null) {
      return null;
    }
    
    String urlName = name.trim().toLowerCase().replace(' ', '-').replace('/', '-');
    while (urlName.indexOf("--") > 0) {
      urlName = urlName.replace("--", "-");
    }
    
    try {
      urlName = URLEncoder.encode(urlName, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.error("Failed to URLEncode report page name", e);
    }
    
    return urlName;
  }
  
  /**
   * Returns whether page supports charts or not
   * 
   * @param queryPage query page
   * @return whether page supports charts or not
   */
  private boolean isSupportingCharts(QueryPage queryPage) {
    QueryPageType pageType = queryPage.getPageType();
    
    if (pageType == QueryPageType.TEXT) {
      return false;
    }
    
    return true;
  }

}
