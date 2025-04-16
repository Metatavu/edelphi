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
import org.slf4j.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

@Startup
@Singleton
public class ScheduledPanelDeletion {
  @Inject
  private Logger logger;

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

  @Schedule (hour = "*", minute = "*/15")
  public void delete() throws InterruptedException {
    if (SchedulerUtils.panelDeletionScheduleActive()) {
      List<Panel> panelsToDelete = panelController.listPanelsToDelete(30, 1);

      for (Panel panelToDelete : panelsToDelete) {
        logger.info("Deleting panel: {} ({}), reason: archived and last modified at: {}", panelToDelete.getId(), panelToDelete.getRootFolder().getUrlName(), panelToDelete.getLastModified());

        panelController.deletePanelAuths(panelToDelete);
        panelController.deletePanelUserRoleActions(panelToDelete);
        panelController.deletePanelBulletins(panelToDelete);
        panelController.deletePanelInvitations(panelToDelete);

          List<Query> queries = queryController.listPanelQueries(panelToDelete, true);
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
          panelController.deletePanel(panelToDelete);
        }
      }
    }
  }

}
