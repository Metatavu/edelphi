package fi.metatavu.edelphi.reports.pdf ;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

/**
 * PDF Printer
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class PdfPrinter {
  
  @Inject
  private Logger logger;

  /**
   * Renders HTML stream as PDF stream
   * 
   * @param htmlStream HTMLstream
   * @param pdfStream PDF stream
   * @throws PdfRenderException error thrown on unsuccessful render
   */
  public void printHtmlAsPdf(InputStream htmlStream, OutputStream pdfStream) throws PdfRenderException {
    try {
      byte[] htmlBytes = cleanHtmlAttrs(IOUtils.toByteArray(htmlStream));
      
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument(parseDocument(htmlBytes), "");
      renderer.layout();
      renderer.createPDF(pdfStream);

    } catch (IOException | DocumentException | ParserConfigurationException e) {
      throw new PdfRenderException("Pdf rendering failed", e);
    }
  }

  /**
   * Parses HTML document using either normal DOM parser or a fallback tidy parser
   * 
   * @param htmlBytes HTML bytes
   * @return document
   * @throws ParserConfigurationException thrown when parser is configured incorrectly
   * @throws IOException thrown when stream reading fails
   */
  private Document parseDocument(byte[] htmlBytes) throws ParserConfigurationException, IOException {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlBytes)) {
      return parseDomDocument(inputStream);
    } catch (SAXException e) {
      logger.warn("Failed to parse html", e);
      return parseTidyDocument(htmlBytes);
    }
  }
  
  /**
   * Parses document using normal DOM parser
   * 
   * @param htmlStream HTML stream
   * @return document
   * @throws ParserConfigurationException thrown when parser is configured incorrectly
   * @throws IOException thrown when stream reading fails
   * @throws SAXException thrown when content can not be parsed by this parser
   */
  private Document parseDomDocument(InputStream htmlStream) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(htmlStream);
  }

  private Document parseTidyDocument(byte[] htmlBytes) throws ParserConfigurationException, IOException {
    try (ByteArrayInputStream tidyStream = new ByteArrayInputStream(tidyHtml(htmlBytes))) {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      return builder.parse(tidyStream);
    } catch (SAXException e1) {
      throw new IOException(e1);
    }
  }
  
  /**
   * Attempts to tidy up invalid HTML
   * 
   * @param untidy untidy HTML
   * @return tidy HTML
   * @throws IOException thrown when tidying fails
   */
  private byte[] tidyHtml(byte[] untidy) throws IOException {
    try (ByteArrayOutputStream tidyXHtml = new ByteArrayOutputStream()) {
      Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(false);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);
      tidy.setWraplen(0);
      tidy.setQuoteNbsp(false);
      tidy.parse(new StringReader(new String(untidy, StandardCharsets.UTF_8)), tidyXHtml);
      byte[] result = tidyXHtml.toByteArray();
      
      logger.info(String.format("Tidyied HTML %s", new String(result, StandardCharsets.UTF_8)));
      
      return result;
    }
  }
  
  /**
   * Cleans strange attributes from HTML code
   * @param html HTML code
   * @return clean HTML code
   */
  private byte[] cleanHtmlAttrs(byte[] html) {
    if (html == null) {
      return new byte[0];
    }
    
    String regex = "([A-Za-z-]{1,}\\:\\=\\\"\")";
    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(new String(html, StandardCharsets.UTF_8));
    String result = matcher.replaceAll("");
    
    logger.info(String.format("Cleaned HTML %s", result));
    
    return result.getBytes(StandardCharsets.UTF_8);
  }
  
}