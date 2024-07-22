package fi.metatavu.edelphi.reports.image;

import java.io.IOException;
import java.util.ArrayList;
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
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.charts.ChartController;
import org.apache.commons.lang3.StringUtils;

/**
 * Report chart image provider for multiple scale 1d reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class MultipleScale1dReportPageChartImageProvider extends AbstractReportPageChartImageProvider {

  @Inject
  private ChartController chartController;
  
  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private QueryPageController queryPageController;
  
  @Override
  public List<ChartData> getPageCharts(ImageReportPageContext exportContext) throws ReportException {
    List<ChartData> result = new ArrayList<>();
    
    QueryPage queryPage = exportContext.getPage();
    Long[] queryReplyIds = exportContext.getQueryReplyIds();
    Locale locale = exportContext.getLocale();
    List<QueryReply> queryReplies = queryReplyIds == null ? Collections.emptyList() : Arrays.stream(queryReplyIds).map(queryReplyController::findQueryReply).collect(Collectors.toList());
    
    String label = queryPageController.getSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_LABEL_OPTION);
    List<String> options = queryPageController.getListSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_OPTIONS_OPTION);
    double[][] pageValues = queryPageController.getMultipleScale1dValues(queryPage, queryReplies);
    List<String> theses = queryPageController.getListSetting(queryPage, QueryPageController.MULTIPLE_1D_SCALES_THESES_OPTION);

    for (int thesisIndex = 0; thesisIndex < pageValues.length; thesisIndex++) {
      String thesis = theses.size() > thesisIndex ? theses.get(thesisIndex).trim() : null;
      String title = StringUtils.isEmpty(thesis) ? queryPage.getTitle() : thesis;

      double[] thesisValues = pageValues[thesisIndex];
      try {
        byte[] data = chartController.renderChartPNG(chartController.createBarChart(locale, queryPage, queryReplies, label, options, thesisValues));
        if (data != null && data.length > 0) {
          result.add(new ChartData("image/png", data, title));
        }
      } catch (IOException e) {
        throw new ReportException(e);
      }
    }
    
    return result;
  }
  
}
