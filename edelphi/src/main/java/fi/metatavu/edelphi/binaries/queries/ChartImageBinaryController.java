package fi.metatavu.edelphi.binaries.queries;

import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.taglib.chartutil.ReportChartCache;

public class ChartImageBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    String id = requestContext.getString("id");
    
    byte[] data = ReportChartCache.pop(id);
    requestContext.setResponseContent(data, "image/svg+xml");
  }

}
