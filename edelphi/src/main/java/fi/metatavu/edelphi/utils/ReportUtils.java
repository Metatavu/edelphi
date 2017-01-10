package fi.metatavu.edelphi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReportPageComment;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.smvcj.logging.Logging;

public class ReportUtils {
  
  public static void appendComments(QueryReportPage reportPage, QueryPage queryPage, ReportContext reportContext) {
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    PanelStamp panelStamp = panelStampDAO.findById(reportContext.getPanelStampId());
    
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
    List<QueryQuestionComment> comments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, panelStamp);
    for (QueryQuestionComment comment : comments) {
      // Root comments
      QueryReportPageComment reportComment = new QueryReportPageComment(comment.getQueryReply().getId(), comment.getComment(), comment.getLastModified());
      reportComment.setFiltered(!queryReplies.contains(comment.getQueryReply()));
      reportPage.addComment(reportComment);
      // Replies
      List<QueryQuestionComment> replies = queryQuestionCommentDAO.listByParentCommentAndArchived(comment, Boolean.FALSE);
      for (QueryQuestionComment reply : replies) {
        QueryReportPageComment commentReply = new QueryReportPageComment(reply.getQueryReply().getId(), reply.getComment(), reply.getLastModified());
        commentReply.setFiltered(!queryReplies.contains(reply.getQueryReply()));
        reportComment.addReply(commentReply);
      }
    }
  }

  public static List<QueryReply> getQueryReplies(QueryPage queryPage, ReportContext reportContext) {
    List<QueryReplyFilter> filters = QueryReplyFilter.parseFilters(reportContext.getFilters());
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelStamp panelStamp = panelStampDAO.findById(reportContext.getPanelStampId());
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(queryPage.getQuerySection().getQuery(), panelStamp);
    for (QueryReplyFilter filter : filters) {
      queryReplies = filter.filterList(queryReplies);
    }
    return queryReplies;
  }
  
  public static Map<Long, Long> getOptionListData(QueryField queryOptionField, List<QueryOptionFieldOption> queryFieldOptions, List<QueryReply> queryReplies) {
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    
    for (QueryOptionFieldOption queryFieldOption : queryFieldOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }
    
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryOptionField);
      
      if (answer != null) {
        Long v = listOptionAnswerCounts.get(answer.getOption().getId());
        listOptionAnswerCounts.put(answer.getOption().getId(), new Long(v.longValue() + 1));
      }
    }
    
    return listOptionAnswerCounts;
  }

  public static Map<Long, Long> getGroupData(QueryOptionField groupField, QueryOptionFieldOptionGroup group, List<QueryOptionFieldOption> groupOptions, List<QueryReply> queryReplies) {
    QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    for (QueryOptionFieldOption queryFieldOption : groupOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }
    for (QueryReply queryReply : queryReplies) {
      List<QueryQuestionOptionGroupOptionAnswer> groupAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, groupField, group);
      for (QueryQuestionOptionGroupOptionAnswer groupAnswer : groupAnswers) {
        Long v = listOptionAnswerCounts.get(groupAnswer.getOption().getId());
        listOptionAnswerCounts.put(groupAnswer.getOption().getId(), new Long(v.longValue() + 1));
      }
    }
    
    return listOptionAnswerCounts;
  }

  public static Map<Long, Long> getMultiselectData(QueryField queryMultiselectField, List<QueryOptionFieldOption> queryFieldOptions, List<QueryReply> queryReplies) {
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    
    for (QueryOptionFieldOption queryFieldOption : queryFieldOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }

    for (QueryReply queryReply : queryReplies) {
      QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryMultiselectField);

      if (answer != null) {
        Set<QueryOptionFieldOption> options = answer.getOptions();
        
        for (QueryOptionFieldOption option : options) {
          Long v = listOptionAnswerCounts.get(option.getId());
          listOptionAnswerCounts.put(option.getId(), new Long(v.longValue() + 1));
        }
      }
    }
    
    return listOptionAnswerCounts;
  }
  
  public static QueryFieldDataStatistics getOptionListStatistics(List<QueryOptionFieldOption> queryFieldOptions, Map<Long, Long> optionListData) {
    Map<Double, String> dataNames = new HashMap<Double, String>();
    List<Double> result = new ArrayList<Double>();
    
    for (int i = 0; i < queryFieldOptions.size(); i++) {
      QueryOptionFieldOption queryFieldOption = queryFieldOptions.get(i);

      dataNames.put(new Double(i), queryFieldOption.getText());
      
      Long value = optionListData.get(queryFieldOption.getId());
      
      if (value != null) {
        for (int j = 0; j < value.intValue(); j++) {
          // Add index of the option to the list
          result.add(new Double(i));
        }
      }
    }
    
    return getStatistics(result, dataNames);
  }
  
  public static QueryFieldDataStatistics getStatistics(List<Double> values, Map<Double, String> dataNames) {
    return new QueryFieldDataStatistics(values, dataNames);
  }
  
  public static List<Double> getNumberFieldData(QueryField numberField, List<QueryReply> queryReplies) {
    QueryQuestionNumericAnswerDAO questionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    List<Double> data = new ArrayList<Double>();

    for (QueryReply queryReply : queryReplies) {
      QueryQuestionNumericAnswer answer = questionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, numberField);

      if (answer != null) {
        data.add(answer.getData());
      }
    }

    return data;
  }
  
  public static Map<Double, Long> getClassifiedNumberFieldData(List<Double> data) {
    List<Double> temp = new ArrayList<Double>(data);
    Map<Double, Long> result = new HashMap<Double, Long>();
    
    while (temp.size() > 0) {
      Double v = temp.get(0);
      int c = 0;
      
      while (temp.remove(v))
        c++;
      
      result.put(v, new Long(c));
    }
    
    return result;
  }

  public static void zipCharts(RequestContext requestContext, OutputStream outputStream, String imageFormat, URL url) throws IOException, ParserConfigurationException, SAXException,
      TransformerException {

    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

    String hostUrl = new StringBuilder().append(url.getProtocol()).append("://").append(url.getHost()).append(':').append(url.getPort()).toString();

    // Read the url...

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
    connection.setRequestProperty("Accept-Language", requestContext.getRequest().getLocale().getLanguage());
    connection.setRequestMethod("GET");
    connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
    connection.connect();
    InputStream is = connection.getInputStream();
    
    String html = null;
    try {
      html = StreamUtils.readStreamToString(is, "UTF-8");
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

    // ...tidy it...

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
    tidy.parse(new StringReader(html), tidyXHtml);

    // ...parse it into a DOM...

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(false);
    builderFactory.setValidating(false);
    builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
    builderFactory.setFeature("http://xml.org/sax/features/validation", false);
    builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(tidyXHtml.toByteArray());
    Document document = builder.parse(inputStream);

    // ...and zip its chart images

    if ("SVG".equals(imageFormat)) {
      NodeList svgObjectList = XPathAPI.selectNodeList(document, "//object");
      for (int i = 0, l = svgObjectList.getLength(); i < l; i++) {
        Element svgObjectElement = (Element) svgObjectList.item(i);
        if ("image/svg+xml".equals(svgObjectElement.getAttribute("type"))) {
          String svgUri = svgObjectElement.getAttribute("data");
          if (StringUtils.startsWith(svgUri, "/")) {
            svgUri = hostUrl + svgUri;
          }
          
          byte[] svgContent = downloadUrlAsByteArray(svgUri);
          ZipEntry zipEntry = new ZipEntry(String.format("%03d", i + 1) + ".svg");
          zipOutputStream.putNextEntry(zipEntry);
          zipOutputStream.write(svgContent);
        }
      }
    }
    else {
      NodeList imgList = XPathAPI.selectNodeList(document, "//img");
      for (int i = 0, l = imgList.getLength(); i < l; i++) {
        Element imgElement = (Element) imgList.item(i);
        // TODO This assumes the image source is not relative
        String imgUrl = imgElement.getAttribute("src");
        if (StringUtils.startsWith(imgUrl, "/")) {
          imgUrl = hostUrl + imgUrl;
        }
        ZipEntry zipEntry = new ZipEntry(String.format("%03d", i + 1) + ".png");
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(downloadUrlAsByteArray(imgUrl));
      }
    }
    zipOutputStream.finish();
  }

  public static String uploadReportToGoogleDrive(RequestContext requestContext, Drive drive, URL url, String queryName, int retryCount, boolean imagesOnly) throws IOException,
      TransformerException, ParserConfigurationException, SAXException {
    Logging.logInfo("Exporting report into Google Drive from " + url);

    File exportTempFolder = GoogleDriveUtils.getFile(drive, GoogleDriveUtils.insertFolder(drive, queryName, "", null, 3));
    Set<File> tempFiles = new HashSet<>();
    try {
      // Resolve host URL to help with embedding of styles and images

      String hostUrl = new StringBuilder().append(url.getProtocol()).append("://").append(url.getHost()).append(':').append(url.getPort()).toString();

      // First we need to fetch report as html

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestProperty("Accept-Language", requestContext.getRequest().getLocale().getLanguage());
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      InputStream is = connection.getInputStream(); 

      String reportHtml = null;
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

      // .. tidy it a bit

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

      // Tidied document needs to be parsed into a document, so we can do some
      // changes into it
      Document reportDocument = builder.parse(inputStream);

      // Google Drive can not access our style sheets, so we need to embed them
      // directly into the document
      NodeList linkList = XPathAPI.selectNodeList(reportDocument, "//link");
      for (int i = 0, l = linkList.getLength(); i < l; i++) {
        Element linkElement = (Element) linkList.item(i);
        String linkRel = linkElement.getAttribute("rel");
        if (StringUtils.equalsIgnoreCase(linkRel, "stylesheet")) {
          String href = linkElement.getAttribute("href");
          Logging.logInfo("Embedding css from " + href + " into Google report");
          String cssText = CSSUtils.downloadCSS(hostUrl + href, true).replaceAll("[\n\r]", " ");

          Node parent = linkElement.getParentNode();
          Element styleElement = reportDocument.createElement("style");
          styleElement.setAttribute("type", "text/css");
          styleElement.appendChild(reportDocument.createTextNode(cssText));

          parent.replaceChild(styleElement, linkElement);
        }
      }

      if (!imagesOnly) {

        // After document has been altered to fit the purpose, we just serialize
        // it back to html

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(resultStream);
        transformer.transform(new DOMSource(reportDocument), streamResult);

        resultStream.flush();
        resultStream.close();

        // And upload the final product into Google Drive

        byte[] documentContent = resultStream.toByteArray();

        String fileId = GoogleDriveUtils.insertFile(drive, queryName, "", null, "text/html", documentContent, retryCount);

        Logging.logInfo("Report exported into Google Drive from " + url + " with id " + fileId);

        return fileId;
      } else {
        return exportTempFolder.getId();
      }
    } finally {
      if (!imagesOnly) {
        Logging.logInfo("Cleaning temporary files");

        for (File tempFile : tempFiles) {
          Logging.logInfo("Deleting export temp file " + tempFile.getId());
          GoogleDriveUtils.deleteFile(drive, tempFile);
        }

        Logging.logInfo("Deleting export temp folder " + exportTempFolder.getId());
        GoogleDriveUtils.deleteFile(drive, exportTempFolder);
      }
    }
  }

  private static byte[] downloadUrlAsByteArray(String urlString) throws IOException {
    if (StringUtils.startsWith(urlString,  "data:")) {
      int base64Index = urlString.indexOf("base64,");
      return Base64.decodeBase64(urlString.substring(base64Index + 7));
    }
    else {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      InputStream is = connection.getInputStream();
      byte[] urlContent = null;
      try {
        urlContent = StreamUtils.readStreamToByteArray(is);
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
      return urlContent;
    }
  }

}
