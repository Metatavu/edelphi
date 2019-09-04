package fi.metatavu.edelphi.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

public abstract class AbstractQueryPageHandler implements QueryPageHandler {

  public AbstractQueryPageHandler() {
    // TODO: Localize default
    options.add(new QueryOption(QueryOptionType.PAGE, "title", "panelAdmin.block.query.pageTitleOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.PAGE, "visible", "panelAdmin.block.query.pageVisibleOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    String title = settings.get("title");
    Boolean visible = "1".equals(settings.get("visible"));
    
    queryPageDAO.updateTitle(queryPage, StringUtils.abbreviate(title, SystemUtils.MAX_QUERY_PAGE_TITLE), modifier);
    queryPageDAO.updateVisible(queryPage, visible, modifier);
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    return options;
  }

  protected void addRequiredFragment(PageRequestContext requestContext, RequiredQueryFragment requiredFragment) {
    @SuppressWarnings("unchecked")
    List<RequiredQueryFragment> requiredFragments = (List<RequiredQueryFragment>) requestContext.getRequest().getAttribute("requiredQueryFragments");
    if (requiredFragments == null) {
      requiredFragments = new ArrayList<RequiredQueryFragment>();
      requestContext.getRequest().setAttribute("requiredQueryFragments", requiredFragments);
    }
    
    requiredFragments.add(requiredFragment);
  }

  protected void addJsDataVariable(PageRequestContext pageRequestContext, String name, String value) {
    @SuppressWarnings("unchecked")
    Map<String, String> jsData = (Map<String, String>) pageRequestContext.getRequest().getAttribute("jsData");
    if (jsData == null) {
      jsData = new HashMap<String, String>();
      pageRequestContext.getRequest().setAttribute("jsData", jsData);
    }
    
    jsData.put("queryFragment." + name, value);
  }

  protected void addJsDataVariable(PageRequestContext pageRequestContext, QueryOption queryOption, String value) {
    @SuppressWarnings("unchecked")
    Map<String, String> jsData = (Map<String, String>) pageRequestContext.getRequest().getAttribute("jsData");
    if (jsData == null) {
      jsData = new HashMap<String, String>();
      pageRequestContext.getRequest().setAttribute("jsData", jsData);
    }
    
    jsData.put("queryFragment." + queryOption.getName(), value);
  }
  
  protected QueryOption getDefinedOption(String name) {
    List<QueryOption> definedOptions = getDefinedOptions();
    for (QueryOption definedOption : definedOptions) {
      if (name.equals(definedOption.getName()))
        return definedOption;
    }
    
    return null;
  }
  
  protected String getStringOptionValue(QueryPage queryPage, QueryOption queryOption) {
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(queryOption.getName());
    if (key != null) {
      QueryPageSetting setting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage);
      if (setting != null)
        return setting.getValue();
    }
    
    return null;
  }
  
  protected NavigableMap<String, String> getMapOptionValue(QueryPage queryPage, QueryOption queryOption) {
    String rawValue = getStringOptionValue(queryPage, queryOption);
    return QueryPageUtils.parseSerializedMap(rawValue);
  }
  
  protected List<String> getListOptionValue(QueryPage queryPage, QueryOption queryOption) {
    String rawValue = getStringOptionValue(queryPage, queryOption);
    return QueryPageUtils.parseSerializedList(rawValue);
  }
  
  protected Boolean getBooleanOptionValue(QueryPage queryPage, QueryOption queryOption) {
    String value = getStringOptionValue(queryPage, queryOption);
    return "1".equals(value);
  }
  
  protected Long getLongOptionValue(QueryPage queryPage, QueryOption queryOption) {
    return NumberUtils.createLong(getStringOptionValue(queryPage, queryOption));
  }
  
  protected Integer getIntegerOptionValue(QueryPage queryPage, QueryOption queryOption) {
    return NumberUtils.createInteger(getStringOptionValue(queryPage, queryOption));
  }
  
  protected Double getDoubleOptionValue(QueryPage queryPage, QueryOption queryOption) {
    return NumberUtils.createDouble(getStringOptionValue(queryPage, queryOption));
  }
  
  /**
   * Renders comment list and comment editor
   * 
   * @param requestContext request context
   * @param queryPage query page
   * @param queryReply query reply
   * @param commentable whether to render comment editor
   * @param viewDiscussion whether to render comment list
   */
  protected void renderComments(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply, boolean commentable, boolean viewDiscussion) {
    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
    
    RequiredQueryFragment queryFragment = new RequiredQueryFragment("comments");
    queryFragment.addAttribute("panelId", panel.getId().toString());
    queryFragment.addAttribute("queryId", query.getId().toString());
    queryFragment.addAttribute("pageId", queryPage.getId().toString());
    
    if (queryReply != null) {
      queryFragment.addAttribute("queryReplyId", queryReply.getId().toString());
    }
    
    queryFragment.addAttribute("queryPageCommentable", commentable ? "true" : "false");
    queryFragment.addAttribute("queryViewDiscussion", viewDiscussion ? "true" : "false");
    addRequiredFragment(requestContext, queryFragment);  
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}
