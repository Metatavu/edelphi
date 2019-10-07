package fi.metatavu.edelphi.reports.spreadsheet.export;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.queries.QueryPageController;
import fi.metatavu.edelphi.reports.i18n.ReportMessages;
import fi.metatavu.edelphi.reports.spreadsheet.SpreadsheetExportContext;
import fi.metatavu.edelphi.reports.spreadsheet.comments.ReportPageCommentProcessor;
import fi.metatavu.edelphi.reports.spreadsheet.comments.TimeSerieReportPageCommentProcessor;

@ApplicationScoped
public class TimeserieQueryPageSpreadsheetExporter extends AbstractQueryPageSpreadsheetExporter {
  
  @Inject
  private ReportMessages reportMessages;

  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;
  
  @Override
  public void exportSpreadsheet(SpreadsheetExportContext exportContext) {
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    boolean commentable = isPageCommentable(queryPage);
    Double minX = queryPageController.getDoubleSetting(queryPage, "time_serie.minX");
    Double maxX = queryPageController.getDoubleSetting(queryPage, "time_serie.maxX");
    Double stepX = queryPageController.getDoubleSetting(queryPage, "time_serie.stepX");
    Double userStepX = queryPageController.getDoubleSetting(queryPage, "time_serie.userStepX");
    
    if (userStepX == null)
      userStepX = stepX;

    if (minX != null && maxX != null && userStepX != null) {
      NavigableMap<String,String> predefinedValues = queryPageController.getMapSetting(queryPage, "time_serie.predefinedValues");
      int predefinedValueCount = getPredefinedValueCount(predefinedValues);
      minX = getUserAnswerMinX(predefinedValues, minX);

      for (Double x = minX + (predefinedValueCount > 0 ? userStepX : 0); x <= maxX; x += userStepX) {
        String fieldName = getFieldName(x);

        QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);

        
        int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryNumericField.getCaption());
        
        for (QueryReply queryReply : queryReplies) {
          QueryQuestionNumericAnswer answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryNumericField);
          if (answer != null && answer.getData() != null)
            exportContext.setCellValue(queryReply, columnIndex, answer.getData());
        }
      }

      if (commentable) {
        Locale locale = exportContext.getLocale();
        int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + reportMessages.getText(locale, "reports.spreadsheet.comment")); 
        for (QueryReply queryReply : queryReplies) {
          QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
          exportContext.setCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
        }
      }

    }
  }
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    Double maxX = queryPageController.getDoubleSetting(queryPage, "time_serie.maxX");
    String axisXTitle = queryPageController.getSetting(queryPage, "time_serie.xAxisTitle");
    return new TimeSerieReportPageCommentProcessor(queryPage, listRootComments(stamp, queryPage), new HashMap<>(), maxX, axisXTitle);
  }
  
  private int getPredefinedValueCount(NavigableMap<String, String> predefinedValues) {
    Set<String> keySet = predefinedValues.keySet();
    
    int predefinedValueCount = 0;
    int i = 0;
    
    for (String x : keySet) {
      String y = predefinedValues.get(x);
      
      if (y != null) {
        predefinedValueCount = i;
      }
      
      i++;
    }
    
    return predefinedValueCount;
  }

  private Double getUserAnswerMinX(NavigableMap<String, String> predefinedValues, Double minX) {
    for (String key : predefinedValues.navigableKeySet()) {
      if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(predefinedValues.get(key))) {
        minX = Math.max(minX, NumberUtils.createDouble(key));
      }
    }

    return minX;
  }

  private String getFieldName(Double x) {
    return "time_serie." + x;
  }
  
}
