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
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.ScatterValue;
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
   * Creates a bar chart 
   * 
   * @param locale locale
   * @param title chart title
   * @param label chart label
   * @param options options
   * @param values values
   * @return bar chart
   */
  public CategoryChart createBarChart(
          Locale locale,
          String title,
          String label,
          List<String> options,
          double[] values
  ) {
    // Create Chart
    
    CategoryChart chart = new CategoryChartBuilder()
            .width(GRAPH_WIDTH)
            .height(GRAPH_HEIGHT)
            .title(title)
            .xAxisTitle(label)
            .build();
    
    // Customize Chart
    
    chart.getStyler().setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Bar);
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
    chart.getStyler().setMarkerSize(16);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    chart.getStyler().setChartTitleBoxBackgroundColor(Color.WHITE);
    chart.getStyler().setLocale(locale);
    
    // Axis 
    
    double maxX = options.size() - 1;
    
    chart.getStyler().setXAxisMin(0d);
    chart.getStyler().setXAxisMax(maxX);
    chart.getStyler().setYAxisMin(0d);
    
    // Ticks 
    
    chart.setXAxisLabelOverrideMap(createTickMap(options));
    
    // Series
    
    double[] categories = new double[options.size()];
    for (int i = 0; i < categories.length; i++) {
      categories[i] = i;
    }

    if (values != null && values.length > 0) {
      chart.addSeries("data", categories, values);
    }

    return chart;
  }

  /**
   * Creates a live 2d report
   * 
   * @param locale locale
   * @param title chart title
   * @param scatterValues scatter values
   * @param labelX label on x-axis
   * @param labelY label on y-axis
   * @param optionsX options on x-axis
   * @param optionsY options on y-axis
   * @return live 2d chart
   * @throws ReportException thrown when chart creation fails
   */
  @SuppressWarnings ({"squid:S3776"})
  public XYChart createLive2dChart(
          Locale locale,
          String title,
          List<ScatterValue> scatterValues,
          String labelX,
          String labelY,
          List<String> optionsX,
          List<String> optionsY
  ) throws ReportException {
    try {
      // Create Chart

      XYChart chart = new XYChartBuilder()
              .width(GRAPH_WIDTH)
              .height(GRAPH_HEIGHT)
              .title(title)
              .xAxisTitle(labelX)
              .yAxisTitle(labelY).build();

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

      double maxX = optionsX.size() - 1;
      double maxY = optionsY.size() - 1;

      chart.getStyler().setXAxisMin(0d);
      chart.getStyler().setXAxisMax(maxX);
      chart.getStyler().setYAxisMin(0d);
      chart.getStyler().setYAxisMax(maxY);

      // Ticks

      chart.setXAxisLabelOverrideMap(createTickMap(optionsX));
      chart.setYAxisLabelOverrideMap(createTickMap(optionsY));

      // Values

      double[] xValues = scatterValues.stream().map(ScatterValue::getX).mapToDouble(Double::doubleValue).toArray();
      double[] yValues = scatterValues.stream().map(ScatterValue::getY).mapToDouble(Double::doubleValue).toArray();

      // Series

      if (xValues != null && xValues.length > 0 && yValues != null && yValues.length > 0) {
        chart.addSeries("values", xValues, yValues);
      }

      addStraightLineSerie(chart, "xaxis", 0, maxY / 2, maxX, maxY / 2);
      addStraightLineSerie(chart, "yaxis", maxX / 2, 0, maxX / 2, maxY);

      return chart;
    } catch (Exception e) {
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
  public String printGraphSVG(XYChart chart) throws ReportException {
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
   * Render chart as HTML img PNG
   * 
   * @param chart chart
   * @return HTML
   * @throws ReportException thrown when graph printing fails
   */
  public String printGraphPNG(Chart<?, ?> chart) throws ReportException {
    try {
      byte[] chartData = renderChartPNG(chart);
      return String.format("<img src=\"data:image/png;base64,%s\" width=\"%s\" height=\"%s\"/>", Base64.encodeBase64String(chartData), GRAPH_WIDTH, GRAPH_HEIGHT);
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
  public byte[] renderChartSVG(Chart<?, ?> chart) throws IOException {
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
  public byte[] renderChartPNG(Chart<?, ?> chart) throws IOException {
    return BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
  }
  
  /**
   * Adds a straight line series into a chart
   * 
   * @param chart chart
   * @param label label
   * @param x1 x1
   * @param y1 xy
   * @param x2 x2
   * @param y2 y2
   */
  private void addStraightLineSerie(XYChart chart, String label, double x1, double y1, double x2, double y2) {
    XYSeries axisSeriesLiability = chart.addSeries(label, new double[] { x1, x2 }, new double[] { y1, y2 });
    axisSeriesLiability.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
    axisSeriesLiability.setMarker(SeriesMarkers.NONE);
    axisSeriesLiability.setLineWidth(2f);
    axisSeriesLiability.setLineColor(Color.gray);
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
      if (StringUtils.isNotBlank(ticks.get(i))) {
        result.put(Double.valueOf(i), ticks.get(i));
      }
    }
    
    return result;
  }
  
}
