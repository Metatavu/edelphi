package fi.metatavu.edelphi.utils.comments;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;

/**
 * Report page comment processor for scale 2d pages
 * 
 * @author Antti Leppä
 */
public class Scale2DReportPageCommentProcessor extends AbstractReportPageCommentProcessor {

  private Map<Long, String> answerMap;
  
  /**
   * Constructor for a comment processor
   * 
   * @param panelStamp panel stamp
   * @param queryPage query page
   * @param answers target map for answers
   */
  public Scale2DReportPageCommentProcessor(PanelStamp panelStamp, QueryPage queryPage, Map<Long, Map<String, String>> answers) {
    super(panelStamp, queryPage, answers);
    this.answerMap = new HashMap<>();
  }

  @Override
  public void processComments() {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(getQueryPage(), getFieldName("x"));
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(getQueryPage(), getFieldName("y"));
    
    for (QueryQuestionComment comment : getRootComments()) {
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldX); 
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldY);
      answerMap.put(comment.getId(), (xAnswer == null ? "-" : xAnswer.getOption().getValue()) + (yAnswer == null ? "-" : yAnswer.getOption().getValue()));
      if (xAnswer != null || yAnswer != null) {
        if (xAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(xAnswer.getOption().getOptionField().getCaption()));
          setCommentLabel(comment.getId(), caption, xAnswer.getOption().getText());
        }
        
        if (yAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(yAnswer.getOption().getOptionField().getCaption()));
          setCommentLabel(comment.getId(), caption, yAnswer.getOption().getText());
        }
      }
    }
    
    sortRootComments(new StringQuestionCommentComparator(answerMap));
  }

  /**
   * Returns field name for axis
   * 
   * @param axis axis
   * @return field name
   */
  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }
  
}
