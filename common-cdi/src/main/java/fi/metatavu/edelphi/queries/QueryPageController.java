package fi.metatavu.edelphi.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentCategoryDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.resources.ResourceController;

/**
 * Controller for query pages
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class QueryPageController {

  public static final String LIVE2D_VISIBLE_OPTION = "live2d.visible";
  public static final String LIVE2D_LABEL_OPTION_TEMPLATE = "live2d.label.%s";
  public static final String LIVE2D_COLOR_OPTION_TEMPLATE = "live2d.color.%s";
  public static final String OPTIONS_OPTION_TEMPLATE = "live2d.options.%s";
  
  private static final String UTF_8 = "UTF-8";
  private static final String JSON_SERIALIZED_FILTER_START = "/**JSS-";
  private static final String JSON_SERIALIZED_FILTER_END = "-JSS**/";
  private static final String LIVE2D_FIELD_NAME_X = "x";
  private static final String LIVE2D_FIELD_NAME_Y = "y";
  
  @Inject
  private Logger logger;

  @Inject
  private QueryPageDAO queryPageDAO;

  @Inject
  private QueryPageSettingDAO queryPageSettingDAO;
  
  @Inject
  private QueryPageSettingKeyDAO queryPageSettingKeyDAO;
  
  @Inject
  private ResourceController resourceController;

  @Inject
  private QueryQuestionCommentCategoryDAO queryQuestionCommentCategoryDAO;

  @Inject
  private QueryNumericFieldDAO queryNumericFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;

  /**
   * Finds a query page by id
   * 
   * @param queryPageId query page
   * @return found query or null if not found
   */
  public QueryPage findQueryPage(Long queryPageId) {
    return queryPageDAO.findById(queryPageId);
  }

  /**
   * Returns page setting as string
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as string
   */
  public String getSetting(QueryPage queryPage, String name) {
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(name);
    if (key != null) {
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage); 
      if (queryPageSetting != null)
        return queryPageSetting.getValue();
    }
    
    return null;
  }

  /**
   * Returns page setting as long
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as long
   */
  public Long getLongSetting(QueryPage queryPage, String name) {
    return NumberUtils.createLong(getSetting(queryPage, name));
  }

  /**
   * Returns page setting as integer
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as integer
   */
  public Integer getIntegerSetting(QueryPage queryPage, String name) {
    return NumberUtils.createInteger(getSetting(queryPage, name));
  }

  /**
   * Returns page setting as double
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as double
   */
  public Double getDoubleSetting(QueryPage queryPage, String name) {
    return NumberUtils.createDouble(getSetting(queryPage, name));
  }

  /**
   * Returns page setting as boolean
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as boolean
   */
  public Boolean getBooleanSetting(QueryPage queryPage, String name) {
    String value = getSetting(queryPage, name);
    return "1".equals(value);
  }
  
  /**
   * Returns page setting as an enum
   * 
   * @param <E> generic type for an enum class
   * @param queryPage query page
   * @param name setting name
   * @param enumClass enum class
   * @return enum or null if not found
   */
  public <E extends Enum<E>> E getEnumSetting(QueryPage queryPage, String name, Class<E> enumClass) {
    String value = getSetting(queryPage, name);
    return EnumUtils.getEnum(enumClass, value);
  }

  /**
   * Returns page setting as map
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as map
   */
  public NavigableMap<String, String> getMapSetting(QueryPage queryPage, String name) {
    return parseSerializedMap(getSetting(queryPage, name));
  }

  /**
   * Returns page setting as list
   * 
   * @param queryPage query 
   * @param name setting name
   * @return page setting as list
   */
  public List<String> getListSetting(QueryPage queryPage, String name) {
    return parseSerializedList(getSetting(queryPage, name));
  }

  /**
   * Sets page setting
   * 
   * @param queryPage query page
   * @param name setting name
   * @param value setting value
   * @param modifier modifier
   */
  public void setSetting(QueryPage queryPage, String name, String value, User modifier) {
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(name);
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    if (key == null) {
      key = queryPageSettingKeyDAO.create(name);
    }
    
    QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage);
    
    if (StringUtils.isBlank(value)) {
      if (queryPageSetting != null) {
        queryPageSettingDAO.delete(queryPageSetting);
      }
    }
    else {
      if (queryPageSetting != null) 
        queryPageSettingDAO.updateValue(queryPageSetting, value);
      else
        queryPageSettingDAO.create(key, queryPage, value);
    }
    
    queryPageDAO.updateLastModified(queryPage, new Date(), modifier);
  }

  /**
   * Sets page setting
   * 
   * @param queryPage query page
   * @param name setting name
   * @param value setting value
   * @param modifier modifier
   */
  public void setSetting(QueryPage queryPage, String name, List<String> value, User modifier) {
    setSetting(queryPage, name, serializeList(value), modifier);
  }

  /**
   * Sets page setting
   * 
   * @param queryPage query page
   * @param name setting name
   * @param value setting value
   * @param modifier modifier
   */
  public void setSetting(QueryPage queryPage, String name, NavigableMap<String, String> value, User modifier) {
    setSetting(queryPage, name, serializeMap(value), modifier);
  }

  /**
   * Filters JSON value
   *  
   * @param value value
   * @return filtered value
   */
  public String filterJsonSerializedSetting(String value) {
    return new StringBuilder(JSON_SERIALIZED_FILTER_START).append(value).append(JSON_SERIALIZED_FILTER_END).toString();
  }

  /**
   * Unfilters JSON value
   *  
   * @param value value
   * @return unfiltered value
   */
  public String unfilterJsonSerializedSetting(String value) {
    if (value.startsWith(JSON_SERIALIZED_FILTER_START) && value.endsWith(JSON_SERIALIZED_FILTER_END))
      return value.substring(JSON_SERIALIZED_FILTER_START.length(), value.length() - JSON_SERIALIZED_FILTER_END.length());
    return value;
  }

  /**
   * Returns whether page is from given panel
   * 
   * @param panel panel
   * @param queryPage page
   * @return whether page is from given panel
   */
  public boolean isPanelsPage(Panel panel, QueryPage queryPage) {
    if (panel == null || queryPage == null) {
      return false;
    }
    
    Panel queryPanel = resourceController.getResourcePanel(queryPage.getQuerySection().getQuery());
    return panel.getId().equals(queryPanel.getId());
  }

  /**
   * Returns whether page is from query
   * 
   * @param query query
   * @param queryPage page
   * @return whether page is from query
   */
  public boolean isQuerysPage(Query query, QueryPage queryPage) {
    if (query == null || queryPage == null) {
      return false;
    }
    
    return query.getId().equals(queryPage.getQuerySection().getQuery().getId());
  }
  
  /**
   * Creates new comment category
   * 
   * @param queryPage query page
   * @param name name
   * @param creator creator
   * @return created comment category
   */
  public QueryQuestionCommentCategory createCommentCategory(Query query, QueryPage queryPage, String name, User creator) {
    return queryQuestionCommentCategoryDAO.create(query, queryPage, name, creator, creator, new Date(), new Date());
  }
  
  /**
   * Lists page comment categories by page
   * 
   * @param queryPage query page
   * @param includeQueryScoped whether query scoped categories should be included or not
   * @return comment categories
   */
  public List<QueryQuestionCommentCategory> listCommentCategoriesByPage(QueryPage queryPage, boolean includeQueryScoped) {
    if (includeQueryScoped) {
      return queryQuestionCommentCategoryDAO.listByQueryPageOrPageNull(queryPage);
    }
    
    return queryQuestionCommentCategoryDAO.listByQueryPage(queryPage);
  }
  
  /**
   * Lists page comment categories by query
   * 
   * @param queryPage query
   * @param onlyQueryScoped return only categories without a page
   * @return comment categories
   */
  public List<QueryQuestionCommentCategory> listCommentCategoriesByQuery(Query query, boolean onlyQueryScoped) {
    if (onlyQueryScoped) {
      return queryQuestionCommentCategoryDAO.listByQueryAndPageNull(query);
    }
    
    return queryQuestionCommentCategoryDAO.listByQuery(query);
  }
  
  /**
   * Creates new comment category
   * 
   * @param queryQuestionCommentCategory category
   * @param name name
   * @param lastModifier last modifier
   * @return updated comment category
   */
  public QueryQuestionCommentCategory updateCommentCategory(QueryQuestionCommentCategory queryQuestionCommentCategory, String name, User lastModifier) {
    return queryQuestionCommentCategoryDAO.updateName(queryQuestionCommentCategory, name, lastModifier);
  }
  
  /**
   * Find comment category
   * 
   * @param id id
   * @return comment category
   */
  public QueryQuestionCommentCategory findCommentCategory(Long id) {
    return queryQuestionCommentCategoryDAO.findById(id);
  }
  
  /**
   * Deletes a comment category
   * 
   * @param queryQuestionCommentCategory category
   */
  public void deleteCommentCategory(QueryQuestionCommentCategory queryQuestionCommentCategory) {
    queryQuestionCommentCategoryDAO.delete(queryQuestionCommentCategory);
  }
  
  /**
   * Returns live2d query page answers as list of scatter values
   * 
   * @param queryPage query page
   * @param queryReplies included replies
   * @return live2d query page answers as list of scatter values
   */
  public List<ScatterValue> getLive2dScatterValues(QueryPage queryPage, List<QueryReply> queryReplies) {
    QueryNumericField queryFieldX = queryNumericFieldDAO.findByQueryPageAndName(queryPage, LIVE2D_FIELD_NAME_X);
    QueryNumericField queryFieldY = queryNumericFieldDAO.findByQueryPageAndName(queryPage, LIVE2D_FIELD_NAME_Y);
    
    List<QueryQuestionNumericAnswer> answersX = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldX, queryReplies);
    List<QueryQuestionNumericAnswer> answersY = queryQuestionNumericAnswerDAO.listByQueryFieldAndRepliesIn(queryFieldY, queryReplies);
    
    Map<Long, Double> answerMapX = answersX.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
    Map<Long, Double> answerMapY = answersY.stream().collect(Collectors.toMap(answer -> answer.getQueryReply().getId(), QueryQuestionNumericAnswer::getData));
    Map<Long, Double[]> answerMap = queryReplies.stream().map(QueryReply::getId).collect(Collectors.toMap(queryReplyId -> queryReplyId, queryReplyId -> new Double[] { answerMapX.get(queryReplyId), answerMapY.get(queryReplyId) }));
    
    List<ScatterValue> scatterValues = new ArrayList<>();
    for (Map.Entry<Long, Double[]> answerMapEntry : answerMap.entrySet()) {
      Double[] values = answerMapEntry.getValue();
      if (values[0] != null && values[1] != null) {
        scatterValues.add(new ScatterValue(answerMapEntry.getKey(), values[0], values[1]));
      }
    }
    
    return scatterValues;
  }

  /**
   * Returns whether query page is archived
   * 
   * @param queryPage query page
   * @return whether query page is archived
   */
  public boolean isQueryPageArchived(QueryPage queryPage) {
    return queryPage == null || queryPage.getArchived();
  }

  /**
   * Returns whether query page is visible
   * 
   * @param queryPage query page
   * @return whether query page is visible
   */
  public boolean isQueryPageVisible(QueryPage queryPage) {
    if (isQueryPageArchived(queryPage)) {
      return false;
    }
    
    if (!queryPage.getVisible()) {
      return false;
    }
    
    return queryPage.getQuerySection().getVisible();
  }
  
  /**
   * Parses serialized string into a map
   * 
   * @param serializedData string
   * @return map
   */
  private NavigableMap<String, String> parseSerializedMap(String serializedData) {
    NavigableMap<String, String> parsedMap = new TreeMap<>();

    if (StringUtils.isNotBlank(serializedData)) {
      String[] keyValuePairs = serializedData.split("&");
      for (String keyValuePair : keyValuePairs) {
        String[] pair = keyValuePair.split("=");
        if (pair.length > 2) {
          throw new IllegalArgumentException("Malformed key value pair");
        }
        
        try {
          String key = URLDecoder.decode(pair[0], UTF_8);
          String value = pair.length == 1 ? null : URLDecoder.decode(pair[1], UTF_8);
          parsedMap.put(key, value);
        } catch (UnsupportedEncodingException e) {
          logger.error("Failed to encode string to UTF-8", e);
          return null;
        }
      }
    }
    
    return parsedMap;
  }

  /**
   * Serializes map into a string
   * 
   * @param map map
   * @return string
   */
  private String serializeMap(NavigableMap<String, String> map) {
    StringBuilder resultBuilder = new StringBuilder();
    
    Iterator<String> keyIterator = map.navigableKeySet().iterator();
    while (keyIterator.hasNext()) {
      String key = keyIterator.next();
      String value = map.get(key);
      
      try {
        resultBuilder.append(urlEncode(key));
        resultBuilder.append("=");
        if (StringUtils.isNotBlank(value))
          resultBuilder.append(urlEncode(value));
      } catch (UnsupportedEncodingException e) {
        logger.error("Failed to encode string to UTF-8", e);
        return null;
      }

      if (keyIterator.hasNext())
        resultBuilder.append("&");
    }
    
    return resultBuilder.toString();
  }
  
  /**
   * Parses serialized string into list
   * 
   * @param serializedData string
   * @return list
   */
  private List<String> parseSerializedList(String serializedData) {
    List<String> parsedList = new ArrayList<>();

    if (StringUtils.isNotBlank(serializedData)) {
      String[] values = serializedData.split("&");
      for (String value : values) {
        try {
          parsedList.add(URLDecoder.decode(value, UTF_8));
        }
        catch (UnsupportedEncodingException e) {
          logger.error("Failed to encode string to UTF-8", e);
          return null;
        }
      }
    }
    
    return parsedList;
  }

  /**
   * Serializes list into a string
   * 
   * @param list list
   * @return string
   */
  private String serializeList(List<String> list) {
    StringBuilder resultBuilder = new StringBuilder();
    
    Iterator<String> listIterator = list.iterator();
    while (listIterator.hasNext()) {
      String value = listIterator.next();
      try {
        resultBuilder.append(urlEncode(value));
      } catch (UnsupportedEncodingException e) {
        logger.error("Failed to encode string to UTF-8", e);
        return null;
      }
      
      if (listIterator.hasNext())
        resultBuilder.append("&");
    }
    
    return resultBuilder.toString();
  }
  
  /**
   * URL encoded a string
   * 
   * @param value value
   * @return encoded string
   * @throws UnsupportedEncodingException thrown when UTF-8 encoding is unsupported
   */
  private String urlEncode(String value) throws UnsupportedEncodingException {
    if (StringUtils.isNotBlank(value))
      return URLEncoder.encode(value, UTF_8).replace("+", "%20");
    else
      return null;
  }

}
