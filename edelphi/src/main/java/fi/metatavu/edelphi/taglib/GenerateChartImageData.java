package fi.metatavu.edelphi.taglib;

import java.io.Serializable;
import java.util.Map;

import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;

/**
 * Chart image data for postponed image generation
 * 
 * @author Antti Leppä
 */
public class GenerateChartImageData implements Serializable {

  private static final long serialVersionUID = -2829245172348504343L;
  
  private Map<String, String> chartParameters;
  private Long queryPageId;
  private ReportContext reportContext;
  private int width;
  private int height;

  public GenerateChartImageData() {
    // Zero-argument constructor
  }

  public GenerateChartImageData(int width, int height, Map<String, String> chartParameters, Long queryPageId, ReportContext reportContext) {
    super();
    this.width = width;
    this.height = height;
    this.chartParameters = chartParameters;
    this.queryPageId = queryPageId;
    this.reportContext = reportContext;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Map<String, String> getChartParameters() {
    return chartParameters;
  }

  public void setChartParameters(Map<String, String> chartParameters) {
    this.chartParameters = chartParameters;
  }

  public Long getQueryPageId() {
    return queryPageId;
  }

  public void setQueryPageId(Long queryPageId) {
    this.queryPageId = queryPageId;
  }

  public ReportContext getReportContext() {
    return reportContext;
  }

  public void setReportContext(ReportContext reportContext) {
    this.reportContext = reportContext;
  }

}
