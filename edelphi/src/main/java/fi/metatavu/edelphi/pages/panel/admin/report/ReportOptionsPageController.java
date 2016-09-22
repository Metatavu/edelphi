package fi.metatavu.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelStampDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.query.form.FormFieldType;
import fi.metatavu.edelphi.utils.QueryPageUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ReportOptionsPageController extends PanelPageController {

  public ReportOptionsPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    // Data access objects

    PanelStampDAO panelStampDAO = new PanelStampDAO();
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO optionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    // Input attributes
    
    Long queryId = pageRequestContext.getLong("queryId");
    Long stampId = pageRequestContext.getLong("stampId");
    if (stampId == null || stampId <= 0) {
      stampId = panel.getCurrentStamp().getId();
    }
    
    // Input attributes into objects
    
    Query query = queryDAO.findById(queryId);
    PanelStamp stamp = panelStampDAO.findById(stampId);
    
    // Form fields

    List<QueryPage> pages = queryPageDAO.listByQueryAndType(query, QueryPageType.FORM);
    List<FormOptionListFieldDescriptor> beans = new ArrayList<FormOptionListFieldDescriptor>();
    for (QueryPage queryPage : pages) {
      String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
      JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
    
      JSONObject fieldJson = null;
      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
        fieldJson = fieldsJson.getJSONObject(i);
        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
  
        String name = fieldJson.getString("name");
        String fieldName = "form." + name;
        
        FormOptionListFieldDescriptor mrBean;
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          throw new IllegalArgumentException("Field '" + fieldName + "' not found");
        }
        else {
          switch (fieldType) {
            case MEMO:
            case TEXT:
            break;
            case LIST:
              QueryOptionField queryOptionField = (QueryOptionField) queryField;
              List<QueryOptionFieldOption> options = optionFieldOptionDAO.listByQueryField(queryOptionField);
              String formFieldFilter = pageRequestContext.getString("ff:" + queryOptionField.getId());
              mrBean = new FormOptionListFieldDescriptor(fieldType.toString(), queryOptionField, options, formFieldFilter);
              beans.add(mrBean);
            break;
          }  
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryFormFilterFields", beans);
    
    // User groups

    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, stamp);
    Collections.sort(panelUserGroups, new Comparator<PanelUserGroup>() {
      @Override
      public int compare(PanelUserGroup o1, PanelUserGroup o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("userGroups", panelUserGroups);
    
    // Expertises and interests

    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
    Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
      @Override
      public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    List<PanelUserIntressClass> intressClasses = panelUserIntressClassDAO.listByPanel(panel);
    Collections.sort(intressClasses, new Comparator<PanelUserIntressClass>() {
      @Override
      public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    Map<Long, Map<Long, Long>> expertiseGroupMap = new HashMap<Long, Map<Long, Long>>();    
    Map<Long, Long> expertiseGroupUserCount = new HashMap<Long, Long>();
    
    for (PanelUserIntressClass intressClass : intressClasses) {
      if (!expertiseGroupMap.containsKey(intressClass.getId())) {
        expertiseGroupMap.put(intressClass.getId(), new HashMap<Long, Long>());
      }
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        PanelUserExpertiseGroup group = panelUserExpertiseGroupDAO.findByInterestAndExpertiseAndStamp(intressClass, expertiseClass, stamp);
        if (group != null) {
          Long userCount = panelExpertiseGroupUserDAO.getUserCountInGroup(group);
          expertiseGroupMap.get(intressClass.getId()).put(expertiseClass.getId(), group.getId());
          expertiseGroupUserCount.put(group.getId(), userCount);
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterExpertises", expertiseClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterInterests", intressClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupMap", expertiseGroupMap);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupUserCount", expertiseGroupUserCount);

    // Panel stamps

    List<PanelStamp> stamps = panelStampDAO.listByPanel(panel);
    Collections.sort(stamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? 1 : o2.getStampTime() == null ? -1 : o1.getStampTime().compareTo(o2.getStampTime());
      }
    });
    pageRequestContext.getRequest().setAttribute("stamps", stamps);
    pageRequestContext.getRequest().setAttribute("stampId", stampId);
    
    // Query pages
    
    List<QueryPage> queryPages = new ArrayList<QueryPage>();
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new Comparator<QuerySection>() {
      @Override
      public int compare(QuerySection o1, QuerySection o2) {
        return o1.getSectionNumber() - o2.getSectionNumber();
      }
    });
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        List<QueryPage> sectionPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(sectionPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        for (QueryPage sectionPage : sectionPages) {
          if (sectionPage.getVisible()) {
            queryPages.add(sectionPage);
          }
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryPages", queryPages);
   
    pageRequestContext.setIncludeJSP("/jsp/blocks/panel/admin/report/reportoptions.jsp");
  }

  public class FormOptionListFieldDescriptor {

    private final String fieldType;
    private final QueryOptionField queryOptionField;
    private final List<QueryOptionFieldOption> options;

    public FormOptionListFieldDescriptor(String fieldType, QueryOptionField queryOptionField, List<QueryOptionFieldOption> options, String formFieldFilter) {
      this.fieldType = fieldType;
      this.queryOptionField = queryOptionField;
      this.options = options;
    }

    public String getFieldType() {
      return fieldType;
    }

    public QueryOptionField getQueryOptionField() {
      return queryOptionField;
    }

    public List<QueryOptionFieldOption> getOptions() {
      return options;
    }
  }

}
