package fi.metatavu.edelphi.binaries.queries;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.core.exception.BirtException;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.taglib.GenerateChartImageData;
import fi.metatavu.edelphi.taglib.chartutil.ReportChartCache;

/**
 * Generates chart image based on previously stored serialized chart image data
 * 
 * @author Antti Leppä
 */
public class GenerateChartImageBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    Locale locale = requestContext.getRequest().getLocale();

    String id = requestContext.getString("id");
    if (id == null) {
      throw new PageNotFoundException(locale);
    }
    
    byte[] data = ReportChartCache.pop(id);
    if (data == null) {
      throw new PageNotFoundException(locale);
    }
    
    GenerateChartImageData chartImageData = SerializationUtils.deserialize(data);
    Long queryPageId = chartImageData.getQueryPageId();
    Map<String, String> chartParameters = chartImageData.getChartParameters();
    ReportContext reportContext = chartImageData.getReportContext();
    int width = chartImageData.getWidth();
    int height = chartImageData.getHeight();
    
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    if (queryPage == null) {
      throw new PageNotFoundException(locale);
    }
    
    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    ChartContext chartContext = new ChartContext(reportContext, chartParameters);    
    Chart chartModel = queryReportPageController.constructChart(chartContext, queryPage);
    if (chartModel == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "Failed to generate report image");
    }

    Bounds bounds = chartModel.getBlock().getBounds();
    bounds.setWidth(width);
    bounds.setHeight(height);
    
    try {
      byte[] chartData = ChartModelProvider.getChartData(chartModel, "PNG");
      requestContext.setResponseContent(chartData, "image/png");
    } catch (BirtException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.REPORTING_ERROR, "Failed to generate report image", e);
    }
  }

}
