package fi.metatavu.edelphi.utils.comments;

import java.util.List;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;

/**
 * Interface that describes a single report page comment processor. Function of comment processors is to sort and label root comments by query type specific rules
 * 
 * @author Antti Leppä
 */
public interface ReportPageCommentProcessor {

  /**
   * Returns root comments. List order can changes when calling process comments method.
   * 
   * @return root comments
   */
  public List<QueryQuestionComment> getRootComments();
  
  /**
   * Performs comments processing
   */
  public void processComments();
  
}
