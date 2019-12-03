package fi.metatavu.edelphi.reports.text.batch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;

/**
 * Batch item writer for writing report page HTMLs
 * 
 * @author Antti Leppä
 */
@Named
public class TextReportPageHtmlWriter extends TypedItemWriter<String> {

  @Inject
  private Logger logger;
  
  @Inject
  private TextReportBatchContext reportHtmlBatchContext;
  
  @Inject
  @JobProperty
  private String baseUrl;
  
  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private Long stampId;
  
  @Override
  public void write(List<String> pageHtmls) throws Exception {
    logger.info("Processing {} report html pages", pageHtmls.size());
    
    for (String pageHtml : pageHtmls) {
      try {
        reportHtmlBatchContext.addPageHtml(processHtml(pageHtml));
      } catch (Exception e) {
        logger.error("Failed to process report HTML", e);
      }
    }
  }

  /**
   * Preprocesses HTML to be suitable for printing
   * 
   * @param html HTML code
   * @return download result as data URL
   * @throws IOException thrown on download failure
   * @throws MimeTypeParseException thrown when downloaded file had invalid mime type
   */
  private String processHtml(String html) throws IOException, MimeTypeParseException {
    Document document = Jsoup.parse(html);
    
    for (Element element : document.select("img")) {
      String src = element.attr("src");
      if (!StringUtils.startsWith(src, "data:")) {
        String dataUri = downloadAsDataUrl(URI.create(src));
        if (StringUtils.isNotBlank(dataUri)) {
          element.attr("src", dataUri);
        }
      }      
    }
    
    return document.html();
  }

  /**
   * Downloads binary and returns it as data URL
   * 
   * @param uri URI
   * @return download result as data URL
   * @throws IOException thrown on download failure
   * @throws MimeTypeParseException thrown when downloaded file had invalid mime type
   */
  private String downloadAsDataUrl(URI uri) throws IOException, MimeTypeParseException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(URI.create(baseUrl).resolve(uri));
      
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        try (InputStream stream = response.getEntity().getContent()) {
          byte[] data = IOUtils.toByteArray(stream);
          MimeType mimeType = new MimeType(response.getEntity().getContentType().getValue());
          StringBuilder dataUrlBuilder = new StringBuilder();
          dataUrlBuilder.append("data:");
          dataUrlBuilder.append(mimeType.getBaseType());
          dataUrlBuilder.append(";base64,");
          dataUrlBuilder.append(Base64.encodeBase64String(data));
          return dataUrlBuilder.toString();
        }
      }
    } catch (Exception e) {
      logger.error("Failed to download URL", e);
    }

    return null;
  }

}
