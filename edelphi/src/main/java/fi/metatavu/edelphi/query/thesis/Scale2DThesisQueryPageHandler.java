package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.QueryUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class Scale2DThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  private static final Logger logger = Logger.getLogger(Scale2DThesisQueryPageHandler.class.getName());

  private static final String VALUE_Y = "valueY";
  private static final String VALUE_X = "valueX";
  private static final String FIELD_Y = "y";
  private static final String FIELD_X = "x";
  private static final String SCALE2D_OPTIONS_Y = "scale2d.options.y";
  private static final String SCALE2D_OPTIONS_X = "scale2d.options.x";
  private static final String SCALE2D_TYPE = "scale2d.type";
  private static final String SCALE2D_LABEL_Y = "scale2d.label.y";
  private static final String SCALE2D_LABEL_X = "scale2d.label.x";
  
  public Scale2DThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, SCALE2D_LABEL_X, "panelAdmin.block.query.scale2DXLabelOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, SCALE2D_LABEL_Y, "panelAdmin.block.query.scale2DYLabelOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, SCALE2D_TYPE, "panelAdmin.block.query.scale2DTypeOptionLabel", QueryOptionEditor.SCALE2D_TYPE, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, SCALE2D_OPTIONS_X, "panelAdmin.block.query.scale2DOptionsXOptionLabel", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, SCALE2D_OPTIONS_Y, "panelAdmin.block.query.scale2DOptionsYOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);
    String valueX = requestContext.getString(VALUE_X);
    String valueY = requestContext.getString(VALUE_Y);
    
    saveAnswer(requestContext, queryPage, queryReply, fieldNameX, valueX);
    saveAnswer(requestContext, queryPage, queryReply, fieldNameY, valueY);
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    int type = getIntegerOptionValue(queryPage, getDefinedOption(SCALE2D_TYPE));

    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
    QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

    String labelX = getStringOptionValue(queryPage, getDefinedOption(SCALE2D_LABEL_X));
    String labelY = getStringOptionValue(queryPage, getDefinedOption(SCALE2D_LABEL_Y));
    
    if (type == SCALE_TYPE_RADIO) {
      renderRadioList(requestContext, VALUE_X, labelX, queryFieldX, answerX);
      renderRadioList(requestContext, VALUE_Y, labelY, queryFieldY, answerY);
    } else if (type == SCALE_TYPE_SLIDER) {
      renderSlider(requestContext, VALUE_X, labelX, queryFieldX, answerX);
      renderSlider(requestContext, VALUE_Y, labelY, queryFieldY, answerY);
    } else if (type == SCALE_TYPE_GRAPH) {
      renderGraph(requestContext, VALUE_X, VALUE_Y, labelX, labelY, queryFieldX, queryFieldY, answerX, answerY);
    }
  }

  private void renderGraph(PageRequestContext requestContext, String nameX, String nameY, String labelX, String labelY, QueryOptionField queryFieldX, QueryOptionField queryFieldY, QueryQuestionOptionAnswer answerX, QueryQuestionOptionAnswer answerY) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("scale_graph");

    List<QueryOptionFieldOption> optionsX = QueryUtils.listQueryOptionFieldOptions(queryFieldX);
    List<QueryOptionFieldOption> optionsY = QueryUtils.listQueryOptionFieldOptions(queryFieldY);

    if (answerX != null) {
      requiredFragment.addAttribute(VALUE_X, answerX.getOption().getValue());
    } else {
      if (!optionsX.isEmpty())
        requiredFragment.addAttribute(VALUE_X, optionsX.get(0).getValue());
    }

    if (answerY != null) {
      requiredFragment.addAttribute(VALUE_Y, answerY.getOption().getValue());
    } else {
      if (!optionsY.isEmpty())
        requiredFragment.addAttribute(VALUE_Y, optionsY.get(0).getValue());
    }
    
    int i = 0;
    for (QueryOptionFieldOption option : optionsX) {
      addJsDataVariable(requestContext, "scale_graph.options.x." + i + ".value", option.getValue());
      addJsDataVariable(requestContext, "scale_graph.options.x." + i + ".text", option.getText());
      i++;
    }
    
    addJsDataVariable(requestContext, "scale_graph.options.x.count", String.valueOf(optionsX.size()));
    addJsDataVariable(requestContext, "scale_graph.options.x.label", labelX);
    
    i = 0;
    for (QueryOptionFieldOption option : optionsY) {
      addJsDataVariable(requestContext, "scale_graph.options.y." + i + ".value", option.getValue());
      addJsDataVariable(requestContext, "scale_graph.options.y." + i + ".text", option.getText());
      i++;
    }
    
    addJsDataVariable(requestContext, "scale_graph.options.y.count", String.valueOf(optionsY.size()));
    addJsDataVariable(requestContext, "scale_graph.options.y.label", labelY);
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);
    String labelX = settings.get(getDefinedOption(SCALE2D_LABEL_X).getName());
    String labelY = settings.get(getDefinedOption(SCALE2D_LABEL_Y).getName());

    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }

    if (!hasAnswers) {
      QueryOption optionsOptionX = getDefinedOption(SCALE2D_OPTIONS_X);
      QueryOption optionsOptionY = getDefinedOption(SCALE2D_OPTIONS_Y);
  
      // TODO: Mandarory ???
  
      Boolean mandatory = false;
  
      synchronizeField(settings, queryPage, optionsOptionX, fieldNameX, labelX, mandatory);
      synchronizeField(settings, queryPage, optionsOptionY, fieldNameY, labelY, mandatory);
    } else {
      synchronizeFieldCaption(queryPage, fieldNameX, labelX);
      synchronizeFieldCaption(queryPage, fieldNameY, labelY);
    }
  }

  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    boolean commentable = Boolean.TRUE.equals(this.getBooleanOptionValue(queryPage,  getDefinedOption("thesis.commentable")));    
    
    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);
    
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    Messages messages = Messages.getInstance();
    Locale locale = exportContext.getLocale();

    int columnIndexX = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldX.getCaption());
    int columnIndexY = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldY.getCaption());
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
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_bubblechart");
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    UserDAO userDAO = new UserDAO();

    Query query = queryPage.getQuerySection().getQuery();
    String fieldNameX = getFieldName(FIELD_X);
    String fieldNameY = getFieldName(FIELD_Y);
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);
    
    if (queryFieldX == null || queryFieldY == null) {
      logger.severe(String.format("QueryField missing, could not render report for query page %d", queryPage.getId()));
      return;
    }
    
    List<QueryOptionFieldOption> optionsX = QueryUtils.listQueryOptionFieldOptions(queryFieldX);
    List<QueryOptionFieldOption> optionsY = QueryUtils.listQueryOptionFieldOptions(queryFieldY);
    
    int maxX = 0;
    int maxY = 0;
    
    List<String> xTickLabels = new ArrayList<>();
    
    for (QueryOptionFieldOption optionX : optionsX) {
      int x = NumberUtils.createInteger(optionX.getValue());
      maxX = Math.max(maxX, x);
      xTickLabels.add(optionX.getText());
    }

    List<String> yTickLabels = new ArrayList<>();
    for (QueryOptionFieldOption optionY : optionsY) {
      int y = NumberUtils.createInteger(optionY.getValue());
      maxY = Math.max(maxY, y);
      yTickLabels.add(optionY.getText());
    }
    
    maxX++;
    maxY++;
    
    Double[][] values = new Double[maxX][];
    for (int x = 0; x < maxX; x++) {
      values[x] = new Double[maxY];
    }
    
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, query);

    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(query, RequestUtils.getActiveStamp(requestContext));
    for (QueryReply queryReply : queryReplies) {
      if ((excludeReply == null)||(!queryReply.getId().equals(excludeReply.getId()))) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);
        
        if (answerX != null && answerY != null) {
          int x = NumberUtils.createInteger(answerX.getOption().getValue());
          int y = NumberUtils.createInteger(answerY.getOption().getValue());
          
          values[x][y] = values[x][y] != null ? values[x][y] + 1 : 1; 
        }
      }
    }

    requiredFragment.addAttribute("xAxisLabel", queryFieldX.getCaption());
    requiredFragment.addAttribute("yAxisLabel", queryFieldY.getCaption());
    requiredFragment.addAttribute("xValueCount", String.valueOf(maxX));
    requiredFragment.addAttribute("yValueCount", String.valueOf(maxY));
    
    for (int i = 0, l = xTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("xTickLabel." + i, xTickLabels.get(i)); 
    }
    
    for (int i = 0, l = yTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("yTickLabel." + i, yTickLabels.get(i)); 
    }
    
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (values[x][y] != null) {
          requiredFragment.addAttribute("bubble." + x + "." + y + ".x", String.valueOf((x)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".y", String.valueOf((y)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".value", String.valueOf(values[x][y]));
        }
      }
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}