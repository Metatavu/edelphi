package fi.metatavu.edelphi.reports.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.reports.ReportException;
import fi.metatavu.edelphi.reports.text.legacy.LegacyReportContext;
import fi.metatavu.edelphi.settings.SettingsController;

/**
 * Report chart image provider for legacy reports
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class LegacyReportPageChartImageProvider extends AbstractReportPageChartImageProvider {
  
  @Inject
  private SettingsController settingsController;
  
  @Override
  public byte[] getPng(ImageReportPageContext exportContext) throws ReportException {
    try {
      String baseUrl = exportContext.getBaseURL();
      QueryPage queryPage = exportContext.getPage();
      PanelStamp panelStamp = exportContext.getStamp();
      String serializedContext = getSerializedContext(exportContext);
      String internalAuthorizationHash = settingsController.getInternalAuthorizationHash();
  
      URL url = new URL(baseUrl + "/panel/admin/report/page.page?chartFormat=PNG&pageId=" + queryPage.getId() + "&panelId=" + panelStamp.getPanel().getId() + "&serializedContext=" + serializedContext);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Authorization", "InternalAuthorization " + internalAuthorizationHash);
      connection.setRequestProperty("Accept-Language", exportContext.getLocale().getLanguage());
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      
      try (InputStream inputStream = connection.getInputStream()) {
        Document document = Jsoup.parse(inputStream, "UTF-8", baseUrl);
        Elements reportPage = document.select("body > .reportPage");
        if (reportPage == null) {
          throw new ReportException("Could not find report page element from HTML");
        }
        
        Elements reportImages = reportPage.select("img.report-image");
        if (!reportImages.isEmpty()) {
          return downloadUrlAsByteArray(baseUrl, reportImages.get(0).attr("src"), internalAuthorizationHash);
        }
        
        return null;
      }
      
    } catch (IOException e) {
      throw new ReportException(e);
    }
  }
  
  /**
   * Downloads URL as byte array
   * 
   * @param urlString URL
   * @param internalAuthorizationHash internal authorization hash
   * @return URL as byte array
   * @throws IOException thrown on download failure
   */
  private byte[] downloadUrlAsByteArray(String baseUrl, String urlString, String internalAuthorizationHash) throws IOException {
    if (StringUtils.startsWith(urlString,  "data:")) {
      int base64Index = urlString.indexOf("base64,");
      return Base64.decodeBase64(urlString.substring(base64Index + 7));
    } else {
      URL url = URI.create(baseUrl).resolve(urlString).toURL();
      
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      try {
        connection.setRequestProperty("Authorization", "InternalAuthorization " + internalAuthorizationHash);
        connection.setRequestMethod("GET");
        connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
        connection.connect();
        
        try (InputStream is = connection.getInputStream()) {
          return IOUtils.toByteArray(is);
        }
      } finally {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns serialized context for legacy reports
   * 
   * @param exportContext export context
   * @return serialized context for legacy reports
   * @throws JsonProcessingException thrown when JSON serialization fails
   */
  private String getSerializedContext(ImageReportPageContext exportContext) throws JsonProcessingException {
    LegacyReportContext reportContext = new LegacyReportContext(exportContext.getLocale().toString(), exportContext.getStamp().getId());
    
    if (exportContext.getExpertiseGroupIds() != null) {
      String filter = Arrays.stream(exportContext.getExpertiseGroupIds()).map(String::valueOf).collect(Collectors.joining(","));
      reportContext.addFilter("EXPERTISE", filter);
    }
    
    ObjectMapper objectMApper = new ObjectMapper();
    return Base64.encodeBase64URLSafeString(objectMApper.writeValueAsBytes(reportContext)); 
  }
  
  
}
