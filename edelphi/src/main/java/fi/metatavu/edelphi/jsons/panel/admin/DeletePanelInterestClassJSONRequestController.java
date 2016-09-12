package fi.metatavu.edelphi.jsons.panel.admin;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.metatavu.edelphi.smvc.PageNotFoundException;
import fi.metatavu.edelphi.smvc.SmvcRuntimeException;
import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querymeta.QueryFieldDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.metatavu.edelphi.utils.RequestUtils;

public class DeletePanelInterestClassJSONRequestController extends JSONController {

  public DeletePanelInterestClassJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long interestClassId = jsonRequestContext.getLong("interestClassId");
    PanelUserIntressClassDAO interestClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    PanelUserIntressClass interestClass = interestClassDAO.findById(interestClassId);
    
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    // Interest class cannot be deleted if it has been used in queries

    if (hasAnswers(interestClass)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INTEREST_CONTAINS_ANSWERS, messages.getText(locale, "exception.1025.interestContainsAnswers"));
    }
    
    // Interest class cannot be deleted if it contains panelists
    
    if (hasMembers(interestClass)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INTEREST_CONTAINS_USERS, messages.getText(locale, "exception.1042.interestContainsUsers"));
    }
    
    // Delete all groups of the interest class...  
    
    List<PanelUserExpertiseGroup> groups = expertiseGroupDAO.listByInterest(interestClass);
    for (PanelUserExpertiseGroup group : groups) {
      expertiseGroupDAO.delete(group);
    }
    
    // ...as well as the interest class itself
    
    interestClassDAO.delete(interestClass);

    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(interestClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      ExpertiseQueryPageHandler pageHandler = (ExpertiseQueryPageHandler) QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.EXPERTISE);
      pageHandler.synchronizedFields(expertisePage);
    }

  }
  
  private boolean hasAnswers(PanelUserIntressClass interestClass) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    
    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(interestClass.getPanel());
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(interestClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(expertisePage, getFieldName(expertiseClass));
        List<QueryQuestionMultiOptionAnswer> answers = queryQuestionMultiOptionAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionMultiOptionAnswer answer : answers) {
          for (QueryOptionFieldOption option : answer.getOptions()) {
            Long optionIterestId = NumberUtils.createLong(option.getValue());
            if (interestClass.getId().equals(optionIterestId))
              return true;
          }
        }
      }
    }
    
    return false;
  }
  
  private boolean hasMembers(PanelUserIntressClass interestClass) {
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO groupUserDAO = new PanelExpertiseGroupUserDAO();
    List<PanelUserExpertiseGroup> groups = expertiseGroupDAO.listByInterest(interestClass);
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
