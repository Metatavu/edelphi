package fi.metatavu.edelphi.pages.panel.admin.report.util;

import java.util.List;

public class QueryReportPageThesisMultipleScale2D extends QueryReportPage {

  private List<String> theses;
  
  public QueryReportPageThesisMultipleScale2D(Long queryPageId, String title, String jspFile) {
    super(queryPageId, title, jspFile);
  }
  
  public List<String> getTheses() {
    return theses;
  }
  
  public void setTheses(List<String> theses) {
    this.theses = theses;
  }
  
}
