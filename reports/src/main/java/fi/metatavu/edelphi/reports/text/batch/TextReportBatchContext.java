package fi.metatavu.edelphi.reports.text.batch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jberet.cdi.JobScoped;

/**
 * Batch context for text reports
 * 
 * @author Antti Leppä
 */
@JobScoped
public class TextReportBatchContext {
  
  private List<String> pageHtmls;
  
  @PostConstruct
  public void init() {
    pageHtmls = new ArrayList<>();
  }
  
  /**
   * Adds a HTML page into a report
   * 
   * @param html HTML 
   */
  public void addPageHtml(String html) {
    pageHtmls.add(html);
  }

  /**
   * Returns report HTML pages
   * 
   * @return report HTML pages
   */
  public List<String> getPageHtmls() {
    return pageHtmls;
  }
  
}
