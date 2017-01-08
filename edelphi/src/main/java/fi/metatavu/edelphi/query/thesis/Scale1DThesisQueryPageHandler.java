package fi.metatavu.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
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
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.ReportUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class Scale1DThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {
  
  private static final Logger logger = Logger.getLogger(Scale1DThesisQueryPageHandler.class.getName());

  public Scale1DThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale1d.label", "panelAdmin.block.query.scale1DLabelOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale1d.type", "panelAdmin.block.query.scale1DTypeOptionLabel", QueryOptionEditor.SCALE1D_TYPE, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale1d.options", "panelAdmin.block.query.scale1DOptionsOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }
  
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String fieldName = getFieldName();
    String value = requestContext.getString("value");
    saveAnswer(requestContext, queryPage, queryReply, fieldName, value);
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    int type = getIntegerOptionValue(queryPage, getDefinedOption("scale1d.type"));
    String fieldName = getFieldName();
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
    String label = getStringOptionValue(queryPage, getDefinedOption("scale1d.label"));
    
    if (type == SCALE_TYPE_RADIO) {
      renderRadioList(requestContext, "value", label, queryField, answer);
    } else if (type == SCALE_TYPE_SLIDER) {
      renderSlider(requestContext, "value", label, queryField, answer);
    }
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    String fieldName = getFieldName();
    String fieldCaption = settings.get("scale1d.label");

    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
    
    if (!hasAnswers) {
      QueryOption optionsOption = getDefinedOption("scale1d.options");
      
      // TODO: Mandarory ???
      
      Boolean mandatory = false;
      
      synchronizeField(settings, queryPage, optionsOption, fieldName, fieldCaption, mandatory);
    } else {
      synchronizeFieldCaption(queryPage, fieldName, fieldCaption);
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
    
    String fieldName = getFieldName();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);

    Messages messages = Messages.getInstance();
    Locale locale = exportContext.getLocale();

    int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption());
    int commentColumnIndex = commentable ? exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption() + "/" + messages.getText(locale, "panelAdmin.query.export.comment")) : -1;
    
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
      exportContext.addCellValue(queryReply, columnIndex, answer != null ? answer.getOption().getText() : null);
      
      QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
      if (commentable) {
        exportContext.addCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
      }
    }
    
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    UserDAO userDAO = new UserDAO();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_barchart");
    
    String fieldName = getFieldName();
    Query query = queryPage.getQuerySection().getQuery();

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryField == null) {
      logger.severe(String.format("QueryField missing, could not render report for query page %d", queryPage.getId()));
      return;
    }
      
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(query, RequestUtils.getActiveStamp(requestContext));
    List<QueryReply> includeReplies = new ArrayList<>();
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, query);
    
    if (excludeReply != null) {
      for (QueryReply queryReply : queryReplies) {
        if (!queryReply.getId().equals(excludeReply.getId())) {
          includeReplies.add(queryReply); 
        }
      }
    } else {
      includeReplies.addAll(queryReplies); 
    }
    
    Map<Long, Long> optionListData = ReportUtils.getOptionListData(queryField, queryFieldOptions, includeReplies);
    
    requiredFragment.addAttribute("axisLabel", queryField.getCaption());
    requiredFragment.addAttribute("valueCount", String.valueOf(queryFieldOptions.size()));
    for (int i = 0, l = queryFieldOptions.size(); i < l; i++) {
      QueryOptionFieldOption fieldOption = queryFieldOptions.get(i);
      Long count = optionListData.get(fieldOption.getId());
      requiredFragment.addAttribute("name." + i, "reportValue." + String.valueOf(Math.round(i)));
      requiredFragment.addAttribute("value." + i, String.valueOf(count));
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  private String getFieldName() {
    return "scale1d";
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}