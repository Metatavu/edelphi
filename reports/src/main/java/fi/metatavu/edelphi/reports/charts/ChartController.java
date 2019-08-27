package fi.metatavu.edelphi.reports.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.ReportException;

/**
 * Controller for charts
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class ChartController {
  
  private static final int GRAPH_WIDTH = 600;
  private static final int GRAPH_HEIGHT = 600;
  
  private static final String OBJECT_STYLES = "display: block; border: 1px solid #000; margin: 10px;";
  private static final String OPTIONS_X = "live2d.options.x";
  private static final String OPTIONS_Y = "live2d.options.y";
  
  @Inject
  private QueryNumericFieldDAO queryNumericFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;

  @Inject
  private QueryPageController queryPageController;

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
  public String createLive2dChart(Locale locale, QueryPage queryPage, List<QueryReply> queryReplies, String title, String labelX, String labelY, String fieldNameX, String fieldNameY) throws ReportException {
    QueryNumericField queryFieldX = queryNumericFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryNumericField queryFieldY = queryNumericFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    List<String> optionsX = queryPageController.getListSetting(queryPage, OPTIONS_X);
    List<String> optionsY = queryPageController.getListSetting(queryPage, OPTIONS_Y);
    
    List<QueryQuestionNumericAnswer> answersX = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldX, queryReplies);
    List<QueryQuestionNumericAnswer> answersY = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldY, queryReplies);
    
    Map<Long, Double> answerMapX = answersX.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
    Map<Long, Double> answerMapY = answersY.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
    Map<Long, Double[]> answerMap = queryReplies.stream().map(QueryReply::getId).collect(Collectors.toMap(queryReplyId -> queryReplyId, queryReplyId -> new Double[] { answerMapX.get(queryReplyId), answerMapY.get(queryReplyId) }));
    
    List<ScatterValue> scatterValues = answerMap.values().stream()
      .filter(values -> values[0] != null && values[1] != null)
      .map(values -> new ScatterValue(values[0], values[1]))
      .collect(Collectors.toList());
    
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
    
    return printGraph(chart);
  }

  /**
   * Prints graph
   * 
   * @param chart chart
   * @return HTML
   * @throws ReportException thrown when graph printing fails
   */
  private String printGraph(XYChart chart) throws ReportException {
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
   * Renders a chart as SVG
   * 
   * @param chart chart
   * @return SVG
   * @throws IOException thrown when chart rendering fails
   */
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
