package fi.metatavu.edelphi.utils.comments;

import java.util.Comparator;
import java.util.Map;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;

/**
 * Comparator for comparing query question comments by string based answer map
 * 
 * @author Antti Leppä
 */
public class StringQuestionCommentComparator implements Comparator<QueryQuestionComment> {
  
  private final Map<Long, String> answerMap;

  /**
   * Constructor
   * 
   * @param answerMap answer map
   */
  protected StringQuestionCommentComparator(Map<Long, String> answerMap) {
    this.answerMap = answerMap;
  }

  @Override
  public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
    return answerMap.get(o2.getId()).compareTo(answerMap.get(o1.getId()));
  }
}