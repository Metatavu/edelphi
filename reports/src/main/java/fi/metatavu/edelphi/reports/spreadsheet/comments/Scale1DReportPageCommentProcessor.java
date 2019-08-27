package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;

/**
 * Report page comment processor for scale 1d pages
 * 
 * @author Antti Leppä
 */
public class Scale1DReportPageCommentProcessor extends AbstractReportPageCommentProcessor {

  /**
   * Constructor for a comment processor
   * 
   * @param queryPage query page
   * @param rootComments root comments
   * @param answers target map for answers
   */
  public Scale1DReportPageCommentProcessor(QueryPage queryPage, List<QueryQuestionComment> rootComments, Map<Long, Map<String, String>> answers) {
    super(queryPage, rootComments, answers);
  }

  @Override
  public void processComments() {
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(getQueryPage());

    final Map<Long,String> answerMap = new HashMap<>();
    for (QueryQuestionComment comment : getRootComments()) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryOptionField);
      answerMap.put(comment.getId(), answer == null ? "-" : answer.getOption().getValue());
      if (answer != null) {
        String caption = StringUtils.capitalize(StringUtils.lowerCase(answer.getOption().getOptionField().getCaption()));
        String value = answer.getOption().getText();
        setCommentLabel(comment.getId(), caption, value);
      }
    }
    
    sortRootComments(new StringQuestionCommentComparator(answerMap));
  }
  
  @SuppressWarnings ("squid:S00112")
  private QueryOptionField getOptionFieldFromScale1DPage(QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
  
    List<QueryField> pageFields = queryFieldDAO.listByQueryPage(queryPage);
    
    if (pageFields.size() == 1) 
      return (QueryOptionField) pageFields.get(0);
    else
      throw new RuntimeException("Scale 1D page contained more that one field");
  }
  
}
