package fi.metatavu.edelphi.reports.text.batch;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.TypedItemWriter;

/**
 * Batch item writer for writing report page HTMLs
 * 
 * @author Antti Leppä
 */
@Named
@Stateless
@AccessTimeout (unit = TimeUnit.HOURS, value = 4)
public class TextReportPageHtmlWriter extends TypedItemWriter<String> {
  
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
    for (String pageHtml : pageHtmls) {
      reportHtmlBatchContext.addPageHtml(processHtml(pageHtml));
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
    URL url = URI.create(baseUrl).resolve(uri).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.connect();
    
    try (InputStream stream = connection.getInputStream()) {
      byte[] data = IOUtils.toByteArray(stream);
      MimeType mimeType = new MimeType(connection.getContentType());
      StringBuilder dataUrlBuilder = new StringBuilder();
      dataUrlBuilder.append("data:");
      dataUrlBuilder.append(mimeType.getBaseType());
      dataUrlBuilder.append(";base64,");
      dataUrlBuilder.append(Base64.encodeBase64String(data));
      return dataUrlBuilder.toString();
    }
  }

}
