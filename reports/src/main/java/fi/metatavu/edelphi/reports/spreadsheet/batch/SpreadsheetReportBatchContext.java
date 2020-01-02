package fi.metatavu.edelphi.reports.spreadsheet.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jberet.cdi.JobScoped;

import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;

/**
 * Batch context for spreadsheet reports
 * 
 * @author Antti Leppä
 */
@JobScoped
public class SpreadsheetReportBatchContext {
  
  private Map<Long, Map<Integer, Object>> rows;
  private List<String> columns;
  
  /**
   * Post constructor method
   */
  @PostConstruct
  public void init() {
    columns = new ArrayList<>();
    rows = new HashMap<>();
  }
  
  /**
   * Adds a column into spreadsheet
   * 
   * @param columnName column name
   * @return column index
   */
  public int addColumn(String columnName) {
    int index = columns.size();
    columns.add(columnName);
    return index;
  }
  
  /**
   * Sets a cell value
   * 
   * @param cellValue cell value
   */
  public void setCellValue(SpreadsheetCellValue cellValue) {
    QueryReply queryReply = cellValue.getQueryReply();
    int columnIndex = cellValue.getColumnIndex();
    Object value = cellValue.getValue();
    
    Map<Integer, Object> columnValues = rows.get(queryReply.getId());
    if (columnValues == null) {
      columnValues = new HashMap<Integer, Object>();
      rows.put(queryReply.getId(), columnValues);
    }
    
    columnValues.put(columnIndex, value);
  }

  /**
   * Returns columns
   * 
   * @return columns
   */
  public List<String> getColumns() {
    return columns;
  }
  
  /**
   * Sets columns
   * 
   * @param columns columns
   */
  public void setColumns(List<String> columns) {
    this.columns = columns;
  }
  
  /**
   * Returns rows
   * 
   * @return rows
   */
  public Map<Long, Map<Integer, Object>> getRows() {
    return rows;
  }
  
  /**
   * Sets rows
   * 
   * @param rows rows
   */
  public void setRows(Map<Long, Map<Integer, Object>> rows) {
    this.rows = rows;
  }
  
}
