package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Report generic page comment processor
 * 
 * @author Antti Leppä
 */
public class GenericReportPageCommentProcessor extends AbstractReportPageCommentProcessor {

  /**
   * Constructor for a comment processor
   * 
   * @param queryPage query page
   * @param rootComments root comments
   * @param answers target map for answers
   */
  public GenericReportPageCommentProcessor(QueryPage queryPage, List<QueryQuestionComment> rootComments, Map<Long, Map<String, String>> answers) {
    super(queryPage, rootComments, answers);
  }

  @Override
  public void processComments() {
    // Nothing to do
  }
  
}
