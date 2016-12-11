package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;

public class Multiple2DScalesThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  private static final String LABEL_X_OPTION = "multiple2dscales.labelx";
  private static final String LABEL_Y_OPTION = "multiple2dscales.labely";
  private static final String OPTIONS_OPTION = "multiple2dscales.options";
  private static final String THESES_OPTION = "multiple2dscales.theses";

  private List<QueryOption> options = new ArrayList<>();

  public Multiple2DScalesThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_X_OPTION, "panelAdmin.block.query.multiple2dScales.options.labelX", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_Y_OPTION, "panelAdmin.block.query.multiple2dScales.options.labelY", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, OPTIONS_OPTION, "panelAdmin.block.query.multiple2dScales.options.options", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, THESES_OPTION, "panelAdmin.block.query.multiple2dScales.options.theses", QueryOptionEditor.OPTION_SET, false));
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("multiple_2dscales");

    String pageLabelX = getStringOptionValue(queryPage, getDefinedOption(LABEL_X_OPTION));
    String pageLabelY = getStringOptionValue(queryPage, getDefinedOption(LABEL_Y_OPTION));
    List<String> pageOptions = getListOptionValue(queryPage, getDefinedOption(OPTIONS_OPTION));
    List<String> pageTheses = getListOptionValue(queryPage, getDefinedOption(THESES_OPTION));
    List<String> selectedX = new ArrayList<>(pageTheses.size());
    List<String> selectedY = new ArrayList<>(pageTheses.size());
    
    for (int thesisIndex = 0, thesisCount = pageTheses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
      QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);
      QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
      QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

      selectedX.add(answerX != null ? answerX.getOption().getValue() : "NA");
      selectedY.add(answerY != null ? answerY.getOption().getValue() : "NA");
    }    
    
    addFragmentStringList(requiredFragment, "thesis", pageTheses);
    addFragmentStringList(requiredFragment, "option", pageOptions);
    addFragmentStringList(requiredFragment, "selectedX", selectedX);
    addFragmentStringList(requiredFragment, "selectedY", selectedY);
    
    requiredFragment.addAttribute("labelX", pageLabelX);
    requiredFragment.addAttribute("labelY", pageLabelY);
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    List<String> theses = getListOptionValue(queryPage, getDefinedOption(THESES_OPTION));
    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      String fieldValueX = requestContext.getString(fieldNameX);
      String fieldValueY = requestContext.getString(fieldNameY);
      saveAnswer(requestContext, queryPage, queryReply, fieldNameX, fieldValueX);
      saveAnswer(requestContext, queryPage, queryReply, fieldNameY, fieldValueY);
    }    
  }

  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    // Live reports are not supported by multiple 2d scale pages
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    boolean commentable = Boolean.TRUE.equals(this.getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable")));    
    
    List<String> theses = getListOptionValue(queryPage, getDefinedOption(THESES_OPTION));
    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      
      QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
      QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

      Messages messages = Messages.getInstance();
      Locale locale = exportContext.getLocale();

      int columnIndexX = exportContext.addColumn(String.format("%s/%s", queryPage.getTitle(), queryFieldX.getCaption()));
      int columnIndexY = exportContext.addColumn(String.format("%s/%s", queryPage.getTitle(), queryFieldY.getCaption()));
      int commentColumnIndex = commentable ? exportContext.addColumn(queryPage.getTitle() + "/" + messages.getText(locale, "panelAdmin.query.export.comment")) : -1; 
      
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

        exportContext.addCellValue(queryReply, columnIndexX, answerX != null ? answerX.getOption().getText() : null);
        exportContext.addCellValue(queryReply, columnIndexY, answerY != null ? answerY.getOption().getText() : null);

        if (commentable) {
          QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
          exportContext.addCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
        }
      }
    }
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
    String labelX = settings.get(LABEL_X_OPTION);
    String labelY = settings.get(LABEL_Y_OPTION);
    List<String> fieldOptions = QueryPageUtils.parseSerializedList(settings.get(OPTIONS_OPTION));
    List<String> theses = QueryPageUtils.parseSerializedList(thesesOption);

    for (int thesisIndex = 0, thesisCount = theses.size(); thesisIndex < thesisCount; thesisIndex++) {
      String thesis = theses.get(thesisIndex);
      
      String fieldNameX = getFieldName(thesisIndex, "x");
      String fieldNameY = getFieldName(thesisIndex, "y");
      String fieldLabelX = String.format("%s/%s", thesis, labelX);
      String fieldLabelY = String.format("%s/%s", thesis, labelY);
      Boolean mandatory = false;
      
      if (hasAnswers) {
        synchronizeFieldCaption(queryPage, fieldNameX, fieldLabelX);
        synchronizeFieldCaption(queryPage, fieldNameY, fieldLabelY);
      } else {
        synchronizeField(queryPage, fieldOptions, fieldNameX, fieldLabelX, mandatory);
        synchronizeField(queryPage, fieldOptions, fieldNameY, fieldLabelY, mandatory);
      }
    }
  }
  
  private void addFragmentStringList(RequiredQueryFragment requiredFragment, String name, List<String> list) {
    requiredFragment.addAttribute(String.format("%sCount", name), String.valueOf(list.size()));
    for (int i = 0, l = list.size(); i < l; i++) {
      requiredFragment.addAttribute(String.format("%s.%d", name, i), list.get(i));
    }
  }

  private String getFieldName(int index, String axis) {
    return String.format("multiple2dscales.%d.%s", index, axis);
  }

}