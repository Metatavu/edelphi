package fi.metatavu.edelphi.reports.text;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Text report page context
 * 
 * @author Antti Leppä
 */
public class TextReportPageContext {

  private String baseURL;
  private Locale locale;
  private PanelStamp stamp;
  private Map<String, List<String>> filters;
  private Map<String, String> parameters;
  private QueryPage page;
  
  public TextReportPageContext() {
    super();
  }

  public TextReportPageContext(String baseURL, Locale locale, PanelStamp stamp, Map<String, List<String>> filters, Map<String, String> parameters, QueryPage page) {
    super();
    this.baseURL = baseURL;
    this.locale = locale;
    this.stamp = stamp;
    this.filters = filters;
    this.parameters = parameters;
    this.page = page;
  }

  public String getBaseURL() {
    return baseURL;
  }

  public void setBaseURL(String baseURL) {
    this.baseURL = baseURL;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public PanelStamp getStamp() {
    return stamp;
  }

  public void setStamp(PanelStamp stamp) {
    this.stamp = stamp;
  }

  public Map<String, List<String>> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, List<String>> filters) {
    this.filters = filters;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public QueryPage getPage() {
    return page;
  }

  public void setPage(QueryPage page) {
    this.page = page;
  }

}
