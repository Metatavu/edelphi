package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
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
   * @param panelStamp panel stamp
   * @param queryPage query page
   * @param answers target map for answers
   */
  public Scale1DReportPageCommentProcessor(PanelStamp panelStamp, QueryPage queryPage, Map<Long, Map<String, String>> answers) {
    super(panelStamp, queryPage, answers);
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
