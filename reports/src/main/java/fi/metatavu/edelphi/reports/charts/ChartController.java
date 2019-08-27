package fi.metatavu.edelphi.reports.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.binary.Base64;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.ReportException;

/**
 * Controller for charts
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ChartController {
  
  private static final int GRAPH_WIDTH = 690;
  private static final int GRAPH_HEIGHT = 690;
  private static final String OBJECT_STYLES = "display: block; border: 1px solid #000; margin: 10px;";

  /**
   * Creates a live 2d report
   * 
   * @param locale locale
   * @param queryPage
   * @param queryReplies
   * @param title
   * @param labelX
   * @param labelY
   * @param fieldNameX
   * @param fieldNameY
   * @return
   * @throws ReportException
   */
  @SuppressWarnings ({"squid:S3776"})
  public String createLive2dChart(Locale locale, QueryPage queryPage, List<QueryReply> queryReplies, List<ScatterValue> scatterValues, String labelX, String labelY, List<String> optionsX, List<String> optionsY) throws ReportException {
    // Create Chart
    
    XYChart chart = new XYChartBuilder().width(GRAPH_WIDTH).height(GRAPH_HEIGHT).title(queryPage.getTitle()).xAxisTitle(labelX).yAxisTitle(labelY).build();
    
    // Customize Chart
    
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
    chart.getStyler().setMarkerSize(16);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    chart.getStyler().setChartTitleBoxBackgroundColor(Color.WHITE);
    chart.getStyler().setLocale(locale);
    
    // Axis 
    
    chart.getStyler().setXAxisMin(0d);
    chart.getStyler().setXAxisMax(Double.valueOf(optionsX.size()));
    chart.getStyler().setYAxisMin(0d);
    chart.getStyler().setYAxisMax(Double.valueOf(optionsY.size()));
    
    // Ticks 
    
    chart.setXAxisLabelOverrideMap(createTickMap(optionsX));
    chart.setYAxisLabelOverrideMap(createTickMap(optionsY));
    
    // Values
    
    double[] xValues = scatterValues.stream().map(ScatterValue::getX).mapToDouble(Double::doubleValue).toArray();
    double[] yValues = scatterValues.stream().map(ScatterValue::getY).mapToDouble(Double::doubleValue).toArray();
    
    // Series
    
    chart.addSeries("values", xValues, yValues);
    
    return printGraphPNG(chart);
  }

  /**
   * Prints graph
   * 
   * @param chart chart
   * @return HTML
   * @throws ReportException thrown when graph printing fails
   */
  @SuppressWarnings("unused")
  private String printGraphSVG(XYChart chart) throws ReportException {
    try {
      byte[] chartData = renderChartSVG(chart);

      StringBuilder html = new StringBuilder();
      html.append("<object type=\"image/svg+xml\"");
      StringBuilder dataUrlBuilder = new StringBuilder();
      dataUrlBuilder.append("data:image/svg+xml;charset=UTF-8;base64,");
      dataUrlBuilder.append(Base64.encodeBase64String(chartData));
      html.append(String.format(" data=\"%s\"", dataUrlBuilder.toString()));
      html.append(" width=\"").append(GRAPH_WIDTH).append('"').append(" height=\"").append(GRAPH_HEIGHT).append('"').append(String.format(" style=\"%s\"", OBJECT_STYLES)).append("></object>");
      
      return html.toString();
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }

  /**
   * Prints graph
   * 
   * @param chart chart
   * @return HTML
   * @throws ReportException thrown when graph printing fails
   */
  private String printGraphPNG(XYChart chart) throws ReportException {
    try {
      byte[] chartData = renderChartPNG(chart);
      return String.format("<img src=\"data:image/svg+xml;charset=UTF-8;base64,%s\" width=\"%s\" height=\"%s\"/>", Base64.encodeBase64String(chartData), GRAPH_WIDTH, GRAPH_HEIGHT);
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Renders a chart as SVG
   * 
   * @param chart chart
   * @return SVG
   * @throws IOException thrown when chart rendering fails
   */
  @SuppressWarnings("unused")
  private byte[] renderChartSVG(XYChart chart) throws IOException {
    Processor processor = new SVGProcessor();
    Graphics2D vg2d = new VectorGraphics2D();
    CommandSequence commands = ((VectorGraphics2D) vg2d).getCommands();
    chart.paint(vg2d, chart.getWidth(), chart.getHeight());

    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      PageSize pageSize = new PageSize(0.0, 0.0, chart.getWidth(), chart.getHeight());
      de.erichseifert.vectorgraphics2d.Document document = processor.getDocument(commands, pageSize);
      document.writeTo(output);
      
      return output.toByteArray();
    }
  }
  
  /**
   * Renders a chart as PNG
   * 
   * @param chart chart
   * @return SVG
   * @throws IOException thrown when chart rendering fails
   */
  private byte[] renderChartPNG(XYChart chart) throws IOException {
    return BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
  }

  /**
   * Creates a tick label map
   * 
   * @param ticks ticks
   * @return tick label map
   */
  private Map<Double, Object> createTickMap(List<String> ticks) {
    Map<Double, Object> result = new HashMap<>();
    
    for (int i = 0; i < ticks.size(); i++) {
      result.put(Double.valueOf(i), ticks.get(i));
    }
    
    return result;
  }
  
}
