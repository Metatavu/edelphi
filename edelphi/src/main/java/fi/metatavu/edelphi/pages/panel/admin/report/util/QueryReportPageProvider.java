package fi.metatavu.edelphi.pages.panel.admin.report.util;

import java.util.HashMap;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.pages.panel.admin.report.expretise.ExpertiseQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.text.TextQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.FormQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisGroupingQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisMultiselectQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisOrderQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisScale1DQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisScale2DQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisTimeSerieQueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.thesis.ThesisTimelineQueryReportPage;

public class QueryReportPageProvider {
  
  public static QueryReportPageController getController(QueryPageType queryPageType) {
    return controllers.get(queryPageType);
  }

  public static void registerController(QueryReportPageController queryReportPageController) { // QueryPageType queryPageType) {
    controllers.put(queryReportPageController.getQueryPageType(), queryReportPageController);
  }

  static {
    controllers = new HashMap<QueryPageType, QueryReportPageController>();

    registerController(new TextQueryReportPage());
    registerController(new ThesisScale1DQueryReportPage());
    registerController(new ThesisScale2DQueryReportPage());
    registerController(new ThesisTimeSerieQueryReportPage());
    registerController(new ThesisMultiselectQueryReportPage());
    registerController(new ThesisOrderQueryReportPage());
    registerController(new ExpertiseQueryReportPage());
    registerController(new ThesisGroupingQueryReportPage());
    registerController(new ThesisTimelineQueryReportPage());
    registerController(new FormQueryReportPage());
  }
  
  private static Map<QueryPageType, QueryReportPageController> controllers;
}
