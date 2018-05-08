package fi.metatavu.edelphi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.opencsv.CSVWriter;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
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
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.StatusCode;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;

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
  
  /**
   * Export query comments into CSV byte array
   * 
   * @param locale locale
   * @param replierExportStrategy replies export strategy
   * @param replies replies to be exported
   * @param query query
   * @param stamp stamp
   * @return query comments in CSV byte array
   * @throws IOException throws IOException when CSV writing fails
   */
  public static byte[] exportQueryCommentsAsCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, Query query, PanelStamp stamp) throws IOException {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
    Collections.sort(queryPages, new QueryPageNumberComparator());
    
    String[] columnHeaders = new String[] { 
      getReplierExportStrategyLabel(locale, replierExportStrategy), 
      Messages.getInstance().getText(locale, "panelAdmin.query.export.csvCommentAnswerColumn"), 
      Messages.getInstance().getText(locale, "panelAdmin.query.export.csvCommentCommentColumn"), 
      Messages.getInstance().getText(locale, "panelAdmin.query.export.csvCommentReplyColumn"), 
    };
    
    List<String[]> rows = new ArrayList<>();
    
    for (QueryPage queryPage : queryPages) {
      List<String[]> pageRows = exportQueryPageCommentsAsCsv(replierExportStrategy, replies, stamp, queryPage);
      
      if (!pageRows.isEmpty()) {
        rows.add(new String[] { "", "", "", "" });
        rows.add(new String[] { queryPage.getTitle(), "", "", "" });
        rows.add(new String[] { "", "", "", "" });
        rows.addAll(pageRows);
      }
    }

    return writeCsv(columnHeaders, rows);
  }

  public static byte[] exportQueryDataAsCSV(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, Query query, PanelStamp panelStamp) throws IOException {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
    Collections.sort(queryPages, new QueryPageNumberComparator());
    
    Map<QueryReply, Map<Integer, Object>> rows = new HashMap<>();
    List<String> columns = new ArrayList<>();
    
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

  /**
   * Exports single query page into CSV rows
   * 
   * @param replierExportStrategy replier export strategy
   * @param replies replies to be exported
   * @param stamp stamp
   * @param queryPage query page to be exported
   * @return CSV rows
   */
  private static List<String[]> exportQueryPageCommentsAsCsv(ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, PanelStamp stamp, QueryPage queryPage) {
    QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
    if (queryPageHandler != null) {
      ReportPageCommentProcessor processor = queryPageHandler.exportComments(queryPage, stamp, replies);
      if (processor != null) {
        return exportQueryPageCommentsAsCsv(replierExportStrategy, queryPage, processor);
      }
    }
    
    return Collections.emptyList();
  }

  /**
   * Exports single query page into CSV rows using comment processor
   * 
   * @param replierExportStrategy replier export strategy
   * @param queryPage query page to be exported
   * @param processor comment processor
   * @return CSV rows
   */
  private static List<String[]> exportQueryPageCommentsAsCsv(ReplierExportStrategy replierExportStrategy, QueryPage queryPage, ReportPageCommentProcessor processor) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<String[]> rows = new ArrayList<>();

    processor.processComments();
    List<QueryQuestionComment> rootComments = processor.getRootComments();
    
    if (rootComments != null && !rootComments.isEmpty()) {
      Map<Long, List<QueryQuestionComment>> childCommentMap = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
      
      for (QueryQuestionComment rootComment : rootComments) {
        String rootUser = getReplierExportStrategyValue(replierExportStrategy, rootComment.getQueryReply());
        String rootLabel = processor.getCommentLabel(rootComment.getId());
        rows.add(new String[] { rootUser, rootLabel, rootComment.getComment(), "" });
        List<QueryQuestionComment> childComments = childCommentMap.get(rootComment.getId());
        if (childComments != null) {
          childComments.forEach(childComment -> {
            String childUser = getReplierExportStrategyValue(replierExportStrategy, childComment.getQueryReply());
            rows.add(new String[] { childUser, "", "", childComment.getComment() });
          });
        }
      }
    }
    
    return rows;
  }
  
  /**
   * Writes a CSV file
   * 
   * @param columnHeaders headers
   * @param rows rows
   * @return CSV file
   * @throws IOException throws IOException when CSV writing fails
   */
  private static byte[] writeCsv(String[] columnHeaders, List<String[]> rows) throws IOException {
    return writeCsv(columnHeaders, rows.stream().toArray(String[][]::new));
  }

  /**
   * Writes a CSV file
   * 
   * @param columnHeaders headers
   * @param rows rows
   * @return CSV file
   * @throws IOException throws IOException when CSV writing fails
   */
  private static byte[] writeCsv(String[] columnHeaders, String[][] rows) throws IOException {
    try (ByteArrayOutputStream csvStream = new ByteArrayOutputStream(); OutputStreamWriter streamWriter = new OutputStreamWriter(csvStream, Charset.forName("UTF-8"))) {
      CSVWriter csvWriter = new CSVWriter(streamWriter, ',');

      csvWriter.writeNext(columnHeaders);
      
      for (String[] row : rows) {
        csvWriter.writeNext(row);
      }

      csvWriter.close();
      
      return csvStream.toByteArray();
    }
  }
  
  /**
   * Returns label for given replier export strategy
   * 
   * @param locale locale
   * @param replierExportStrategy replier export strategy
   * @return label
   */
  private static String getReplierExportStrategyLabel(Locale locale, ReplierExportStrategy replierExportStrategy) {
    switch (replierExportStrategy) {
      case NONE:
      break;
      case HASH:
        return Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierIdColumn");
      case NAME:
        return Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierNameColumn");
      case EMAIL:
        return Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierEmailColumn");
    }
    
    return null;
  }
  
  /**
   * Returns user identifier for given replier export strategy
   * 
   * @param replierExportStrategy replier export strategy
   * @param queryReply reply
   * @return user identifier
   */
  private static String getReplierExportStrategyValue(ReplierExportStrategy replierExportStrategy, QueryReply queryReply) {
    if (queryReply != null) {
      User user = queryReply.getUser();
      if (user != null) {
        switch (replierExportStrategy) {
          case NONE:
          break;
          case HASH:
            return RequestUtils.md5EncodeString(String.valueOf(user.getId()));
          case NAME:
            return user.getFullName(true, false);
          case EMAIL:
            return user.getDefaultEmailAsString();
        }
      }
    }
    
    return "-";
  }
  
  private static byte[] exportDataToCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<String> columns, Map<QueryReply, Map<Integer, Object>> rows) throws IOException {
    try (
      ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
      OutputStreamWriter streamWriter = new OutputStreamWriter(csvStream, Charset.forName("UTF-8"))) {
      CSVWriter csvWriter = new CSVWriter(streamWriter, ',');
      List<String> nextLine = new ArrayList<>();
      
      String replierExportStrategyLabel = getReplierExportStrategyLabel(locale, replierExportStrategy);
      if (replierExportStrategyLabel != null) {
        nextLine.add(replierExportStrategyLabel);
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
