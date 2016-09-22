package fi.metatavu.edelphi.binaries.queries;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.BinaryRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.binaries.BinaryController;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.metatavu.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.metatavu.edelphi.utils.GoogleDriveUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils.ReplierExportStrategy;

public class QueryPageDataExportBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    Long queryPageId = requestContext.getLong("queryPageId");
    Long stampId = requestContext.getLong("stampId");
    String replierExportStrategyParam = requestContext.getString("replierExportStrategy");

    ReportContext reportContext = null;
    String serializedContext = requestContext.getString("serializedContext");
    if (serializedContext != null) {
      try {
        ObjectMapper om = new ObjectMapper();
        byte[] serializedData = Base64.decodeBase64(serializedContext);
        String stringifiedData = new String(serializedData, "UTF-8");
        reportContext = om.readValue(stringifiedData, ReportContext.class); 
      }
      catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);
    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format")); 
    ReplierExportStrategy replierExportStrategy = StringUtils.isNotBlank(replierExportStrategyParam) ? ReplierExportStrategy.valueOf(replierExportStrategyParam) : ReplierExportStrategy.NONE;

    // Query replies, possibly filtered
    
    List<QueryReply> replies = queryReplyDAO.listByQueryAndStampAndArchived(queryPage.getQuerySection().getQuery(), panelStamp, Boolean.FALSE);
    List<QueryReplyFilter> filters = reportContext == null ? null : QueryReplyFilter.parseFilters(reportContext.getFilters());
    if (filters != null) {
      for (QueryReplyFilter filter : filters) {
        replies = filter.filterList(replies);
      }
    }
    
    switch (format) {
      case CSV:
        exportCsv(requestContext, replierExportStrategy, replies, queryPage, panelStamp);
      break;
      case GOOGLE_SPREADSHEET:
        exportGoogleSpreadsheet(requestContext, replierExportStrategy, replies, queryPage, panelStamp);
      break;
    }
  }
  
  private void exportCsv(BinaryRequestContext requestContext, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) {
    try {
      byte[] csvData = QueryDataUtils.exportQueryPageDataAsCsv(requestContext.getRequest().getLocale(), replierExportStrategy, replies, queryPage, panelStamp);
      requestContext.setResponseContent(csvData, "text/csv");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".csv");
    } catch (IOException e) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
    }
     
  }
  
  private void exportGoogleSpreadsheet(RequestContext requestContext, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) {
		Drive drive = GoogleDriveUtils.getAuthenticatedService(requestContext);
		if (drive != null) {
  		try {
  		  String title = queryPage.getQuerySection().getQuery().getName() + " - " + queryPage.getTitle();
  			byte[] csvData = QueryDataUtils.exportQueryPageDataAsCsv(requestContext.getRequest().getLocale(), replierExportStrategy, replies, queryPage, panelStamp);
  			File file = GoogleDriveUtils.insertFile(drive, title, "", null, "text/csv", csvData, 3);
  			String fileUrl = GoogleDriveUtils.getFileUrl(drive, file);
  			if (fileUrl != null) {
  			  requestContext.setRedirectURL(fileUrl);
  			}
  		} catch (Exception e) {
        Messages messages = Messages.getInstance();
        Locale locale = requestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
      }
		}
  }

  private enum ExportFormat {
    CSV,
    GOOGLE_SPREADSHEET
  }
}
