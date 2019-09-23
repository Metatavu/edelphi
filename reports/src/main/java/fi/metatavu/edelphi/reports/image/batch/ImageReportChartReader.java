package fi.metatavu.edelphi.reports.image.batch;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.TypedItemReader;
import fi.metatavu.edelphi.reports.batch.BinaryFile;

/**
 * Batch item reader for reading images from image report batch context
 * 
 * @author Antti Leppä
 */
@Named
public class ImageReportChartReader extends TypedItemReader<BinaryFile> {
  
  @Inject
  private Logger logger;

  @Inject
  private ImageReportBatchContext imageReportBatchContext;
  
  private int index;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    index = 0;
    logger.info("Reading {} chart images for report", imageReportBatchContext.getImages().size());
  }

  @Override
  public BinaryFile read() throws Exception {
    try {
      List<BinaryFile> images = imageReportBatchContext.getImages();
      int imageCount = images.size();
      
      if (this.index < imageCount) {
        return images.get(index);
      }
      
      return null;
    } finally {
      this.index++;
    }
  }
  
}
