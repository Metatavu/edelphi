package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.metatavu.edelphi.utils.RequestUtils;

public class DeletePanelExpertiseClassJSONRequestController extends JSONController {

  public DeletePanelExpertiseClassJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long expertiseClassId = jsonRequestContext.getLong("expertiseClassId");
    PanelUserExpertiseClassDAO expertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();

    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    PanelUserExpertiseClass expertiseClass = expertiseClassDAO.findById(expertiseClassId);

    // Expertise class cannot be deleted if it has been used in queries
    
    if (hasAnswers(expertiseClass)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.EXPERTISE_CONTAINS_ANSWERS, messages.getText(locale, "exception.1024.expertiseContainsAnswers"));
    }

    // Expertise class cannot be deleted if it contains panelists
    
    if (hasMembers(expertiseClass)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.EXPERTISE_CONTAINS_USERS, messages.getText(locale, "exception.1041.expertiseContainsUsers"));
    }

    // Delete all groups of the expertise class...  
    
    List<PanelUserExpertiseGroup> groups = expertiseGroupDAO.listByExpertise(expertiseClass);
    for (PanelUserExpertiseGroup group : groups) {
      expertiseGroupDAO.delete(group);
    }
    
    // ...as well as the expertise class itself

    expertiseClassDAO.delete(expertiseClass);
    
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(expertiseClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      ExpertiseQueryPageHandler pageHandler = (ExpertiseQueryPageHandler) QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.EXPERTISE);
      pageHandler.synchronizedFields(expertisePage);
    }
  }
  
  private boolean hasAnswers(PanelUserExpertiseClass expertiseClass) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();
    
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(expertiseClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(expertisePage, getFieldName(expertiseClass));
      Long replies = queryQuestionAnswerDAO.countByQueryField(queryField);
      return replies > 0;
    }
    
    return false;
  }

  private boolean hasMembers(PanelUserExpertiseClass expertiseClass) {
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO groupUserDAO = new PanelExpertiseGroupUserDAO();
    List<PanelUserExpertiseGroup> groups = expertiseGroupDAO.listByExpertise(expertiseClass);
    for (PanelUserExpertiseGroup group : groups) {
      List<PanelExpertiseGroupUser> users = groupUserDAO.listByGroup(group);
      if (!users.isEmpty()) {
        return true;
      }
    }
    return false;
  }
  
  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }
}
