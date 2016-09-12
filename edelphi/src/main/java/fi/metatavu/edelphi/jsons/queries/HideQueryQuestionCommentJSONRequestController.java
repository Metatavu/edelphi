package fi.metatavu.edelphi.jsons.queries;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class HideQueryQuestionCommentJSONRequestController extends JSONController {

  public HideQueryQuestionCommentJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_QUERY_COMMENTS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    Long commentId = jsonRequestContext.getLong("commentId");
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    QueryQuestionComment comment = queryQuestionCommentDAO.findById(commentId);
    queryQuestionCommentDAO.updateHidden(comment, Boolean.TRUE, loggedUser);
    
    jsonRequestContext.addResponseParameter("commentId", comment.getId());
  }

}