package fi.metatavu.edelphi.reports.image.batch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jberet.cdi.JobScoped;

import fi.metatavu.edelphi.reports.batch.BinaryFile;

/**
 * Batch context for image reports
 * 
 * @author Antti Leppä
 */
@JobScoped
public class ImageReportBatchContext {
  
  private List<BinaryFile> images;
  
  @PostConstruct
  public void init() {
    images = new ArrayList<>();
  }
  
  /**
   * Adds a chart image
   * 
   * @param image image
   */
  public void addImage(BinaryFile image) {
    images.add(image);
  }

  /**
   * Returns chart images
   * 
   * @return chart images
   */
  public List<BinaryFile> getImages() {
    return images;
  }
  
}
