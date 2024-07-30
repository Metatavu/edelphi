package fi.metatavu.edelphi.reports.image;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.queries.ScatterValue;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import org.apache.commons.lang3.StringUtils;

/**
 * Report chart image provider for live2d reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Live2dReportPageChartImageProvider extends AbstractReportPageChartImageProvider {

  private static final String OPTIONS_X = "live2d.options.x";
  private static final String OPTIONS_Y = "live2d.options.y";
  
  @Inject
  private ChartController chartController;
  
  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryPageController queryPageController;
  
  @Override
  public List<ChartData> getPageCharts(ImageReportPageContext exportContext) throws ReportException {
    QueryPage queryPage = exportContext.getPage();
    Long[] queryReplyIds = exportContext.getQueryReplyIds();
    Locale locale = exportContext.getLocale();
    
    List<QueryReply> queryReplies = queryReplyIds == null ? Collections.emptyList() : Arrays.stream(queryReplyIds).map(queryReplyController::findQueryReply).collect(Collectors.toList());

    String thesis = StringUtils.trim(queryPageController.getSetting(queryPage, "thesis.text"));
    String labelX = queryPageController.getSetting(queryPage, "live2d.label.x");
    String labelY = queryPageController.getSetting(queryPage, "live2d.label.y");
    String title = StringUtils.isEmpty(thesis) ? queryPage.getTitle() : thesis;
    List<String> optionsX = queryPageController.getListSetting(queryPage, OPTIONS_X);
    List<String> optionsY = queryPageController.getListSetting(queryPage, OPTIONS_Y);
    
    List<ScatterValue> scatterValues = queryPageController.getLive2dScatterValues(queryPage, queryReplies);
    try {
      byte[] data = chartController.renderChartPNG(chartController.createLive2dChart(
              locale,
              title,
              scatterValues,
              labelX,
              labelY,
              optionsX,
              optionsY)
      );

      if (data == null || data.length == 0) {
        return Collections.emptyList();
      }

      return Collections.singletonList(new ChartData("image/png", data, title));
    } catch (IOException e) {
      throw new ReportException(e);
    }
    
  }
  
}
