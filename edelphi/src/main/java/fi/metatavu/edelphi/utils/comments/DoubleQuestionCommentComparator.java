package fi.metatavu.edelphi.utils.comments;

import java.util.Comparator;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;

/**
 * Comparator for comparing query question comments by double based answer map
 * 
 * @author Antti Leppä
 */
public class DoubleQuestionCommentComparator implements Comparator<QueryQuestionComment> {
  
  private Map<Long ,Double> answerMap;
  
  /**
   * Constructor
   * 
   * @param answerMap answer map
   */
  public DoubleQuestionCommentComparator(Map<Long ,Double> answerMap) {
    this.answerMap = answerMap;
  }
  
  @Override
  public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
    return answerMap.get(o2.getId()).compareTo(answerMap.get(o1.getId()));
  }
  
}