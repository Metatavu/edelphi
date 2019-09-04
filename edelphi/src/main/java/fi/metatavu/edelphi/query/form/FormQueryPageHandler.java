package fi.metatavu.edelphi.query.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.smvcj.logging.Logging;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryTextFieldDAO;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryTextField;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.query.AbstractQueryPageHandler;
import fi.metatavu.edelphi.query.QueryExportContext;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryOptionType;
import fi.metatavu.edelphi.query.RequiredQueryFragment;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.SystemUtils;
import fi.metatavu.edelphi.utils.comments.ReportPageCommentProcessor;

public class FormQueryPageHandler extends AbstractQueryPageHandler {

  public FormQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.FORM, "form.fields", "panelAdmin.block.query.formFieldsOptionLabel", QueryOptionEditor.FORM_FIELDS, false));
  }
  
  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryOption fieldsOption = getDefinedOption("form.fields");
    
    String fieldsValue = getStringOptionValue(queryPage, fieldsOption);
    if (StringUtils.isNotBlank(fieldsValue)) {
      JSONArray fieldsJson = JSONArray.fromObject(fieldsValue);
      JSONObject fieldJson = null;
      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
        fieldJson = fieldsJson.getJSONObject(i);
        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
        switch (fieldType) {
          case TEXT:
            renderTextField(requestContext, queryPage, queryReply, fieldJson);
          break;
          case MEMO:
            renderMemoField(requestContext, queryPage, queryReply, fieldJson);
          break;
          case LIST:
            renderListField(requestContext, queryPage, queryReply, fieldJson);
          break;
        }
      }
    }
  }

  private void renderTextField(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply, JSONObject fieldJson) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
    FormTextField textField = new FormTextField(fieldJson);
    String fieldName = getFieldName(textField.getName());
    QueryTextField textQueryField = (QueryTextField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionTextAnswer textAnswer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply, textQueryField);
    addRequiredFragment(requestContext, textToFragment(textField, textAnswer != null ? textAnswer.getData() : null));
  }

  private void renderMemoField(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply, JSONObject fieldJson) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
    FormMemoField memoField = new FormMemoField(fieldJson);
    String fieldName = getFieldName(memoField.getName());
    QueryTextField memoQueryField = (QueryTextField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionTextAnswer memoAnswer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply, memoQueryField);
    addRequiredFragment(requestContext, memoToFragment(memoField, memoAnswer != null ? memoAnswer.getData() : null));
  }

  private void renderListField(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply, JSONObject fieldJson) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    FormListField listField = new FormListField(fieldJson);
    String fieldName = getFieldName(listField.getName());
    QueryOptionField optionQueryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    QueryQuestionOptionAnswer optionAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, optionQueryField);
    String selectedOption = null;
    if (optionAnswer != null && optionAnswer.getOption() != null) {
      selectedOption = optionAnswer.getOption().getValue();
    }
    addRequiredFragment(requestContext, listToFragment(listField, selectedOption));
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    if (!hasAnswers) {
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
      QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();
      QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO(); 
      
      QueryOption fieldsOption = getDefinedOption("form.fields");
      String fieldsSetting = QueryPageUtils.unfilterJsonSerializedSetting(settings.get(fieldsOption.getName()));
      QueryPageUtils.setSetting(queryPage, fieldsOption.getName(), fieldsSetting, modifier);
      List<QueryField> existingFields = queryFieldDAO.listByQueryPage(queryPage);
      List<String> removedFields = new ArrayList<String>();
      for (QueryField queryField : existingFields) {
        removedFields.add(queryField.getName());
      }
      
      try {
        if (StringUtils.isNotBlank(fieldsSetting)) {
          JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
          JSONObject fieldJson = null;
          for (int i = 0, l = fieldsJson.size(); i < l; i++) {
            fieldJson = fieldsJson.getJSONObject(i);
            FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
            // TODO: mandatory
            Boolean mandatory = Boolean.FALSE;

            switch (fieldType) {
              case TEXT:
                updateTextField(queryPage, removedFields, fieldJson, mandatory);
                break;
              case MEMO:
                updateMemoField(queryPage, removedFields, fieldJson, mandatory);
                break;
              case LIST:
                updateListField(queryPage, removedFields, fieldJson, mandatory);
                break;
            }
          }
        }
      }
      catch (JSONException e) {
        Logging.logException(e);
        Logging.logError("updatePageOptions: " + fieldsSetting);
        throw e;
      }
      
      for (String removedField : removedFields) {
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, removedField);
        if (queryField instanceof QueryOptionField) {
          List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField((QueryOptionField) queryField);
          for (QueryOptionFieldOption option : options) {
            long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(option);
            if (answerCount == 0) {
              queryOptionFieldOptionDAO.delete(option);
            }
            else {
              queryOptionFieldOptionDAO.archive(option);
            }
          }
        }
        if (queryField != null) {
          long answerCount = queryQuestionAnswerDAO.countByQueryField(queryField);
          if (answerCount == 0) {
            queryFieldDAO.delete(queryField);
          }
          else {
            queryFieldDAO.archive(queryField);
          }
        }
      }
    }
  }

  private void updateTextField(QueryPage queryPage, List<String> removedFields, JSONObject fieldJson, Boolean mandatory) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryTextFieldDAO queryTextFieldDAO = new QueryTextFieldDAO();

    FormTextField textField = new FormTextField(fieldJson);
    String fieldName = getFieldName(textField.getName());
    QueryTextField queryTextField = (QueryTextField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryTextField == null) {
      queryTextField = queryTextFieldDAO.create(queryPage, fieldName, mandatory, textField.getCaption());
    } else {
      queryFieldDAO.updateCaption(queryTextField, StringUtils.abbreviate(textField.getCaption(), SystemUtils.MAX_QUERY_FIELD_CAPTION));
      queryFieldDAO.updateMandatory(queryTextField, mandatory);
      removedFields.remove(fieldName);
    }
  }

  private void updateMemoField(QueryPage queryPage, List<String> removedFields, JSONObject fieldJson, Boolean mandatory) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryTextFieldDAO queryTextFieldDAO = new QueryTextFieldDAO();

    FormMemoField memoField = new FormMemoField(fieldJson);
    String fieldName = getFieldName(memoField.getName());
    
    QueryTextField queryMemoField = (QueryTextField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryMemoField == null) {
      queryMemoField = queryTextFieldDAO.create(queryPage, fieldName, mandatory, memoField.getCaption());
    } else {
      queryFieldDAO.updateCaption(queryMemoField, StringUtils.abbreviate(memoField.getCaption(), SystemUtils.MAX_QUERY_FIELD_CAPTION));
      queryFieldDAO.updateMandatory(queryMemoField, mandatory);
      removedFields.remove(fieldName);
    }
  }

  private void updateListField(QueryPage queryPage, List<String> removedFields, JSONObject fieldJson, Boolean mandatory) {
    try {
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryOptionFieldDAO queryOptionFieldDAO = new QueryOptionFieldDAO();
      QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
      QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

      FormListField listField = new FormListField(fieldJson);
      String fieldName = getFieldName(listField.getName());

      QueryOptionField queryListField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      if (queryListField == null) {
        queryListField = queryOptionFieldDAO.create(queryPage, fieldName, mandatory, listField.getCaption());
      } else {
        queryFieldDAO.updateCaption(queryListField, StringUtils.abbreviate(listField.getCaption(), SystemUtils.MAX_QUERY_FIELD_CAPTION));
        queryFieldDAO.updateMandatory(queryListField, mandatory);
        removedFields.remove(fieldName);
      }

      List<String> removedOptions = new ArrayList<String>();
      List<QueryOptionFieldOption> existingOptions = queryOptionFieldOptionDAO.listByQueryField(queryListField);
      for (QueryOptionFieldOption fieldOption : existingOptions) {
        removedOptions.add(fieldOption.getValue());
      }

      for (FormListFieldOption option : listField.getOptions()) {
        QueryOptionFieldOption fieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryListField, option.getValue());
        if (fieldOption == null) {
          queryOptionFieldOptionDAO.create(queryListField, option.getLabel(), option.getValue());
        } else {
          queryOptionFieldOptionDAO.updateText(fieldOption, option.getLabel());
          removedOptions.remove(fieldOption.getValue());
        }
      }

      for (String removedOption : removedOptions) {
        QueryOptionFieldOption fieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryListField, removedOption);
        if (fieldOption != null) {
          long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(fieldOption);
          if (answerCount == 0) {
            queryOptionFieldOptionDAO.delete(fieldOption);
          }
          else {
            queryOptionFieldOptionDAO.archive(fieldOption);
          }
        }
      }
    }
    catch (JSONException e) {
      Logging.logException(e);
      Logging.logError("updateListField: " + (fieldJson == null ? "null" : fieldJson.toString()));
      throw e;
    }
  }

  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    String fieldsSetting = getStringOptionValue(queryPage, getDefinedOption("form.fields"));
    JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
  
    JSONObject fieldJson = null;
    for (int i = 0, l = fieldsJson.size(); i < l; i++) {
      fieldJson = fieldsJson.getJSONObject(i);
      FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));

      String name = fieldJson.getString("name");
      String value = requestContext.getString(name);
      String fieldName = getFieldName(name);
      
      Query query = queryPage.getQuerySection().getQuery();
      
      QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      if (queryField == null) {
        throw new IllegalArgumentException("Field '" + fieldName + "' not found");
      } else {
        switch (fieldType) {
          case MEMO:
          case TEXT:
            QueryQuestionTextAnswer textAnswer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
            if (textAnswer != null) {
              if (query.getAllowEditReply()) {
                textAnswer = queryQuestionTextAnswerDAO.updateData(textAnswer, value);
              } else {
                throw new IllegalStateException("Could not save reply: Already replied");
              }
            } else {
              queryQuestionTextAnswerDAO.create(queryReply, queryField, value);
            }
          break;
          case LIST:
            QueryOptionField queryOptionField = (QueryOptionField) queryField;
            QueryOptionFieldOption fieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryOptionField, value);

            if (fieldOption != null) {
              QueryQuestionOptionAnswer questionAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
              if (questionAnswer != null) {
                if (query.getAllowEditReply()) {
                  questionAnswer = queryQuestionOptionAnswerDAO.updateOption(questionAnswer, fieldOption);
                } else {
                  throw new IllegalStateException("Could not save reply: Already replied");
                }
              } else {
                questionAnswer = queryQuestionOptionAnswerDAO.create(queryReply, queryField, fieldOption);
              }
            }
          break;
        }  
      }
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
    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    List<QueryReply> queryReplies = exportContext.getQueryReplies();

    QueryPage queryPage = exportContext.getQueryPage();

    List<QueryField> queryFields = queryFieldDAO.listByQueryPage(queryPage);
    for (QueryField queryField : queryFields) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryField.getCaption());
      
      for (QueryReply queryReply : queryReplies) {
        switch (queryField.getType()) {
        case OPTIONFIELD:
          QueryQuestionOptionAnswer optionAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          exportContext.addCellValue(queryReply, columnIndex, optionAnswer != null ? optionAnswer.getOption().getText() : null);
          break;
        case NUMERIC:
        case NUMERIC_SCALE:
          QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply,  queryField);
          exportContext.addCellValue(queryReply, columnIndex, numericAnswer != null ? numericAnswer.getData() : null);
          break;
        case TEXT:
          QueryQuestionTextAnswer textAnswer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply,  queryField);
          exportContext.addCellValue(queryReply, columnIndex, textAnswer != null ? textAnswer.getData() : null);
          break;
        }
      }
    }
  }
  
  @Override
  public ReportPageCommentProcessor exportComments(QueryPage queryPage, PanelStamp stamp, List<QueryReply> replies) {
    return null;
  }
  
  private RequiredQueryFragment textToFragment(FormTextField field, String value) {
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("form_text");
    requiredFragment.addAttribute("id", "text-" + UUID.randomUUID().toString());
    requiredFragment.addAttribute("caption", field.getCaption());
    requiredFragment.addAttribute("name", field.getName());
    requiredFragment.addAttribute("value", value);

    return requiredFragment;
  }
  
  private RequiredQueryFragment memoToFragment(FormMemoField field, String value) {
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("form_memo");
    requiredFragment.addAttribute("id", "memo-" + UUID.randomUUID().toString());
    requiredFragment.addAttribute("caption", field.getCaption());
    requiredFragment.addAttribute("name", field.getName());
    requiredFragment.addAttribute("value", value);

    return requiredFragment;
  }

  private RequiredQueryFragment listToFragment(FormListField field, String value) {
    List<FormListFieldOption> fieldOptions = field.getOptions();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("form_list");
    requiredFragment.addAttribute("id", "list-" + UUID.randomUUID().toString());
    requiredFragment.addAttribute("listType", field.getListType().toString());
    requiredFragment.addAttribute("caption", field.getCaption());
    requiredFragment.addAttribute("name", field.getName());
    requiredFragment.addAttribute("optionsCount", String.valueOf(fieldOptions.size()));
    
    for (int i = 0, l = fieldOptions.size(); i < l; i++) {
      FormListFieldOption fieldOption = fieldOptions.get(i);
      requiredFragment.addAttribute("option." + i + ".value", fieldOption.getValue());
      requiredFragment.addAttribute("option." + i + ".label", fieldOption.getLabel());
      if (fieldOption.getValue().equals(value))
        requiredFragment.addAttribute("option." + i + ".selected", "1");
    }
    
    return requiredFragment;
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
  
  private String getFieldName(String name) {
    return "form." + name;
  }
  
  private abstract class FormField {
  
    public FormField(FormFieldType type, String name, String caption) {
      this.type = type;
      this.name = name;
      this.caption = caption;
    }
    
    public FormField(JSONObject jsonObject) {
      this(FormFieldType.valueOf(jsonObject.getString("type")), jsonObject.getString("name"), jsonObject.getString("caption"));
    }
    
    public FormFieldType getType() {
      return type;
    }
    
    public String getCaption() {
      return caption;
    }
    
    public String getName() {
      return name;
    }
    
    public abstract JSONObject serialize();
  
    private FormFieldType type;
    private String name;
    private String caption;
  }
  
  private class FormTextField extends FormField {

    public FormTextField(String name, String caption) {
      super(FormFieldType.TEXT, name, caption);
    }
    
    public FormTextField(JSONObject jsonObject) {
      super(jsonObject);
    }
    
    @Override
    public JSONObject serialize() {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", getType());
      jsonObject.put("name", getName());
      jsonObject.put("caption", getCaption());
      return jsonObject;
    }

  }

  private class FormMemoField extends FormField {

    public FormMemoField(String name, String caption) {
      super(FormFieldType.MEMO, name, caption);
    }
    
    public FormMemoField(JSONObject jsonObject) {
      super(jsonObject);
    }
    
    @Override
    public JSONObject serialize() {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", getType());
      jsonObject.put("name", getName());
      jsonObject.put("caption", getCaption());
      return jsonObject;
    }
  }
  
  private class FormListField extends FormField {

    public FormListField(FormListFieldType listType, String name, String caption) {
      super(FormFieldType.LIST, name, caption);
      this.listType = listType;
    }
    
    public FormListField(JSONObject jsonObject) {
      super(jsonObject);

      this.listType = FormListFieldType.valueOf(jsonObject.getString("listType"));

      JSONArray optionsJson = jsonObject.getJSONArray("options");
      for (int i = 0, l = optionsJson.size(); i < l; i++) {
        JSONObject optionJson = optionsJson.getJSONObject(i);
        String label = optionJson.getString("label");
        String value = optionJson.getString("value");
        addOption(new FormListFieldOption(value, label));
      }
    }
    
    public List<FormListFieldOption> getOptions() {
      return options;
    }
    
    public FormListFieldType getListType() {
      return listType;
    }
    
    public void addOption(FormListFieldOption option) {
      this.options.add(option);
    }
    
    @Override
    public JSONObject serialize() {
      JSONArray optionsJson = new JSONArray();
      
      for (FormListFieldOption option : options) {
        JSONObject optionJson = new JSONObject();
        optionJson.put("label", option.getLabel());
        optionJson.put("value", option.getValue());
        optionsJson.add(optionJson);
      }
      
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", getType());
      jsonObject.put("listType", getListType());
      jsonObject.put("name", getName());
      jsonObject.put("caption", getCaption());
      jsonObject.put("options", optionsJson);
      
      return jsonObject;
    }

    private List<FormListFieldOption> options = new ArrayList<FormQueryPageHandler.FormListFieldOption>();
    private FormListFieldType listType;
  }
  
  private enum FormListFieldType {
    SELECT,
    RADIO,
    SLIDER
  }
  
  private class FormListFieldOption {

    public FormListFieldOption(String value, String label) {
      this.value = value;
      this.label = label;
    }
    
    public String getLabel() {
      return label;
    }
    
    public String getValue() {
      return value;
    }
    
    private String value;
    private String label;
  }
}
