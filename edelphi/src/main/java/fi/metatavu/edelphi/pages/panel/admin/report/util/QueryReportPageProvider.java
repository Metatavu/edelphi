package fi.metatavu.edelphi.pages.panel.admin.report.util;

import java.util.EnumMap;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.pages.panel.admin.report.expretise.ExpertiseQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.text.TextQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.FormQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisGroupingQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisMultipleScale2DQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisMultiselectQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisOrderQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisScale1DQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisScale2DQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisTimeSerieQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisTimelineQueryReportPage;

public class QueryReportPageProvider {
  
  private static Map<QueryPageType, QueryReportPageController> controllers;
  
  private QueryReportPageProvider() {
  }
  
  public static QueryReportPageController getController(QueryPageType queryPageType) {
    return controllers.get(queryPageType);
  }

  public static void registerController(QueryReportPageController queryReportPageController) {
    controllers.put(queryReportPageController.getQueryPageType(), queryReportPageController);
  }

  static {
    controllers = new EnumMap<>(QueryPageType.class);

    registerController(new TextQueryReportPage());
    registerController(new ThesisScale1DQueryReportPage());
    registerController(new ThesisScale2DQueryReportPage());
    registerController(new ThesisTimeSerieQueryReportPage());
    registerController(new ThesisMultiselectQueryReportPage());
    registerController(new ThesisOrderQueryReportPage());
    registerController(new ThesisMultipleScale2DQueryReportPage());
    registerController(new ExpertiseQueryReportPage());
    registerController(new ThesisGroupingQueryReportPage());
    registerController(new ThesisTimelineQueryReportPage());
    registerController(new FormQueryReportPage());
  }
  
}
