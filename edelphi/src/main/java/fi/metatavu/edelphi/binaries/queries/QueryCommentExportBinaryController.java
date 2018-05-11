package fi.metatavu.edelphi.binaries.queries;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils.ReplierExportStrategy;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class QueryCommentExportBinaryController extends BinaryController {

  private static Logger logger = Logger.getLogger(QueryCommentExportBinaryController.class.getName());

  @Override
  public void process(BinaryRequestContext requestContext) {
    QueryDAO queryDAO = new QueryDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    
    Long queryId = requestContext.getLong("queryId");
    Long stampId = requestContext.getLong("stampId");

    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format"));
    if (format == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, "Missing parmeter format");
    }

    ReportContext reportContext = null;
    String serializedContext = requestContext.getString("serializedContext");
    if (serializedContext != null) {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] serializedData = Base64.decodeBase64(serializedContext);
        String stringifiedData = new String(serializedData, "UTF-8");
        reportContext = objectMapper.readValue(stringifiedData, ReportContext.class); 
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Failed to create serialized context", e);
      }
    }
    
    Query query = queryDAO.findById(queryId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);

    // Query replies, possibly filtered
        
    List<QueryReply> replies = queryReplyDAO.listByQueryAndStampAndArchived(query, panelStamp, Boolean.FALSE);
    List<QueryReplyFilter> filters = reportContext == null ? null : QueryReplyFilter.parseFilters(reportContext.getFilters());
    if (filters != null) {
      for (QueryReplyFilter filter : filters) {
        replies = filter.filterList(replies);
      }
    }
    
    if (format == ExportFormat.CSV) {
      exportCsv(requestContext, replies, query, panelStamp);
    }
  }

  private void exportCsv(BinaryRequestContext requestContext, List<QueryReply> replies, Query query, PanelStamp panelStamp) {
    try {
      byte[] csvData = QueryDataUtils.exportQueryCommentsAsCsv(requestContext.getRequest().getLocale(), ReplierExportStrategy.HASH, replies, query, panelStamp);
      
      // Add UTF-8 preamble bytes so that poor little Excel realizes this is UTF-8 data (as usual, OpenOffice/LibreOffice figure it out automatically)
      
      byte[] preamble = "\uFEFF".getBytes("UTF-8");
      byte[] combined = new byte[preamble.length + csvData.length];
      System.arraycopy(preamble, 0, combined, 0, preamble.length);
      System.arraycopy(csvData, 0, combined, preamble.length, csvData.length);
      csvData = combined;
      
      requestContext.setResponseContent(csvData, "text/csv");
      requestContext.setFileName(ResourceUtils.getUrlName(query.getName()) + ".csv");
    } catch (IOException e) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
    }

  }

  private enum ExportFormat {
    CSV
  }
  
}
