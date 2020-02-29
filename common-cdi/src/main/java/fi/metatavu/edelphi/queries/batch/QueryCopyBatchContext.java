package fi.metatavu.edelphi.queries.batch;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jberet.cdi.JobScoped;

/**
 * Batch context for copying queries
 * 
 * @author Antti Leppä
 */
@JobScoped
public class QueryCopyBatchContext {
  
  private Long originalQueryId;
  private Long newQueryId;
  private Map<Long, Long> queryReplyIdMap;
  private Map<Long, Long> querySectionIdMap;
  private Map<Long, Long> queryCommentCategoryIdMap;
  private Map<Long, Long> queryPageIdMap;
  
  @PostConstruct
  public void init() {
    queryReplyIdMap = new HashMap<>();
    querySectionIdMap = new HashMap<>();
    queryCommentCategoryIdMap = new HashMap<>();
    queryPageIdMap = new HashMap<>();
  }
  
  /**
   * Returns new query id
   * 
   * @return new query id
   */
  public Long getNewQueryId() {
    return newQueryId;
  }
  
  /**
   * Sets new query id
   * 
   * @param newQueryId new query id
   */
  public void setNewQueryId(Long newQueryId) {
    this.newQueryId = newQueryId;
  }
  
  /**
   * Returns original query id
   * 
   * @return original query id
   */
  public Long getOriginalQueryId() {
    return originalQueryId;
  }
  
  /**
   * Sets original query id
   * 
   * @param originalQueryId original query id
   */
  public void setOriginalQueryId(Long originalQueryId) {
    this.originalQueryId = originalQueryId;
  }
  
  /**
   * Sets mapping entry for original -> new query reply id
   * 
   * @param originalId original id
   * @param newId new id
   */
  public void setQueryReplyId(Long originalId, Long newId) {
    queryReplyIdMap.put(originalId, newId);
  }

  /**
   * Sets mapping entry for original -> new query section id
   * 
   * @param originalId original id
   * @param newId new id
   */
  public void setQuerySectionId(Long originalId, Long newId) {
    querySectionIdMap.put(originalId, newId);
  }

  /**
   * Sets mapping entry for original -> new query comment category id
   * 
   * @param originalId original id
   * @param newId new id
   */
  public void setQueryCommentCategoryId(Long originalId, Long newId) {
    queryCommentCategoryIdMap.put(originalId, newId);
  }

  /**
   * Sets mapping entry for original -> new query page id
   * 
   * @param originalId original id
   * @param newId new id
   */
  public void setQueryPageId(Long originalId, Long newId) {
    queryPageIdMap.put(originalId, newId);
  }
  
  /**
   * Returns mapping entry for original -> new query comment category id
   * 
   * @return id mapping map
   */
  public Map<Long, Long> getQueryCommentCategoryIdMap() {
    return queryCommentCategoryIdMap;
  }

  /**
   * Returns mapping entry for original -> new query reply id
   * 
   * @return id mapping map
   */
  public Map<Long, Long> getQueryReplyIdMap() {
    return queryReplyIdMap;
  }

  /**
   * Returns mapping entry for original -> new query section id
   * 
   * @return id mapping map
   */
  public Map<Long, Long> getQuerySectionIdMap() {
    return querySectionIdMap;
  }

  /**
   * Returns mapping entry for original -> new query page id
   * 
   * @return id mapping map
   */
  public Map<Long, Long> getQueryPageIdMap() {
    return queryPageIdMap;
  }
  
}
