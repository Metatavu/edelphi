package fi.metatavu.edelphi.reports.spreadsheet;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.spreadsheet.batch.SpreadsheetCellValue;

/**
 * Implementation for spreadsheet context
 * 
 * @author Antti Leppä
 */
public class SpreadsheetExportContextImpl implements SpreadsheetExportContext {

  private Function<String, Integer> addColumn;
  private Consumer<SpreadsheetCellValue> setCellValue;
  private QueryPage queryPage;
  private List<QueryReply> queryReplies;
  private PanelStamp panelStamp;
  private Locale locale;

  /**
   * Constructor
   * 
   * @param locale locale
   * @param queryPage query page
   * @param panelStamp panel stamp 
   * @param queryReplies replies
   * @param addColumn add column function
   * @param setCellValue set cell value function
   */
  public SpreadsheetExportContextImpl(Locale locale, QueryPage queryPage, PanelStamp panelStamp, List<QueryReply> queryReplies, Function<String, Integer> addColumn, Consumer<SpreadsheetCellValue> setCellValue) {
    this.locale = locale;
    this.queryPage = queryPage;
    this.panelStamp = panelStamp;
    this.queryReplies = queryReplies;
    this.addColumn = addColumn;
    this.setCellValue = setCellValue;
  }
  
  /**
   * {@inheritDoc}
   */
  public QueryPage getQueryPage() {
    return queryPage;
  }

  /**
   * {@inheritDoc}
   */
  public PanelStamp getStamp() {
    return panelStamp;
  }

  /**
   * {@inheritDoc}
   */  
  public Locale getLocale() {
    return locale;
  }

  /**
   * {@inheritDoc}
   */
  public int addColumn(String columnName) {
    return addColumn.apply(columnName);
  }

  /**
   * {@inheritDoc}
   */
  public void setCellValue(QueryReply queryReply, int columnIndex, Object value) {
    this.setCellValue.accept(new SpreadsheetCellValue(queryReply, columnIndex, value));
  }

  /**
   * {@inheritDoc}
   */
  public List<QueryReply> getQueryReplies() {
    return queryReplies;
  }

  /**
   * {@inheritDoc}
   */
  public void setQueryReplies(List<QueryReply> queryReplies) {
    this.queryReplies = queryReplies;
  }

}