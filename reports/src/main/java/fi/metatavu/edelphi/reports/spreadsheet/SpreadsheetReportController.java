package fi.metatavu.edelphi.reports.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;

import com.opencsv.CSVWriter;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.queries.QueryPageNumberComparator;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.export.ExpertiseQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.FormQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.GroupingQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.Live2dQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.MultiSelectQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.Multiple2DScalesQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.OrderingQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.QueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.Scale1DQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.Scale2DQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.TextQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.TimelineQueryPageSpreadsheetExporter;
import fi.metatavu.edelphi.reports.spreadsheet.export.TimeserieQueryPageSpreadsheetExporter;

/**
 * Controller for spreadsheet reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class SpreadsheetReportController {

  @Inject
  private Logger logger;

  @Inject
  private ReportMessages reportMessages; 
  
  @Inject
  private ExpertiseQueryPageSpreadsheetExporter expertiseQueryPageSpreadsheetExporter;
  
  @Inject
  private FormQueryPageSpreadsheetExporter formQueryPageSpreadsheetExporter;
  
  @Inject
  private GroupingQueryPageSpreadsheetExporter groupingQueryPageSpreadsheetExporter;
  
  @Inject
  private Multiple2DScalesQueryPageSpreadsheetExporter multiple2DScalesQueryPageSpreadsheetExporter;
  
  @Inject
  private MultiSelectQueryPageSpreadsheetExporter multiSelectQueryPageSpreadsheetExporter;
  
  @Inject
  private OrderingQueryPageSpreadsheetExporter orderingQueryPageSpreadsheetExporter;
  
  @Inject
  private Scale1DQueryPageSpreadsheetExporter scale1DQueryPageSpreadsheetExporter;
  
  @Inject
  private Scale2DQueryPageSpreadsheetExporter scale2DQueryPageSpreadsheetExporter;
  
  @Inject
  private TextQueryPageSpreadsheetExporter textQueryPageSpreadsheetExporter;
  
  @Inject
  private TimelineQueryPageSpreadsheetExporter timelineQueryPageSpreadsheetExporter;
  
  @Inject
  private TimeserieQueryPageSpreadsheetExporter timeserieQueryPageSpreadsheetExporter;

  @Inject
  private Live2dQueryPageSpreadsheetExporter live2dQueryPageSpreadsheetExporter;
  
  /**
   * Exports query page spreadsheet data
   * 
   * @param exportContext export context
   */
  public void exportQueryPageSpreadsheet(SpreadsheetExportContext exportContext) {
    QueryPageType pageType = exportContext.getQueryPage().getPageType();
    QueryPageSpreadsheetExporter spreadsheetExporter = getQueryPageSpreadsheetExporter(pageType);
    if (spreadsheetExporter != null) {
      spreadsheetExporter.exportSpreadsheet(exportContext);
    } else {
      logger.warn("Could not find a spreadsheetExporter for {}", pageType);
    }
  }
  
  /**
   * Exports data as CSV file
   * 
   * @param locale locale
   * @param replierExportStrategy replier export strategy
   * @param columns columns
   * @param rows rows
   * @return CSV data
   * @throws IOException thrown when CSV generator failes
   */
  public byte[] exportDataToCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<String> columns, Map<QueryReply, Map<Integer, Object>> rows) throws IOException {
    try (
      ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
      OutputStreamWriter streamWriter = new OutputStreamWriter(csvStream, Charset.forName("UTF-8"))) {
      CSVWriter csvWriter = new CSVWriter(streamWriter, ',');
      List<String> nextLine = new ArrayList<>();
      
      String replierExportStrategyLabel = getReplierExportStrategyLabel(locale, replierExportStrategy);
      if (replierExportStrategyLabel != null) {
        nextLine.add(replierExportStrategyLabel);
      }
      
      // Header
      for (String column : columns) {
        nextLine.add(column);
      }
      
      csvWriter.writeNext(nextLine.toArray(new String[0]));
      nextLine = new ArrayList<>();
      
      // Rows
      for (QueryReply queryReply : rows.keySet()) {
        switch (replierExportStrategy) {
          case NONE:
          break;
          case HASH:
            nextLine.add(queryReply.getUser() != null ? DigestUtils.md5Hex(String.valueOf(queryReply.getUser().getId())) : "-");
          break;
          case NAME:
            nextLine.add(queryReply.getUser() != null ? queryReply.getUser().getFullName(true, false) : "-");
          break;
          case EMAIL:
            nextLine.add(queryReply.getUser() != null ? queryReply.getUser().getDefaultEmailAsString() : "-");
          break;
        }
        
        Map<Integer, Object> columnValues = rows.get(queryReply);
  
        for (int columnIndex = 0, columnCount = columns.size(); columnIndex < columnCount; columnIndex++) {
          Object value = columnValues.get(columnIndex);
          if (value == null) {
            nextLine.add("");
          } else {
            if (value instanceof Number) {
              nextLine.add(String.valueOf(value));
            }
            else {
  
              // Convert cell value line breaks to spaces, as poor little Excel has trouble interpreting them correctly    
              // TODO This conversion could probably be avoided some way; OpenOffice/LibreOffice handle line breaks just fine  
              
              nextLine.add(String.valueOf(value).replace('\n', ' ').replace('\r', ' '));
            }
          }
        }
  
        csvWriter.writeNext(nextLine.toArray(new String[0]));
        nextLine = new ArrayList<>();
      }
  
      csvWriter.close();
  
      return csvStream.toByteArray();
    }
  }
  
  /**
   * Export query comments into CSV byte array
   * 
   * @param locale locale
   * @param replierExportStrategy replies export strategy
   * @param replies replies to be exported
   * @param query query
   * @param stamp stamp
   * @return query comments in CSV byte array
   * @throws IOException throws IOException when CSV writing fails
   */
  public byte[] exportCommentsAsCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, Query query, List<QueryPage> queryPages, PanelStamp stamp) throws IOException {
    Collections.sort(queryPages, new QueryPageNumberComparator());
    
    String[] columnHeaders = new String[] { 
      getReplierExportStrategyLabel(locale, replierExportStrategy), 
      reportMessages.getText(locale, "reports.spreadsheet.commentAnswerColumn"), 
      reportMessages.getText(locale, "reports.spreadsheet.commentCommentColumn"), 
      reportMessages.getText(locale, "reports.spreadsheet.commentReplyColumn"), 
    };
    
    List<String[]> rows = new ArrayList<>();
    
    for (QueryPage queryPage : queryPages) {
      List<String[]> pageRows = exportQueryPageCommentsAsCsv(replierExportStrategy, replies, stamp, queryPage);
      
      if (!pageRows.isEmpty()) {
        rows.add(new String[] { "", "", "", "" });
        rows.add(new String[] { queryPage.getTitle(), "", "", "" });
        rows.add(new String[] { "", "", "", "" });
        rows.addAll(pageRows);
      }
    }

    return writeCsv(columnHeaders, rows);
  }
  
  /**
   * Returns a query page spreadsheet exporter for given page type
   * 
   * @param pageType page type
   * @return query page spreadsheet exporter or null if not defined
   */
  private QueryPageSpreadsheetExporter getQueryPageSpreadsheetExporter(QueryPageType pageType) {
    switch (pageType) {
      case COLLAGE_2D:
        return null; // Does not support spreadsheet exports
      case EXPERTISE:
        return expertiseQueryPageSpreadsheetExporter;
      case FORM:
        return formQueryPageSpreadsheetExporter;
      case LIVE_2D:
        return live2dQueryPageSpreadsheetExporter;
      case TEXT:
        return textQueryPageSpreadsheetExporter;
      case THESIS_GROUPING:
        return groupingQueryPageSpreadsheetExporter;
      case THESIS_MULTIPLE_2D_SCALES:
        return multiple2DScalesQueryPageSpreadsheetExporter;
      case THESIS_MULTI_SELECT:
        return multiSelectQueryPageSpreadsheetExporter;
      case THESIS_ORDER:
        return orderingQueryPageSpreadsheetExporter;
      case THESIS_SCALE_1D:
        return scale1DQueryPageSpreadsheetExporter;
      case THESIS_SCALE_2D:
        return scale2DQueryPageSpreadsheetExporter;
      case THESIS_TIMELINE:
        return timelineQueryPageSpreadsheetExporter;
      case THESIS_TIME_SERIE:
        return timeserieQueryPageSpreadsheetExporter;
    }
    
    return null;
  }

  /**
   * Exports single query page into CSV rows
   * 
   * @param replierExportStrategy replier export strategy
   * @param replies replies to be exported
   * @param stamp stamp
   * @param queryPage query page to be exported
   * @return CSV rows
   */
  private List<String[]> exportQueryPageCommentsAsCsv(ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, PanelStamp stamp, QueryPage queryPage) {
    QueryPageSpreadsheetExporter spreadsheetExporter = getQueryPageSpreadsheetExporter(queryPage.getPageType());
    if (spreadsheetExporter != null) {
      ReportPageCommentProcessor processor = spreadsheetExporter.exportComments(queryPage, stamp, replies);
      if (processor != null) {
        return exportQueryPageCommentsAsCsv(replierExportStrategy, queryPage, processor);
      }
    }
    
    return Collections.emptyList();
  }

  /**
   * Exports single query page into CSV rows using comment processor
   * 
   * @param replierExportStrategy replier export strategy
   * @param queryPage query page to be exported
   * @param processor comment processor
   * @return CSV rows
   */
  private List<String[]> exportQueryPageCommentsAsCsv(ReplierExportStrategy replierExportStrategy, QueryPage queryPage, ReportPageCommentProcessor processor) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<String[]> rows = new ArrayList<>();

    processor.processComments();
    List<QueryQuestionComment> rootComments = processor.getRootComments();
    
    if (rootComments != null && !rootComments.isEmpty()) {
      Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
      
      for (QueryQuestionComment rootComment : rootComments) {
        String rootUser = getReplierExportStrategyValue(replierExportStrategy, rootComment.getQueryReply());
        String rootLabel = processor.getCommentLabel(rootComment.getId());
        rows.add(new String[] { rootUser, rootLabel, rootComment.getComment(), "" });
        List<QueryQuestionComment> childComments = childCommentMap.get(rootComment.getId());
        if (childComments != null) {
          childComments.forEach(childComment -> {
            String childUser = getReplierExportStrategyValue(replierExportStrategy, childComment.getQueryReply());
            rows.add(new String[] { childUser, "", "", childComment.getComment() });
          });
        }
      }
    }
    
    return rows;
  }
  
  /**
   * Returns label for given replier export strategy
   * 
   * @param locale locale
   * @param replierExportStrategy replier export strategy
   * @return label
   */
  private String getReplierExportStrategyLabel(Locale locale, ReplierExportStrategy replierExportStrategy) {
    switch (replierExportStrategy) {
      case NONE:
      break;
      case HASH:
        return reportMessages.getText(locale, "reports.spreadsheet.replierIdColumn");
      case NAME:
        return reportMessages.getText(locale, "reports.spreadsheet.replierNameColumn");
      case EMAIL:
        return reportMessages.getText(locale, "reports.spreadsheet.replierEmailColumn");
    }
    
    return null;
  }
  
  /**
   * Returns user identifier for given replier export strategy
   * 
   * @param replierExportStrategy replier export strategy
   * @param queryReply reply
   * @return user identifier
   */
  private String getReplierExportStrategyValue(ReplierExportStrategy replierExportStrategy, QueryReply queryReply) {
    if (queryReply != null) {
      User user = queryReply.getUser();
      if (user != null) {
        switch (replierExportStrategy) {
          case NONE:
          break;
          case HASH:
            return DigestUtils.md5Hex(String.valueOf(user.getId()));
          case NAME:
            return user.getFullName(true, false);
          case EMAIL:
            return user.getDefaultEmailAsString();
        }
      }
    }
    
    return "-";
  }
  
  /**
   * Writes a CSV file
   * 
   * @param columnHeaders headers
   * @param rows rows
   * @return CSV file
   * @throws IOException throws IOException when CSV writing fails
   */
  private byte[] writeCsv(String[] columnHeaders, List<String[]> rows) throws IOException {
    return writeCsv(columnHeaders, rows.stream().toArray(String[][]::new));
  }

  /**
   * Writes a CSV file
   * 
   * @param columnHeaders headers
   * @param rows rows
   * @return CSV file
   * @throws IOException throws IOException when CSV writing fails
   */
  private byte[] writeCsv(String[] columnHeaders, String[][] rows) throws IOException {
    try (ByteArrayOutputStream csvStream = new ByteArrayOutputStream(); OutputStreamWriter streamWriter = new OutputStreamWriter(csvStream, Charset.forName("UTF-8"))) {
      CSVWriter csvWriter = new CSVWriter(streamWriter, ',');

      csvWriter.writeNext(columnHeaders);
      
      for (String[] row : rows) {
        csvWriter.writeNext(row);
      }

      csvWriter.close();
      
      return csvStream.toByteArray();
    }
  }
  
}
