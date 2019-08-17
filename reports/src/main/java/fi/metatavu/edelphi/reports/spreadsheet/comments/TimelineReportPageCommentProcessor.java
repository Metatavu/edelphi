package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.HashMap;
import java.util.Map;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;

/**
 * Report page comment processor for timeline pages
 * 
 * @author Antti Leppä
 */
public class TimelineReportPageCommentProcessor extends AbstractReportPageCommentProcessor {

  private static final String TIMELINE_VALUE_1 = "timeline.value1";
  private static final String TIMELINE_VALUE_2 = "timeline.value2";

  /**
   * Constructor for a comment processor
   * 
   * @param panelStamp panel stamp
   * @param queryPage query page
   * @param answers target map for answers
   */
  public TimelineReportPageCommentProcessor(PanelStamp panelStamp, QueryPage queryPage, Map<Long, Map<String, String>> answers) {
    super(panelStamp, queryPage, answers);
  }

  @Override
  public void processComments() {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    QueryNumericField queryField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(getQueryPage(), TIMELINE_VALUE_1);
    QueryNumericField queryField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(getQueryPage(), TIMELINE_VALUE_2);

    final Map<Long, Double> answerMap = new HashMap<>();
    for (QueryQuestionComment comment : getRootComments()) {
      QueryReply queryReply = comment.getQueryReply();
      
      QueryQuestionNumericAnswer answer1 = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField1);
      QueryQuestionNumericAnswer answer2 = queryField2 == null ? null : queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField2);

      if ((answer1 != null) && (answer1.getData() != null)) {
        answerMap.put(comment.getId(), answer1.getData());
        String caption;
        String value;
        
        if ((answer2 != null) && (answer2.getData() != null)) {
          caption = String.format("%s / %s", queryField1.getCaption(), queryField2.getCaption());
          value = String.format("%s / %s", getAnswerValue(answer1), getAnswerValue(answer2));
        } else {
          caption = queryField1.getCaption();
          value = getAnswerValue(answer1);
        }
        
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
  
}
