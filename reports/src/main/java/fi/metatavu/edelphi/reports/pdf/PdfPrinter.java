package fi.metatavu.edelphi.reports.pdf ;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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

  /**
   * Renders HTML stream as PDF stream
   * 
   * @param htmlStream HTMLstream
   * @param pdfStream PDF stream
   * @throws PdfRenderException error thrown on unsuccessful render
   */
  public void printHtmlAsPdf(InputStream htmlStream, OutputStream pdfStream) throws PdfRenderException {
    try {
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document inputDoc = builder.parse(htmlStream);
      
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument(inputDoc, "");
      renderer.layout();
      renderer.createPDF(pdfStream);

    } catch (IOException | DocumentException | SAXException | ParserConfigurationException e) {
      throw new PdfRenderException("Pdf rendering failed", e);
    }
  }
  
}