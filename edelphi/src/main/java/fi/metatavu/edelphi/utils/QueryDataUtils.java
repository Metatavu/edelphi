package fi.metatavu.edelphi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.opencsv.CSVWriter;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.StatusCode;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.QueryExportContextImpl;
import fi.metatavu.edelphi.query.QueryPageHandler;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;

public class QueryDataUtils {

  public static Object getQueryQuestionAnswer(QueryQuestionAnswer queryQuestionAnswer) {
    // TODO: Test with byte coded entities

    switch (queryQuestionAnswer.getQueryField().getType()) {
    case NUMERIC:
    case NUMERIC_SCALE:
      return ((QueryQuestionNumericAnswer) queryQuestionAnswer).getData();
    case OPTIONFIELD:
      QueryOptionFieldOption option = ((QueryQuestionOptionAnswer) queryQuestionAnswer).getOption();
      return option != null ? option.getValue() : null;
    case TEXT:
      return ((QueryQuestionTextAnswer) queryQuestionAnswer).getData();
    default:
      throw new SmvcRuntimeException(StatusCode.UNDEFINED, "Unrecognized query field type: " + queryQuestionAnswer.getQueryField().getType());
    }

  }
  
  public static Long getQueryReplyId(HttpSession session, Query query) {
    return (Long) session.getAttribute("queryReplyId_" + query.getId());
  }
  
  public static void storeQueryReplyId(HttpSession session, QueryReply queryReply) {
    session.setAttribute("queryReplyId_" + queryReply.getQuery().getId(), queryReply.getId());
  }

  public static void clearQueryReplyIds(HttpSession session) {
    List<String> attributes = new ArrayList<String>();
    Enumeration<String> attributeNames = session.getAttributeNames();
    while (attributeNames.hasMoreElements()) {
      attributes.add(attributeNames.nextElement());
    }
    for (String attribute : attributes) {
      if (attribute.startsWith("queryReplyId_")) {
        session.removeAttribute(attribute);
      }
    }
  }
  
  private static void clearQueryReplyId(HttpSession session, Query query) {
    session.removeAttribute("queryReplyId_" + query.getId());
  }

  public static QueryReply findQueryReply(RequestContext requestContext, User loggedUser, Query query) {
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryReply queryReply = null;
    Long queryReplyId = requestContext.getLong("queryReplyId");
    if (queryReplyId == null) {
      queryReplyId = getQueryReplyId(requestContext.getRequest().getSession(), query);
    }
    if (queryReplyId != null) {
      queryReply = queryReplyDAO.findById(queryReplyId);
      if (queryReply != null) {
        if (queryReply.getArchived() == true || queryReply.getStamp().getId() != panelStamp.getId()) {
          queryReply = null;
          clearQueryReplyId(requestContext.getRequest().getSession(), query);
        }
        else {
          return queryReply;
        }
      }
    }
    return queryReplyDAO.findByUserAndQueryAndStamp(loggedUser, query, panelStamp);
  }

  public static byte[] exportQueryDataAsCSV(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, Query query, PanelStamp panelStamp) throws IOException {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
    Collections.sort(queryPages, new Comparator<QueryPage>() {
      @Override
      public int compare(QueryPage o1, QueryPage o2) {
        return o1.getPageNumber() - o2.getPageNumber();
      }
    });
    
    Map<QueryReply, Map<Integer, Object>> rows = new HashMap<QueryReply, Map<Integer, Object>>();
    List<String> columns = new ArrayList<String>();
    
    for (QueryPage queryPage : queryPages) {
      QueryExportContextImpl exportContext = new QueryExportContextImpl(locale, queryPage, panelStamp, columns, rows);
      exportContext.setQueryReplies(replies);
      QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
      queryPageHandler.exportData(exportContext);
    }
    
    return exportDataToCsv(locale, replierExportStrategy, columns, rows);
  }

  public static byte[] exportQueryPageDataAsCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) throws IOException {
    Map<QueryReply, Map<Integer, Object>> rows = new HashMap<QueryReply, Map<Integer, Object>>();
    List<String> columns = new ArrayList<String>();
       
    QueryExportContextImpl exportContext = new QueryExportContextImpl(locale, queryPage, panelStamp, columns, rows);
    exportContext.setQueryReplies(replies);
    QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
    queryPageHandler.exportData(exportContext);
    
    return exportDataToCsv(locale, replierExportStrategy, exportContext.getColumns(), exportContext.getRows());
  }
  
  private static byte[] exportDataToCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<String> columns, Map<QueryReply, Map<Integer, Object>> rows) throws IOException {
    try (
      ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
      OutputStreamWriter streamWriter = new OutputStreamWriter(csvStream, Charset.forName("UTF-8"))) {
      CSVWriter csvWriter = new CSVWriter(streamWriter, ',');
      List<String> nextLine = new ArrayList<>();
      
      switch (replierExportStrategy) {
      	case NONE:
      	break;
      	case HASH:
      	  nextLine.add(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierIdColumn"));
      	break;
      	case NAME:
      	  nextLine.add(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierNameColumn"));
      	break;
      	case EMAIL:
      	  nextLine.add(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierEmailColumn"));
      	break;
      }
      
      // Header
      for (String column : columns) {
        nextLine.add(column);
      }
      
      csvWriter.writeNext(nextLine.toArray(new String[0]));
      nextLine = new ArrayList<>();
      
      // Rows
      for (QueryReply queryReply : rows.keySet()) {
      	switch (replierExportStrategy) {
        	case NONE:
        	break;
        	case HASH:
        	  nextLine.add(queryReply.getUser() != null ? RequestUtils.md5EncodeString(String.valueOf(queryReply.getUser().getId())) : "-");
        	break;
        	case NAME:
        	  nextLine.add(queryReply.getUser() != null ? queryReply.getUser().getFullName(true, false) : "-");
        	break;
        	case EMAIL:
        	  nextLine.add(queryReply.getUser() != null ? queryReply.getUser().getDefaultEmailAsString() : "-");
        	break;
        }
      	
        Map<Integer, Object> columnValues = rows.get(queryReply);
  
        for (int columnIndex = 0, columnCount = columns.size(); columnIndex < columnCount; columnIndex++) {
          Object value = columnValues.get(columnIndex);
          if (value == null) {
            nextLine.add("");
          } else {
            if (value instanceof Number) {
              nextLine.add(String.valueOf(value));
            }
            else {
  
              // Convert cell value line breaks to spaces, as poor little Excel has trouble interpreting them correctly    
              // TODO This conversion could probably be avoided some way; OpenOffice/LibreOffice handle line breaks just fine  
              
              nextLine.add(String.valueOf(value).replace('\n', ' ').replace('\r', ' '));
            }
          }
        }
  
        csvWriter.writeNext(nextLine.toArray(new String[0]));
        nextLine = new ArrayList<>();
      }
  
      csvWriter.close();
  
      return csvStream.toByteArray();
    }
  }

  public enum ReplierExportStrategy {
  	NONE,
  	HASH,
  	NAME,
  	EMAIL
  }
}
