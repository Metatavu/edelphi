package fi.metatavu.edelphi.utils.comments;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;

/**
 * Abstract base class for all report page comment processors
 * 
 * @author Antti Leppä
 */
public abstract class AbstractReportPageCommentProcessor implements ReportPageCommentProcessor {
  
  private PanelStamp panelStamp;
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
  public AbstractReportPageCommentProcessor(PanelStamp panelStamp, QueryPage queryPage, Map<Long, Map<String, String>> answers) {
    this.panelStamp = panelStamp;
    this.queryPage = queryPage;
    this.answers = answers;
    this.rootComments = listRootComments();
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
    Map<String,String> valueMap = new LinkedHashMap<>();
    valueMap.put(caption, value);
    answers.put(id, valueMap);
  }

  /**
   * Lists page's root comments
   * 
   * @return page's root comments
   */
  private List<QueryQuestionComment> listRootComments() {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    return queryQuestionCommentDAO.listRootCommentsByQueryPageAndStampOrderByCreated(queryPage, panelStamp); 
  }
 
}
