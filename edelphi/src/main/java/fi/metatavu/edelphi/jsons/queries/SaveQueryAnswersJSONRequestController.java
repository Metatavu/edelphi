package fi.metatavu.edelphi.jsons.queries;

import java.util.Locale;

import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryPageHandler;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils;

public class SaveQueryAnswersJSONRequestController extends JSONController {

  public SaveQueryAnswersJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    
    Long queryPageId = jsonRequestContext.getLong("queryPageId");
    
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    Query query = queryPage.getQuerySection().getQuery();
  
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    if (query.getState() == QueryState.CLOSED)
      throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_SAVE_REPLY_QUERY_CLOSED, messages.getText(locale, "exception.1027.cannotSaveReplyQueryClosed"));
    
    if (query.getState() == QueryState.EDIT) {
      if (!ActionUtils.hasPanelAccess(jsonRequestContext, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString()))
        throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_SAVE_REPLY_QUERY_IN_EDIT_STATE, messages.getText(locale, "exception.1028.cannotSaveReplyQueryInEditState"));
    }
    else {
      User loggedUser = null;
      if (jsonRequestContext.isLoggedIn())
        loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
      
      QueryReply queryReply = QueryDataUtils.findQueryReply(jsonRequestContext, loggedUser, query);
      if (queryReply == null) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNKNOWN_REPLICANT, messages.getText(locale, "exception.1026.unknownReplicant"));
      }
      queryReplyDAO.updateLastModified(queryReply, loggedUser);
      
      QueryDataUtils.storeQueryReplyId(jsonRequestContext.getRequest().getSession(), queryReply);
      
      QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
      queryPageHandler.saveAnswers(jsonRequestContext, queryPage, queryReply);
    }
  }
}
