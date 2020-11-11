package fi.metatavu.edelphi.reports.text.legacy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.text.AbstractReportPageHtmlProvider;
import fi.metatavu.edelphi.reports.text.TextReportPageContext;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Text report HTML provider for legacy reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class LegacyReportPageHtmlProvider extends AbstractReportPageHtmlProvider {
  
  @Inject
  private Logger logger;
  
  @Inject
  private SettingsController settingsController;

  private int concurrent;
  
  @PostConstruct
  public void init() {
    concurrent = 0;
  }
  
  @Override
  public String getPageHtml(TextReportPageContext exportContext) throws ReportException {
    concurrent++;
    try { 
      logger.info("Concurrent legacy page downloads: {}", concurrent);
      return downloadPageHtml(exportContext);
    } finally {
      concurrent--;
    }
  }

  private String downloadPageHtml(TextReportPageContext exportContext) throws ReportException {
    try {
      String baseURL = exportContext.getBaseURL();
      QueryPage queryPage = exportContext.getPage();
      PanelStamp panelStamp = exportContext.getStamp();
      String serializedContext = getSerializedContext(exportContext);
      
      String internalAuthorizationHash = settingsController.getInternalAuthorizationHash();
  
      URL url = new URL(baseURL + "/panel/admin/report/page.page?chartFormat=PNG&pageId=" + queryPage.getId() + "&panelId=" + panelStamp.getPanel().getId() + "&serializedContext=" + serializedContext);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setInstanceFollowRedirects(true);
      connection.setRequestProperty("Authorization", "InternalAuthorization " + internalAuthorizationHash);
      connection.setRequestProperty("Accept-Language", exportContext.getLocale().getLanguage());
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      
      try (InputStream inputStream = connection.getInputStream()) {
        Document document = Jsoup.parse(inputStream, "UTF-8", baseURL);
        Elements reportPage = document.select("body > .reportPage");
        if (reportPage == null) {
          throw new ReportException("Could not find report page element from HTML");
        }
        
        return reportPage.outerHtml();
      }
      
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }

  private String getSerializedContext(TextReportPageContext exportContext) throws JsonProcessingException {
    LegacyReportContext reportContext = new LegacyReportContext(exportContext.getLocale().toString(), exportContext.getStamp().getId());
    
    if (exportContext.getExpertiseGroupIds() != null) {
      String filter = Arrays.stream(exportContext.getExpertiseGroupIds()).map(String::valueOf).collect(Collectors.joining(","));
      reportContext.addFilter("EXPERTISE", filter);
    }
    
    if (exportContext.getPanelUserGroupIds() != null) {
      String filter = Arrays.stream(exportContext.getPanelUserGroupIds()).map(String::valueOf).collect(Collectors.joining(","));
      reportContext.addFilter("USER_GROUPS", filter);
    }
    
    ObjectMapper objectMApper = new ObjectMapper();
    return Base64.encodeBase64URLSafeString(objectMApper.writeValueAsBytes(reportContext)); 
  }
  
  
}
