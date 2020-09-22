package fi.metatavu.edelphi.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentCategoryDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryScaleFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryTextFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionCommentCategory;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryScaleField;
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

  public static final String THESIS_TEXT_OPTION = "thesis.text";
  public static final String THESIS_DESCRIPTION_OPTION = "thesis.description";
  
  public static final String LIVE2D_VISIBLE_OPTION = "live2d.visible";
  public static final String LIVE2D_LABEL_OPTION_TEMPLATE = "live2d.label.%s";
  public static final String LIVE2D_COLOR_OPTION_TEMPLATE = "live2d.color.%s";
  public static final String OPTIONS_OPTION_TEMPLATE = "live2d.options.%s";
  
  public static final String MULTIPLE_1D_SCALES_LABEL_OPTION = "multiple1dscales.label";
  public static final String MULTIPLE_1D_SCALES_OPTIONS_OPTION = "multiple1dscales.options";
  public static final String MULTIPLE_1D_SCALES_THESES_OPTION = "multiple1dscales.theses";
  
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
  private QueryFieldDAO queryFieldDAO;

  @Inject
  private QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO;

  @Inject
  private QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO;

  @Inject
  private QueryOptionFieldOptionDAO queryOptionFieldOptionDAO;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private QueryTextFieldDAO queryTextFieldDAO;

  @Inject
  private QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO;

  @Inject
  private QueryOptionFieldDAO queryOptionFieldDAO;

  @Inject
  private QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO;

  @Inject
  private QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO;

  @Inject
  private QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO;

  @Inject
  private QueryScaleFieldDAO queryScaleFieldDAO;
  
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
   * Copies query page
   * 
   * @param queryPage query page to be copied
   * @param targetPanel target panel
   * @param sourcePanel source panel
   * @param newQuery query where the page will be copied
   * @param originalQueryReplies original query replies
   * @param newQuerySection new query section
   * @param copyAnswers whether to copy answers
   * @param copyComments whether to copy comments
   * @param replyMap old -> new reply map
   * @param queryCommentCategoryMap old -> new comment category map
   * @param copier copying user
   * @return copied query page
   */
  public QueryPage copyQueryPage(QueryPage queryPage, Panel targetPanel, Panel sourcePanel, Query newQuery, List<QueryReply> originalQueryReplies, QuerySection newQuerySection, Boolean copyAnswers, Boolean copyComments, Map<Long, QueryReply> replyMap, Map<Long, QueryQuestionCommentCategory> queryCommentCategoryMap, User copier) {
    QueryPage newQueryPage = queryPageDAO.create(copier, newQuerySection, queryPage.getPageType(), queryPage.getPageNumber(), queryPage.getTitle(), queryPage.getVisible());
    List<QueryPageSetting> queryPageSettings = queryPageSettingDAO.listByQueryPage(queryPage);
    for (QueryPageSetting queryPageSetting : queryPageSettings) {
      queryPageSettingDAO.create(queryPageSetting.getKey(), newQueryPage, queryPageSetting.getValue());
    }
    
    Map<Long, QueryQuestionCommentCategory> commentCategoryMap = new HashMap<>();
    queryCommentCategoryMap.entrySet().forEach(entry -> commentCategoryMap.put(entry.getKey(), entry.getValue()));
    
    // Copy page scoped comment categories
    
    List<QueryQuestionCommentCategory> pageCommentCategories = queryQuestionCommentCategoryDAO.listByQueryPage(queryPage);
    for (QueryQuestionCommentCategory pageCommentCategory : pageCommentCategories) {
      QueryQuestionCommentCategory newCategory = queryQuestionCommentCategoryDAO.create(newQuery, newQueryPage, pageCommentCategory.getName(), copier, copier, new Date(), new Date());
      commentCategoryMap.put(pageCommentCategory.getId(), newCategory);
    }
   
    // Comments
    
    if (copyComments) {
      HashMap<Long, Long> commentMap = new HashMap<>();
      List<QueryQuestionComment> queryComments;
      if (sourcePanel.getId().equals(targetPanel.getId())) {
        queryComments = queryQuestionCommentDAO.listByQueryPage(queryPage); 
      } else {
        queryComments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, sourcePanel.getCurrentStamp());
      }
      
      Collections.sort(queryComments, new Comparator<QueryQuestionComment>() {
        @Override
        public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
          return o1.getCreated().compareTo(o2.getCreated());
        }
      });
      
      for (QueryQuestionComment queryComment : queryComments) {
        QueryReply newReply = replyMap.get(queryComment.getQueryReply().getId());
        QueryQuestionComment copiedParentComment = null;
        
        if (queryComment.getParentComment() != null) {
          Long parentCommentId = queryComment.getParentComment().getId();  
          Long copiedParentCommentId = commentMap.get(parentCommentId);
          if (copiedParentCommentId != null) {
            copiedParentComment = queryQuestionCommentDAO.findById(copiedParentCommentId);
          } else {
            logger.error(String.format("Could not find %d from commentMap", parentCommentId));
          }
        }
        
        QueryQuestionCommentCategory newCategory = queryComment.getCategory() != null ? commentCategoryMap.get(queryComment.getCategory().getId()) : null;
        
        QueryQuestionComment newComment = queryQuestionCommentDAO.create(
            newReply,
            newQueryPage,
            copiedParentComment,
            newCategory,
            queryComment.getComment(),
            queryComment.getHidden(),
            queryComment.getCreator(),
            queryComment.getCreated(),
            queryComment.getLastModifier(),
            queryComment.getLastModified());
        commentMap.put(queryComment.getId(), newComment.getId());
      }
    }
    
    // Fields and (optionally) answers
    
    List<QueryField> queryFields = queryFieldDAO.listByQueryPage(queryPage);
    for (QueryField queryField : queryFields) {
      QueryField newQueryField = null;
      switch (queryField.getType()) {
      
        // Text fields
      
        case TEXT:
          newQueryField = queryTextFieldDAO.create(newQueryPage, queryField.getName(), queryField.getMandatory(), queryField.getCaption());
          if (copyAnswers) {
            for (QueryReply originalQueryReply : originalQueryReplies) {
              QueryQuestionTextAnswer answer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(originalQueryReply, queryField);
              if (answer != null) {
                QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                queryQuestionTextAnswerDAO.create(newQueryReply, newQueryField, answer.getData());
              }
            }
          }
          break;

        // Option fields

        case OPTIONFIELD:
          QueryOptionField optionField = (QueryOptionField) queryField;
          newQueryField = queryOptionFieldDAO.create(newQueryPage, optionField.getName(), optionField.getMandatory(), optionField.getCaption());
          List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(optionField);
          Map<Long, Long> optionMap = new HashMap<>();
          for (QueryOptionFieldOption option : options) {
            QueryOptionFieldOption newOption = queryOptionFieldOptionDAO.create((QueryOptionField) newQueryField, option.getText(), option.getValue());
            optionMap.put(option.getId(), newOption.getId());
          }
          Map<Long, QueryOptionFieldOptionGroup> optionGroupMap = new HashMap<>();
          List<QueryOptionFieldOptionGroup> groups = queryOptionFieldOptionGroupDAO.listByQueryField(optionField);
          for (QueryOptionFieldOptionGroup group : groups) {
            QueryOptionFieldOptionGroup newGroup = queryOptionFieldOptionGroupDAO.create((QueryOptionField) newQueryField, group.getName());
            optionGroupMap.put(group.getId(), newGroup);
          }
          if (copyAnswers) {
            for (QueryReply originalQueryReply : originalQueryReplies) {
              QueryQuestionMultiOptionAnswer multiAnswer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(originalQueryReply, queryField);
              if (multiAnswer != null) {
                // QueryQuestionMultiOptionAnswer
                QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                Set<QueryOptionFieldOption> newOptions = new HashSet<>();
                for (QueryOptionFieldOption option : multiAnswer.getOptions()) {
                  QueryOptionFieldOption newOption = queryOptionFieldOptionDAO.findById(optionMap.get(option.getId()));
                  newOptions.add(newOption);
                }
                queryQuestionMultiOptionAnswerDAO.create(newQueryReply, newQueryField, newOptions);
              }
              else {
                // QueryQuestionOptionGroupOptionAnswer
                List<QueryQuestionOptionGroupOptionAnswer> groupAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryField(originalQueryReply, queryField);
                if (!groupAnswers.isEmpty()) {
                  for (QueryQuestionOptionGroupOptionAnswer groupAnswer : groupAnswers) {
                    QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                    QueryOptionFieldOption newOption = queryOptionFieldOptionDAO.findById(optionMap.get(groupAnswer.getOption().getId()));
                    QueryOptionFieldOptionGroup newGroup = optionGroupMap.get(groupAnswer.getGroup().getId());
                    queryQuestionOptionGroupOptionAnswerDAO.create(newQueryReply, newQueryField, newOption, newGroup);
                  }
                }
                else {
                  // QueryQuestionOptionAnswer
                  List<QueryQuestionOptionAnswer> optionAnswers = queryQuestionOptionAnswerDAO.listByQueryReplyAndQueryField(originalQueryReply, queryField);
                  for (QueryQuestionOptionAnswer optionAnswer : optionAnswers) {
                    QueryOptionFieldOption newOption = queryOptionFieldOptionDAO.findById(optionMap.get(optionAnswer.getOption().getId()));
                    QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                    queryQuestionOptionAnswerDAO.create(newQueryReply, newQueryField, newOption);
                  }
                }
              }
            }
          }
          break;
          
        // Numeric scale fields

        case NUMERIC_SCALE:
          QueryScaleField scaleField = (QueryScaleField) queryField;
          newQueryField = queryScaleFieldDAO.create(newQueryPage, scaleField.getName(), scaleField.getMandatory(), scaleField.getCaption(),
              scaleField.getMin(), scaleField.getMax(), scaleField.getPrecision(), scaleField.getStep());
          if (copyAnswers) {
            for (QueryReply originalQueryReply : originalQueryReplies) {
              QueryQuestionNumericAnswer answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(originalQueryReply, queryField);
              if (answer != null) {
                QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                queryQuestionNumericAnswerDAO.create(newQueryReply, newQueryField, answer.getData());
              }
            }
          }
          break;
          
        // Numeric fields
          
        case NUMERIC:
          QueryNumericField numericField = (QueryNumericField) queryField;
          newQueryField = queryNumericFieldDAO.create(newQueryPage, numericField.getName(), numericField.getMandatory(),
              numericField.getCaption(), numericField.getMin(), numericField.getMax(), numericField.getPrecision());
          if (copyAnswers) {
            for (QueryReply originalQueryReply : originalQueryReplies) {
              QueryQuestionNumericAnswer answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(originalQueryReply, queryField);
              if (answer != null) {
                QueryReply newQueryReply = replyMap.get(originalQueryReply.getId());
                queryQuestionNumericAnswerDAO.create(newQueryReply, newQueryField, answer.getData());
              }
            }
          }
          break;
      }
    }
    
    return newQueryPage;
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
  public boolean getBooleanSetting(QueryPage queryPage, String name) {
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
      return queryQuestionCommentCategoryDAO.listByQueryPageOrPageQuery(queryPage);
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
   * Returns multiple scale1d query page answers as two dimensional array where first level is thesis, second option and the value answer count
   * 
   * @param queryPage query page
   * @param queryReplies query replies
   * @return multiple scale1d query page answers as two dimensional array 
   */
  public double[][] getMultipleScale1dValues(QueryPage queryPage, List<QueryReply> queryReplies) {
    List<String> pageOptions = getListSetting(queryPage, MULTIPLE_1D_SCALES_OPTIONS_OPTION);
    List<String> pageTheses = getListSetting(queryPage, MULTIPLE_1D_SCALES_THESES_OPTION);
    
    int thesesCount = pageTheses.size();
    int optionCount = pageOptions.size();
    
    double[][] result = new double[thesesCount][optionCount];
    
    for (int thesisIndex = 0; thesisIndex < thesesCount; thesisIndex++) {
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getMultiple1dScalesFieldName(thesisIndex));
      
      for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
        QueryOptionFieldOption fieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, String.valueOf(optionIndex));
        Long count = queryQuestionOptionAnswerDAO.countByOptionAndReplyIn(fieldOption, queryReplies);
        result[thesisIndex][optionIndex] = count.doubleValue();
      }
    }
    
    return result;
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
   * Lists query pages within section
   * 
   * @param querySection section
   * @return query pages within section
   */
  public List<QueryPage> listQueryPagesBySection(QuerySection querySection) {
    return queryPageDAO.listByQuerySection(querySection);
  }
  
  /**
   * Lists query pages in a query
   * @param query query
   * @return query pages
   */
  public List<QueryPage> listQueryPages(Query query) {
    return queryPageDAO.listByQuery(query);
  }

  /**
   * Lists query pages in a query
   * @param query query
   * @param pageType page type
   * @return query pages
   */
  public List<QueryPage> listQueryPagesByType(Query query, QueryPageType pageType) {
    return queryPageDAO.listByQueryAndType(query, pageType);
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
  /**
   * Returns field name for given index
   * 
   * @param index index
   * @return field name
   */
  private String getMultiple1dScalesFieldName(int index) {
    return String.format("multiple1dscales.%d", index);
  }

  
}
