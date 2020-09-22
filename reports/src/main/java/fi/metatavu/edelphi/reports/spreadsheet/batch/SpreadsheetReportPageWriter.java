package fi.metatavu.edelphi.reports.spreadsheet.batch;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContextImpl;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetReportController;

/**
 * Generates spreadsheet reports for given query pages
 * 
 * @author Antti Leppä
 */
@Named
public class SpreadsheetReportPageWriter extends TypedItemWriter<QueryPage> {

  @Inject
  private Logger logger;

  @Inject
  private PanelController panelController;

  @Inject
  private QueryReplyController queryReplyController;
  
  @Inject
  private SpreadsheetReportBatchContext spreadsheetReportBatchContext;

  @Inject
  private SpreadsheetReportController spreadsheetReportController;
  
  @Inject
  @JobProperty
  private String baseUrl;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long stampId;

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;

  @Inject
  @JobProperty
  private Long[] queryReplyIds;
  
  @Inject
  @JobProperty
  private Long[] commentCategoryIds;
  
  @Override
  public void write(List<QueryPage> items) throws Exception {
    if (queryReplyIds == null) {
      return;
    }
    
    for (int i = 0; i < items.size(); i++) {
      writePage(items.get(i));
    }
  }
  
  private void writePage(QueryPage queryPage) throws Exception {
    PanelStamp stamp = panelController.findPanelStampById(stampId);
    if (stamp == null) {
      throw new ReportException(String.format("Could not find panel stamp %d", stampId));
    }
    
    logger.info("Processing query page {}", queryPage.getId());
    
    List<QueryReply> queryReplies = Arrays.stream(queryReplyIds).map(queryReplyController::findQueryReply).collect(Collectors.toList());
    SpreadsheetExportContext exportContext = new SpreadsheetExportContextImpl(locale, queryPage, stamp, queryReplies, commentCategoryIds, spreadsheetReportBatchContext::addColumn, spreadsheetReportBatchContext::setCellValue);
    
    spreadsheetReportController.exportQueryPageSpreadsheet(exportContext);
  }

}
