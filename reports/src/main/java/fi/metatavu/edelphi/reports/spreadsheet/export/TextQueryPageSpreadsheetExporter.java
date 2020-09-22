package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.GenericReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;

@ApplicationScoped
public class TextQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {

  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    exportComments(exportContext);
  }
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return new GenericReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>());
  }
  
}
