package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.List;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;

public interface QueryPageSpreadsheetExporter {

  /**
   * Exports page data to spreadsheet
   * 
   * @param exportContext spreadsheet export context
   */
  public void exportSpreadsheet(SpreadsheetExportContext exportContext);

  /**
   * Exports page root comments to spreadsheet 
   * 
   * @param exportContext spreadsheet export context
   */
  public void exportRootComments(SpreadsheetExportContext exportContext);
  
  /**
   * Exports all page comments to spreadsheet
   *  
   * @param queryPage query page
   * @param stamp panel stamp
   * @param replies replies
   * @return page comment processor
   */
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies);

}
