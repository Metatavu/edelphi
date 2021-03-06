package fi.metatavu.edelphi.reports.spreadsheet.comments;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Abstract base class for all report page comment processors
 * 
 * @author Antti Leppä
 */
public abstract class AbstractReportPageCommentProcessor implements ReportPageCommentProcessor {
  
  private QueryPage queryPage;
  private List<QueryQuestionComment> rootComments;
  private Map<Long, Map<String, String>> answers;
  
  /**
   * Constructor for a comment processor
   * 
   * @param panelStamp panel stamp
   * @param queryPage query page
   * @param answers target map for answers
   */
  public AbstractReportPageCommentProcessor(QueryPage queryPage, List<QueryQuestionComment> rootComments, Map<Long, Map<String, String>> answers) {
    this.queryPage = queryPage;
    this.answers = answers;
    this.rootComments = rootComments;
  }
  
  /**
   * Returns root comments. List order can changes when calling process comments method.
   * 
   * @return root comments
   */
  @Override
  public List<QueryQuestionComment> getRootComments() {
    return rootComments;
  }
  
  /**
   * Returns query page
   * 
   * @return query page
   */
  public QueryPage getQueryPage() {
    return queryPage;
  }
  
  /**
   * Returns comment label as string
   * 
   * @param id comment id
   * @return comment label as string
   */
  public String getCommentLabel(Long id) {
    Map<String, String> valueMap = answers.get(id);
    if (valueMap != null && !valueMap.isEmpty()) {
      Set<Entry<String,String>> entrySet = valueMap.entrySet();
      List<String> labels = entrySet.stream()
        .map(entry -> String.format("%s / %s", entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
      
      return StringUtils.join(labels, " - ");
    }
    
    return null;
  }
  
  /**
   * Sorts root comment list with specified comparator
   * 
   * @param comparator comparator
   */
  protected void sortRootComments(Comparator<QueryQuestionComment> comparator) {
    Collections.sort(rootComments, comparator);
  }
  
  /**
   * Sets a label for a specified comment
   * 
   * @param id comment id
   * @param caption caption
   * @param value value
   */
  protected void setCommentLabel(Long id, String caption, String value) {
    Map<String, String> valueMap = answers.get(id);
    if (valueMap == null) {
      valueMap = new LinkedHashMap<>();
    }
    
    valueMap.put(caption, value);
    answers.put(id, valueMap);
  }
 
}
