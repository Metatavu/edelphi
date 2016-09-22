package fi.metatavu.edelphi.jsons.queries;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.dao.querylayout.QueryPageTemplateDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageTemplateLocalizedSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageTemplateSimpleSettingDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplateLocalizedSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageTemplateSimpleSetting;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryOption;
import fi.metatavu.edelphi.query.QueryOptionDataType;
import fi.metatavu.edelphi.query.QueryOptionEditor;
import fi.metatavu.edelphi.query.QueryPageHandler;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.utils.LocalizationUtils;
import fi.metatavu.edelphi.utils.QueryPageUtils;

public class GetQueryPageTemplateJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryPageTemplateDAO queryPageTemplateDAO = new QueryPageTemplateDAO();
    
    QueryPageTemplateSimpleSettingDAO queryPageTemplateSimpleSettingDAO = new QueryPageTemplateSimpleSettingDAO();
    QueryPageTemplateLocalizedSettingDAO queryPageTemplateLocalizedSettingDAO = new QueryPageTemplateLocalizedSettingDAO();

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    Long queryPageTemplateId = jsonRequestContext.getLong("queryPageTemplateId");
    QueryPageTemplate queryPageTemplate = queryPageTemplateDAO.findById(queryPageTemplateId);
    
    JSONObject queryPageJson = new JSONObject();
    JSONArray optionsJson = new JSONArray();
    
    QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPageTemplate.getPageType());
    
    List<QueryPageTemplateSimpleSetting> simpleSettings = queryPageTemplateSimpleSettingDAO.listByQueryPageTemplate(queryPageTemplate);
    Map<String, QueryPageTemplateSimpleSetting> simpleSettingMap = new HashMap<String, QueryPageTemplateSimpleSetting>();
    for (QueryPageTemplateSimpleSetting simpleSetting : simpleSettings) {
      simpleSettingMap.put(simpleSetting.getKey().getName(), simpleSetting);
    }

    List<QueryPageTemplateLocalizedSetting> localizedSettings = queryPageTemplateLocalizedSettingDAO.listByQueryPageTemplate(queryPageTemplate);
    Map<String, QueryPageTemplateLocalizedSetting> localizedSettingMap = new HashMap<String, QueryPageTemplateLocalizedSetting>();
    for (QueryPageTemplateLocalizedSetting localizedSetting : localizedSettings) {
      localizedSettingMap.put(localizedSetting.getKey().getName(), localizedSetting);
    }
    
    List<QueryOption> definedOptions = queryPageHandler.getDefinedOptions();
    for (QueryOption definedOption : definedOptions) {
      JSONObject optionJson = new JSONObject();
      
      String caption = messages.getText(locale, definedOption.getLocaleKey());
      String name = definedOption.getName();
      QueryOptionEditor editor = definedOption.getEditor();
      String rawValue = null;

      if (simpleSettingMap.containsKey(name)) {
        QueryPageTemplateSimpleSetting simpleSetting = simpleSettingMap.get(name);
        rawValue = simpleSetting.getValue();
      } else if (localizedSettingMap.containsKey(name)) {
        QueryPageTemplateLocalizedSetting localizedSetting = localizedSettingMap.get(name);
        rawValue = LocalizationUtils.getLocalizedText(localizedSetting.getValue(), locale);
      }
      
      optionJson.put("caption", caption);
      optionJson.put("name", name);
      optionJson.put("editor", editor);
      optionJson.put("type", definedOption.getType());
      
      if (rawValue != null) {
        switch (editor.getDataType()) {
          case MAP:
            NavigableMap<String, String> map = QueryPageUtils.parseSerializedMap(rawValue);
            optionJson.put("value", QueryPageUtils.serializeMap(map));
          break;
          default:
            if (editor.getDataType() == QueryOptionDataType.JSON_SERIALIZED) {
              optionJson.put("value", QueryPageUtils.filterJsonSerializedSetting(rawValue));
            } else {
              optionJson.put("value", rawValue);
            }
            
          break;
        }
      }
      
      optionsJson.add(optionJson);
    
    }
    
    queryPageJson.put("templateId",  queryPageTemplate.getId());
    queryPageJson.put("type",  queryPageTemplate.getPageType());
    queryPageJson.put("options", optionsJson);
    
    jsonRequestContext.addResponseParameter("queryPage", queryPageJson);
  }
}
