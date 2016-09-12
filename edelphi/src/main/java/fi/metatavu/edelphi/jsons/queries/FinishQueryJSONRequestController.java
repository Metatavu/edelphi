package fi.metatavu.edelphi.jsons.queries;

import fi.metatavu.edelphi.smvc.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.utils.RequestUtils;

public class FinishQueryJSONRequestController extends JSONController {
  
  public FinishQueryJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long replyId = jsonRequestContext.getLong("replyId");
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    if (loggedUser != null && replyId != null) {
      QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
      QueryReply queryReply = queryReplyDAO.findById(replyId);
      if (queryReply != null) {
        queryReplyDAO.updateComplete(queryReply, loggedUser, Boolean.TRUE);
      }
    }
  }
}
