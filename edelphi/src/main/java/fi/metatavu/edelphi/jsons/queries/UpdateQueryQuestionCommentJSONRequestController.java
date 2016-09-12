package fi.metatavu.edelphi.jsons.queries;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class UpdateQueryQuestionCommentJSONRequestController extends JSONController {
  
  public UpdateQueryQuestionCommentJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_QUERY_COMMENTS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    
    Long commentId = jsonRequestContext.getLong("commentId");
    String comment = jsonRequestContext.getString("comment");
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    QueryQuestionComment questionComment = queryQuestionCommentDAO.findById(commentId);
    queryQuestionCommentDAO.updateComment(questionComment, comment, loggedUser);
    
    jsonRequestContext.addResponseParameter("commentId", questionComment.getId());
    jsonRequestContext.addResponseParameter("queryPageId", questionComment.getQueryPage().getId());
  }
}
