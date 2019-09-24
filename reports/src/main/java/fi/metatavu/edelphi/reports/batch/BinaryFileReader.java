package fi.metatavu.edelphi.reports.batch;

import java.io.Serializable;
import java.util.List;

import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.TypedItemReader;

/**
 * Batch item reader for reading images user transient data
 * 
 * @author Antti Leppä
 */
@Named
public class BinaryFileReader extends TypedItemReader<BinaryFile> {
  
  @Inject
  private Logger logger;

  @Inject
  private JobContext jobContext;
  
  private int index;
  
  private List<BinaryFile> images;
  
  @SuppressWarnings("unchecked")
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);
    images = (List<BinaryFile>) jobContext.getTransientUserData();
    index = 0;
    logger.info("Reading {} chart images for report", images.size());
  }

  @Override
  public BinaryFile read() throws Exception {
    try {
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
