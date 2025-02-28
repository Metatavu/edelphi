package fi.metatavu.edelphi.rest.scheduled;

import fi.metatavu.edelphi.comments.QueryQuestionCommentController;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.panels.PanelController;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryReplyController;
import fi.metatavu.edelphi.resources.ResourceController;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledPanelDeletion {
  @Inject
  private PanelController panelController;

  @Inject
  private QueryController queryController;

  @Inject
  private QueryQuestionCommentController queryQuestionCommentController;

  @Inject
  private QueryReplyController queryReplyController;

  @Inject
  private ResourceController resourceController;

  @Schedule (hour = "*", minute = "*", second = "*/60", info = "Panel deletion scheduler. Runs every 60 seconds.")
  public void delete() throws InterruptedException {
    if (SchedulerUtils.deletionSchedulersActive()) {
      List<Panel> panelList = panelController.listPanelsToDelete(731, 1);

      if (!panelList.isEmpty()) {
        Panel panel = panelList.get(0);
        panelController.schedulerDeleteQueryDependencies(panel);
        List<Query> queries = queryController.listAllPanelQueries(panel);
        if (!queries.isEmpty()) {
          Query query = queries.get(0);
          List<QueryQuestionComment> comments = queryQuestionCommentController.listAllByQuery(query);
          comments.forEach(queryQuestionCommentController::removeParent);
          comments.forEach(queryQuestionCommentController::deleteQueryQuestionComment);

          List<QueryReply> replies = queryReplyController.listQueryReplies(query, 100);
          if (!replies.isEmpty()) {
            replies.forEach(queryReplyController::deleteQueryReplyAnswers);

            replies.forEach(queryReplyController::deleteReply);
          } else {
            resourceController.deleteResource(query);
          }
        } else {
          panelController.schedulerDeleteAfterDeletingQueries(panel);
        }
      }
    }
  }

}
