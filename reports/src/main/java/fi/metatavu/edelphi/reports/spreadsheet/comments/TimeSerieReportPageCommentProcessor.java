package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;

/**
 * Report page comment processor for time serie pages
 * 
 * @author Antti Leppä
 */
public class TimeSerieReportPageCommentProcessor extends AbstractReportPageCommentProcessor {
  
  private Double maxX;
  private String axisXTitle;

  /**
   * Constructor for a comment processor
   * 
   * @param queryPage query page
   * @param rootComments root comments
   * @param answers target map for answers
   * @param maxX max value for x axis
   * @param axisXTitle x axis title
   */
  public TimeSerieReportPageCommentProcessor(QueryPage queryPage, List<QueryQuestionComment> rootComments, Map<Long, Map<String, String>> answers, Double maxX, String axisXTitle) {
    super(queryPage, rootComments, answers);
    this.maxX = maxX;
    this.axisXTitle = axisXTitle;
  }

  @Override
  public void processComments() {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO questionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    String maxFieldName = getFieldName(maxX);
    QueryNumericField maxQueryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(getQueryPage(), maxFieldName);
    
    
    String maxCaption = String.format("%s - %s", axisXTitle, String.valueOf(Math.round(maxX)));
    
    final Map<Long, Double> answerMap = new HashMap<>();
    for (QueryQuestionComment comment : getRootComments()) {
      QueryReply queryReply = comment.getQueryReply();
      QueryQuestionNumericAnswer maxAnswer = questionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, maxQueryField);
      
      if (maxAnswer != null && maxAnswer.getData() != null) {
        answerMap.put(comment.getId(), maxAnswer.getData());
        String caption = maxCaption;
        String value = getAnswerValue(maxAnswer);
        setCommentLabel(comment.getId(), caption, value);
      } else {
        answerMap.put(comment.getId(), Double.MIN_VALUE);
      }
    }
    
    sortRootComments(new DoubleQuestionCommentComparator(answerMap));
  }

  /**
   * Returns answer value
   * 
   * @param answer answer
   * @return answer value
   */
  private String getAnswerValue(QueryQuestionNumericAnswer answer) {
    return answer != null && answer.getData() != null ? String.valueOf(Math.round(answer.getData())) : null;
  }
  
  /**
   * Returns field name for specific x
   * 
   * @param x x
   * @return field name
   */
  private String getFieldName(Double x) {
    return  "time_serie." + x;
  }
  
}
