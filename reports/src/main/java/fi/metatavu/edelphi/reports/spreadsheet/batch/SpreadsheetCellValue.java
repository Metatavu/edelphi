package fi.metatavu.edelphi.reports.spreadsheet.batch;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;

/**
 * Single cell value in spreadsheet
 * 
 * @author Antti Leppä
 */
public class SpreadsheetCellValue {
  
  private QueryReply queryReply;
  private int columnIndex;
  private Object value;
  
  /**
   * Constructor
   * 
   * @param queryReply query reply
   * @param columnIndex column index
   * @param value value
   */
  public SpreadsheetCellValue(QueryReply queryReply, int columnIndex, Object value) {
    super();
    this.queryReply = queryReply;
    this.columnIndex = columnIndex;
    this.value = value;
  }

  /**
   * Returns query reply
   * 
   * @return query reply
   */
  public QueryReply getQueryReply() {
    return queryReply;
  }

  /**
   * Returns column index
   * 
   * @return column index
   */
  public int getColumnIndex() {
    return columnIndex;
  }

  /**
   * Returns value
   * 
   * @return value
   */
  public Object getValue() {
    return value;
  }
  


}
