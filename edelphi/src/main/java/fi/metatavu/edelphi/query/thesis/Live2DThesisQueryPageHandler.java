package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class Live2DThesisQueryPageHandler extends AbstractThesisQueryPageHandler {

  private static final String LABEL_X_OPTION = "live2d.labelx";
  private static final String COLOR_X_OPTION = "live2d.colorx";
  private static final String LABEL_Y_OPTION = "live2d.labely";
  private static final String COLOR_Y_OPTION = "live2d.colory";
  private static final String MIN_OPTION = "live2d.min";
  private static final String MAX_OPTION = "live2d.max";
  private static final String PRECISION_OPTION = "live2d.precision";
  private static final String FIELD_X = "x";
  private static final String FIELD_Y = "y";

  private List<QueryOption> options = new ArrayList<>();

  public Live2DThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_X_OPTION, "panelAdmin.block.query.live2d.options.labelX", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, COLOR_X_OPTION, "panelAdmin.block.query.live2d.options.colorX", QueryOptionEditor.COLOR, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, LABEL_Y_OPTION, "panelAdmin.block.query.live2d.options.labelY", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, COLOR_Y_OPTION, "panelAdmin.block.query.live2d.options.colorY", QueryOptionEditor.COLOR, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, MIN_OPTION, "panelAdmin.block.query.live2d.options.min", QueryOptionEditor.FLOAT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, MAX_OPTION, "panelAdmin.block.query.live2d.options.max", QueryOptionEditor.FLOAT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, PRECISION_OPTION, "panelAdmin.block.query.live2d.options.precision", QueryOptionEditor.FLOAT, true));
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("live2d");

    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);

    requiredFragment.addAttribute("panelId", panel.getId().toString());
    requiredFragment.addAttribute("queryId", query.getId().toString());
    requiredFragment.addAttribute("pageId", queryPage.getId().toString());
    requiredFragment.addAttribute("queryReplyId", queryReply.getId().toString());
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    // Live 2d answers are saved with API
  }

  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    // Live reports are not supported by live 2d pages
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    /**
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

      int columnIndexX = exportContext.addColumn(getColumnLabel(queryPage.getTitle(), queryFieldX.getCaption()));
      int columnIndexY = exportContext.addColumn(getColumnLabel(queryPage.getTitle(), queryFieldY.getCaption()));
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
    } */
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
    
    String labelX = settings.get(LABEL_X_OPTION);
    String labelY = settings.get(LABEL_Y_OPTION);
    Double min = NumberUtils.createDouble(settings.getOrDefault(MIN_OPTION, "0"));
    Double max = NumberUtils.createDouble(settings.getOrDefault(MAX_OPTION, "100"));
    Double precision = NumberUtils.createDouble(settings.getOrDefault(PRECISION_OPTION, "100"));
    
    synchronizeField(queryPage, FIELD_X, labelX, min, max, precision, hasAnswers);
    synchronizeField(queryPage, FIELD_Y, labelY, min, max, precision, hasAnswers);
  }
  
  private void synchronizeField(QueryPage queryPage, String fieldName, String label, Double min, Double max, Double precision, Boolean hasAnswers) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
    
    QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    
    if (queryNumericField != null) {
      queryFieldDAO.updateCaption(queryNumericField, label);
      if (!hasAnswers) {
        queryNumericFieldDAO.updateMin(queryNumericField, min);
        queryNumericFieldDAO.updateMax(queryNumericField, max);
        queryNumericFieldDAO.updatePrecision(queryNumericField, precision);
      }
    } else {
      queryNumericFieldDAO.create(queryPage, fieldName, Boolean.TRUE, label, min, max, precision);
    }
  }
  
  private void addFragmentStringList(RequiredQueryFragment requiredFragment, String name, List<String> list) {
    requiredFragment.addAttribute(String.format("%sCount", name), String.valueOf(list.size()));
    for (int i = 0, l = list.size(); i < l; i++) {
      requiredFragment.addAttribute(String.format("%s.%d", name, i), list.get(i));
    }
  }

  private String getFieldName(String axis) {
    return String.format("live2d.%s", axis);
  }
  
  private String getFieldLabel(String thesis, String label) {
    return String.format("%s/%s", thesis, label);
  }
  
  private String getColumnLabel(String pageTitle, String fieldCaption) {
    return String.format("%s/%s", pageTitle, fieldCaption);
  }

}