package fi.metatavu.edelphi.jsons.queries;

import java.util.Locale;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.users.UserSettingDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserSetting;
import fi.metatavu.edelphi.domainmodel.users.UserSettingKey;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.utils.MailUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

public class SaveQueryQuestionCommentJSONRequestController extends JSONController {

  public SaveQueryQuestionCommentJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    Long queryPageId = jsonRequestContext.getLong("queryPageId");
    Long parentCommentId = jsonRequestContext.getLong("parentCommentId");
    String comment = jsonRequestContext.getString("comment");
    
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    Query query = queryPage.getQuerySection().getQuery();
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    QueryReply queryReply = QueryDataUtils.findQueryReply(jsonRequestContext, loggedUser, query);
    if (queryReply == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNKNOWN_REPLICANT, messages.getText(locale, "exception.1026.unknownReplicant"));
    }
    
    queryReplyDAO.updateLastModified(queryReply, loggedUser);

    QueryQuestionComment parentComment = null;
    if (parentCommentId != null) {
      parentComment = queryQuestionCommentDAO.findById(parentCommentId);
    }
    
    if (parentComment == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.NO_PARENT_COMMENT, messages.getText(locale, "exception.1043.noParentComment"));
    }
    
    QueryQuestionComment questionComment = queryQuestionCommentDAO.create(queryReply, queryPage, parentComment, comment, false, loggedUser);
    QueryDataUtils.storeQueryReplyId(jsonRequestContext.getRequest().getSession(), queryReply);
    
    // Comment reply e-mail support
    
    if (SystemUtils.isProductionEnvironment() && parentComment.getCreator() != null && !parentComment.getCreator().getId().equals(loggedUser.getId())) {
      User user = parentComment.getCreator();
      UserSettingDAO userSettingDAO = new UserSettingDAO();
      UserSetting userSetting = userSettingDAO.findByUserAndKey(user, UserSettingKey.MAIL_COMMENT_REPLY);
      if (userSetting != null && "1".equals(userSetting.getValue())) {
        
        // URL to the newly added comment
        
        Panel panel = RequestUtils.getPanel(jsonRequestContext);
        StringBuilder commentUrl = new StringBuilder();
        commentUrl.append(RequestUtils.getBaseUrl(jsonRequestContext.getRequest()));
        commentUrl.append('/');
        commentUrl.append(panel.getUrlName());
        commentUrl.append('/');
        commentUrl.append(query.getUrlName());
        commentUrl.append("?page=");
        commentUrl.append(queryPage.getPageNumber());
        commentUrl.append("&comment=");
        commentUrl.append(questionComment.getId());
        
        // Comment mail
        
        String subject = messages.getText(locale, "mail.newReply.template.subject");
        String content = messages.getText(locale, "mail.newReply.template.content", new Object[] {panel.getName(), query.getName(), commentUrl.toString()});
        // TODO system e-mail address could probably be fetched from somewhere?
        MailUtils.sendMail("noreply@edelphi.org", user.getDefaultEmailAsString(), subject, content);
      }
    }
    
    // JSON response parameters
    
    jsonRequestContext.addResponseParameter("commentId", questionComment.getId());
    jsonRequestContext.addResponseParameter("queryPageId", questionComment.getQueryPage().getId());
  }
}
