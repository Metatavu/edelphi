package fi.metatavu.edelphi.reports.spreadsheet;

import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Interface describing a spreadsheet export context
 * 
 * @author Antti Leppä
 */
public interface SpreadsheetExportContext {

  /**
   * Returns locale
   * 
   * @return locale
   */
  public Locale getLocale();
  
  /**
   * Returns query page
   * 
   * @return query page
   */
  public QueryPage getQueryPage();
  
  /**
   * Returns query reply list
   * 
   * @return query reply list
   */
  public List<QueryReply> getQueryReplies();
  
  /**
   * Returns panel stamp
   * 
   * @return panel stamp
   */
  public PanelStamp getStamp();
  
  /**
   * Returns comment category ids
   *  
   * @return comment category ids
   */
  public Long[] getCommentCategoryIds();

  /**
   * Adds a column into spreadsheet
   * 
   * @param columnName column name
   * @return column index
   */
  public int addColumn(String columnName);

  /**
   * Sets a cell value
   * 
   * @param queryReply query reply
   * @param columnIndex column index
   * @param value cell value
   */
  public void setCellValue(QueryReply queryReply, int columnIndex, Object value);
  
}
