package fi.metatavu.edelphi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

public class CSVRenderer {
  
  private static final Logger logger = Logger.getLogger(CSVRenderer.class.getName());

  private String tableClass;
  private InputStream data;
  private boolean headers;
  
  public CSVRenderer(InputStream data, String tableClass, boolean headers) {
    this.data = data;
    this.tableClass = tableClass;
    this.headers = headers;
  }
  
  public String renderHtmlTable() {
    StringBuilder result = new StringBuilder();
    
    try (InputStreamReader streamReader = new InputStreamReader(data)) {
      try (CSVReader queryDataReader = new CSVReader(streamReader, ',')) {
        List<String[]> rows = queryDataReader.readAll();
        
        result.append(String.format("<table class=\"%s\">", this.tableClass));
        
        if (headers) {
          printHeaderRow(result, getColumnCount(rows));
        }
        
        for (int rowNumber = 0; rowNumber < rows.size(); rowNumber++) {
          String[] row = rows.get(rowNumber);
          printRow(result, row, rowNumber); 
        }
        
        result.append("</table>");
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read CSV file", e);
      return null;
    }
    
    return result.toString();
  }
  
  private void printHeaderRow(StringBuilder result, int columns) {
    result.append("<tr>");
    result.append("<th/>");
    for (int column = 0; column < columns; column++) {
      result.append(String.format("<th>%s</th>", toAlphabetic(column)));
    }
    result.append("</tr>");
  }
  
  private void printRow(StringBuilder result, String[] row, int rowNumber) {
    result.append("<tr>");
    
    if (headers) {
      result.append(String.format("<td class=\"header\">%d</td>", rowNumber));
    }
    
    result.append(renderCells(row));
    result.append("</tr>");
  }
  
  private String renderCells(String[] cells) {
    StringBuilder result = new StringBuilder();
    
    for (String cell : cells) {
      result.append(String.format("<td>%s</td>", cell));
    }
    
    return result.toString();
  }
  
  private int getColumnCount(List<String[]> rows) {
    int result = 0;
    
    for (String[] row : rows) {
      result = Math.max(result, row.length);
    }
    
    return result;
  }
  
  private String toAlphabetic(int i) {
    // thanks to Quantum7 (http://stackoverflow.com/questions/10813154/converting-number-to-letter)
    int quot = i / 26;
    int rem = i % 26;
    char letter = (char)((int)'A' + rem);
    if (quot == 0) {
      return String.valueOf(letter);
    } else {
      return toAlphabetic(quot - 1) + letter;
    }
  }
  
}
