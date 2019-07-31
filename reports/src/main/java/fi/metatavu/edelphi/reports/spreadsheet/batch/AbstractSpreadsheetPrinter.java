package fi.metatavu.edelphi.reports.spreadsheet.batch;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.drive.AdminDrive;
import fi.metatavu.edelphi.reports.batch.AbstractPrinter;
import fi.metatavu.edelphi.reports.spreadsheet.ReplierExportStrategy;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetReportController;

/**
 * Abstract base class for spreadsheet printers
 * 
 * @author Antti Leppä
 */
public abstract class AbstractSpreadsheetPrinter extends AbstractPrinter {

  @Inject
  private Logger logger;
  
  @Inject
  private SpreadsheetReportBatchContext spreadsheetReportBatchContext;

  @Inject
  private SpreadsheetReportController spreadsheetReportController;

  @Inject
  @AdminDrive
  private GoogleCredential googleCredentialAdmin;

  @Inject
  @JobProperty
  private Long queryId;

  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private Long[] pageIds;

  @Inject
  @JobProperty
  private String deliveryEmail;

  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long[] expertiseGroupIds;
  
  /**
   * Export data as CSV
   * 
   * @return CSV data
   * @throws IOException thrown when exporting fails
   */
  protected byte[] exportCsvData() throws IOException {
    logger.info("Creating spreadsheet with {} rows", spreadsheetReportBatchContext.getRows().size());
    
    // TODO: ReplierExportStrategy
    ReplierExportStrategy replierExportStrategy = ReplierExportStrategy.HASH;
    return spreadsheetReportController.exportDataToCsv(locale, replierExportStrategy, spreadsheetReportBatchContext.getColumns(), spreadsheetReportBatchContext.getRows());    
  }
  
}
