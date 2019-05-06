package fi.metatavu.edelphi.binaries.queries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.Drive;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.utils.B64ImgReplacedElementFactory;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.StreamUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

public class ExportReportPageBinaryController extends BinaryController {

  private static Logger logger = Logger.getLogger(ExportReportPageBinaryController.class.getName());

  @Override
  public void process(BinaryRequestContext requestContext) {
    Long queryPageId = requestContext.getLong("queryPageId");
    Long stampId = requestContext.getLong("stampId");
    String serializedContext = requestContext.getString("serializedContext");
    if (serializedContext == null) {
      ReportContext reportContext = new ReportContext(requestContext.getRequest().getLocale().toString(), stampId);
      try {
        ObjectMapper om = new ObjectMapper();
        serializedContext = Base64.encodeBase64URLSafeString(om.writeValueAsBytes(reportContext)); 
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Failed to create serialized context", e);
      }
    }
    
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();

    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);
    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format"));

    switch (format) {
    case GOOGLE_DOCUMENT:
      exportGoogleDocument(requestContext, queryPage, panelStamp, false, serializedContext);
      break;
    case GOOGLE_IMAGES:
      exportGoogleDocument(requestContext, queryPage, panelStamp, true, serializedContext);
      break;
    case PDF:
      exportPdf(requestContext, queryPage, panelStamp, serializedContext);
      break;
    case PNG_ZIP:
      exportChartZip(requestContext, "PNG", queryPage, panelStamp, serializedContext);
      break;
    case SVG_ZIP:
      exportChartZip(requestContext, "SVG", queryPage, panelStamp, serializedContext);
      break;
    }
  }

  public void exportGoogleDocument(RequestContext requestContext, QueryPage queryPage, PanelStamp panelStamp, boolean imagesOnly, String serializedContext) {
    Drive drive = GoogleDriveUtils.getAuthenticatedService(requestContext);
    if (drive != null) {
      try {
        String baseUrl = RequestUtils.getBaseUrl(requestContext.getRequest());
        String chartFormat = imagesOnly ? "SVG" : "PNG";
        URL url = new URL(String.format("%s/panel/admin/report/page.page?chartFormat=%s&pageId=%d&panelId=%d&serializedContext=%s", baseUrl, chartFormat, queryPage.getId(), panelStamp.getPanel().getId(), serializedContext));
        Locale locale = requestContext.getRequest().getLocale();
        String title = queryPage.getQuerySection().getQuery().getName() + " - " + queryPage.getTitle();

        if (imagesOnly) {
          String fileId = ReportUtils.uploadReportImagesToGoogleDrive(locale, drive, url, title, 3);
          requestContext.setRedirectURL(GoogleDriveUtils.getWebViewLink(drive, fileId));
        } else {
          String fileId = ReportUtils.uploadReportToGoogleDrive(URI.create(baseUrl), locale, drive, url, title, 3);
          requestContext.setRedirectURL(GoogleDriveUtils.getWebViewLink(drive, fileId));
        }
        
      } catch (TransformerException | SAXException | ParserConfigurationException | IOException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORT_GOOGLE_DRIVE_EXPORT_FAILED, "exception.1031.reportGoogleDriveExportFailed", e);
      }
    }
  }

  public void exportPdf(BinaryRequestContext requestContext, QueryPage queryPage, PanelStamp panelStamp, String serializedContext) {
    try {
      String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());

      URL url = new URL(baseURL + "/panel/admin/report/page.page?chartFormat=PNG&pageId=" + queryPage.getId() + "&panelId=" + panelStamp.getPanel().getId() + "&serializedContext=" + serializedContext);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestProperty("Accept-Language", requestContext.getRequest().getLocale().getLanguage());
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      InputStream is = connection.getInputStream();
      
      String reportHtml= null;
      try {
        reportHtml = StreamUtils.readStreamToString(is, "UTF-8");
      }
      finally {
        if (is != null) {
          try {
            is.close();
          }
          catch (IOException ioe) {
            Logging.logException(ioe);
          }
        }
        connection.disconnect();
      }

      ByteArrayOutputStream tidyXHtml = new ByteArrayOutputStream();
      Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);
      tidy.setWraplen(0);
      tidy.setQuoteNbsp(false);
      tidy.parse(new StringReader(reportHtml), tidyXHtml);

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      ByteArrayInputStream inputStream = new ByteArrayInputStream(tidyXHtml.toByteArray());
      Document doc = builder.parse(inputStream);
      ITextRenderer renderer = new ITextRenderer();
      SharedContext sharedContext = renderer.getSharedContext();
      sharedContext.setReplacedElementFactory(new B64ImgReplacedElementFactory());

      renderer.setDocument(doc, baseURL + "/panel/admin/report/query/");
      renderer.layout();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      renderer.createPDF(outputStream);
      outputStream.close();

      requestContext.setResponseContent(outputStream.toByteArray(), "application/pdf");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".pdf");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to export PDF", e);
    }
  }

  public void exportChartZip(BinaryRequestContext requestContext, String imageFormat, QueryPage queryPage, PanelStamp panelStamp, String serializedContext) {
    try {
      String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());
      URL url = new URL(baseURL + "/panel/admin/report/page.page?chartFormat=" + imageFormat + "&pageId=" + queryPage.getId()
          + "&panelId=" + panelStamp.getPanel().getId() + "&serializedContext=" + serializedContext);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
        ReportUtils.zipCharts(requestContext, outputStream, imageFormat, url);
      }
      finally {
        outputStream.close();
      }
      requestContext.setResponseContent(outputStream.toByteArray(), "application/zip");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".zip");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to export chart as zip", e);
    }
  }

  private enum ExportFormat {
    PDF, GOOGLE_DOCUMENT, PNG_ZIP, SVG_ZIP, GOOGLE_IMAGES
  }
}
