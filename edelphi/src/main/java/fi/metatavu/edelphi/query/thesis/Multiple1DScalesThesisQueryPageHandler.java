package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

/**
 * Page handler for multiple 1d scales query page
 * 
 * @author Antti Leppä
 */
public class Multiple1DScalesThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  private static final String LABEL_OPTION = "multiple1dscales.label";
  private static final String OPTIONS_OPTION = "multiple1dscales.options";
  private static final String THESES_OPTION = "multiple1dscales.theses";

  private List<QueryOption> options = new ArrayList<>();

  /**
   * Constructor
   */
  public Multiple1DScalesThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_OPTION, "panelAdmin.block.query.multiple1dScales.options.label", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, OPTIONS_OPTION, "panelAdmin.block.query.multiple1dScales.options.options", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, THESES_OPTION, "panelAdmin.block.query.multiple1dScales.options.theses", QueryOptionEditor.OPTION_SET, false));
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("multiple_1dscales");

    String pageLabel = getStringOptionValue(queryPage, getDefinedOption(LABEL_OPTION));
    List<String> pageOptions = getListOptionValue(queryPage, getDefinedOption(OPTIONS_OPTION));
    List<String> pageTheses = getListOptionValue(queryPage, getDefinedOption(THESES_OPTION));
    List<String> selected = new ArrayList<>(pageTheses.size());

    for (int thesisIndex = 0, thesisCount = pageTheses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldName = getFieldName(thesisIndex);
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
      selected.add(answer != null ? answer.getOption().getValue() : "NA");
    }    
    
    addFragmentStringList(requiredFragment, "thesis", pageTheses);
    addFragmentStringList(requiredFragment, "option", pageOptions);
    addFragmentStringList(requiredFragment, "selected", selected);
    
    requiredFragment.addAttribute("label", pageLabel);
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    List<String> theses = getListOptionValue(queryPage, getDefinedOption(THESES_OPTION));
    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldName = getFieldName(thesisIndex);
      String fieldValue = requestContext.getString(fieldName);
      saveAnswer(requestContext, queryPage, queryReply, fieldName, fieldValue);
    }    
  }

  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    // Live reports are not supported by multiple 1d scale pages
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    // Data export is done on Multiple1DScalesQueryPageSpreadsheetExporter 
  }

  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> result = new ArrayList<>(super.getDefinedOptions());
    result.addAll(this.options);
    return result;
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    for (QueryOption queryOption : getDefinedOptions()) {
      if ((queryOption.getType() == QueryOptionType.QUESTION) && ((!hasAnswers) || (queryOption.isEditableWithAnswers()))) {
        QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
    

    String thesesOption = settings.get(THESES_OPTION);
    String label = settings.get(LABEL_OPTION);
    List<String> fieldOptions = QueryPageUtils.parseSerializedList(settings.get(OPTIONS_OPTION));
    List<String> theses = QueryPageUtils.parseSerializedList(thesesOption);

    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String thesis = theses.get(thesisIndex);
      
      String fieldName = getFieldName(thesisIndex);
      String fieldLabel = getFieldLabel(thesis, label);
      Boolean mandatory = false;
      
      if (hasAnswers) {
        synchronizeFieldCaption(queryPage, fieldName, fieldLabel);
      } else {
        synchronizeField(queryPage, fieldOptions, fieldName, fieldLabel, mandatory);
      }
    }
  }
  
  /**
   * Adds a string list parameter into page fragment
   * 
   * @param requiredFragment parameter
   * @param name name
   * @param list list
   */
  private void addFragmentStringList(RequiredQueryFragment requiredFragment, String name, List<String> list) {
    requiredFragment.addAttribute(String.format("%sCount", name), String.valueOf(list.size()));
    for (int i = 0, l = list.size(); i < l; i++) {
      requiredFragment.addAttribute(String.format("%s.%d", name, i), list.get(i));
    }
  }

  /**
   * Returns field name for given index
   * 
   * @param index index
   * @return field name
   */
  private String getFieldName(int index) {
    return String.format("multiple1dscales.%d", index);
  }
  
  /**
   * Returns field label for given thesis and label
   * 
   * @param thesis thesis
   * @param label label
   * @return field label
   */
  private String getFieldLabel(String thesis, String label) {
    return StringUtils.abbreviate(String.format("%s/%s", thesis, label), SystemUtils.MAX_QUERY_FIELD_CAPTION);
  }
  
}