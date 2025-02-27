package fi.metatavu.edelphi.users;

import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.base.UserCreatedEntityDAO;
import fi.metatavu.edelphi.dao.drafts.FormDraftDAO;
import fi.metatavu.edelphi.dao.panels.*;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentCategoryDAO;
import fi.metatavu.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageTemplateDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.resources.ResourceLockDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.base.UserCreatedEntity;
import fi.metatavu.edelphi.domainmodel.users.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ClearUserCreationsAndModifications {
  @Inject
  private UserDAO userDAO;

  @Inject
  private PanelUserDAO panelUserDAO;

  @Inject
  private PanelBulletinDAO panelBulletinDAO;

  @Inject
  private DelfoiBulletinDAO delfoiBulletinDAO;

  @Inject
  private DelfoiUserDAO delfoiUserDAO;

  @Inject
  private FormDraftDAO formDraftDAO;

  @Inject
  private PanelDAO panelDAO;

  @Inject
  private PanelInvitationDAO panelInvitationDAO;

  @Inject
  private PanelStampDAO panelStampDAO;

  @Inject
  private PanelUserGroupDAO panelUserGroupDAO;

  @Inject
  private QueryQuestionCommentDAO queryQuestionCommentDAO;

  @Inject
  private QueryQuestionCommentCategoryDAO queryQuestionCommentCategoryDAO;

  @Inject
  private QueryReplyDAO queryReplyDAO;

  @Inject
  private QueryPageDAO queryPageDAO;

  @Inject
  private QueryPageTemplateDAO queryPageTemplateDAO;

  @Inject
  private QuerySectionDAO querySectionDAO;

  @Inject
  private ResourceDAO resourceDAO;

  @Inject
  private ResourceLockDAO resourceLockDAO;

  private static final long ADMIN_USER_ID = 1;

  /**
   * Clear all creator and lastModifier fields where this user is used.
   * The fields are set to the main admin user.
   *
   * @param user user whose fields to reset
   */
  public void clearUserModifiedEntities(User user) {
    User resetUser = userDAO.findById(ADMIN_USER_ID);

    clearUserModifiedEntities(user, panelUserDAO, resetUser);
    clearUserModifiedEntities(user, panelBulletinDAO, resetUser);
    clearUserModifiedEntities(user, delfoiUserDAO, resetUser);
    clearUserModifiedEntities(user, formDraftDAO, resetUser);
    clearUserModifiedEntities(user, panelDAO, resetUser);
    clearUserModifiedEntities(user, delfoiBulletinDAO, resetUser);
    clearUserModifiedEntities(user, panelInvitationDAO, resetUser);
    clearUserModifiedEntities(user, panelStampDAO, resetUser);
    clearUserModifiedEntities(user, panelUserGroupDAO, resetUser);
    clearUserModifiedEntities(user, queryQuestionCommentDAO, resetUser);
    clearUserModifiedEntities(user, queryQuestionCommentCategoryDAO, resetUser);
    clearUserModifiedEntities(user, queryReplyDAO, resetUser);
    clearUserModifiedEntities(user, queryPageDAO, resetUser);
    clearUserModifiedEntities(user, queryPageTemplateDAO, resetUser);
    clearUserModifiedEntities(user, querySectionDAO, resetUser);
    clearUserModifiedEntities(user, resourceDAO, resetUser);
    clearUserModifiedEntities(user, resourceLockDAO, resetUser);
    clearUserModifiedEntities(user, userDAO, resetUser);
  }

  /**
   * Clears for the given entity creator and lastModifier fields where this user is used.
   * Sets the creator and lastModifier fields to the user given in resetUser parameter.
   *
   * @param user user whose fields to reset
   * @param dao DAO of the entity type to clear
   * @param resetUser new value
   * @param <T> type of the entity to clear
   */
  private <T> void clearUserModifiedEntities(User user, UserCreatedEntityDAO<T> dao, User resetUser) {
    List<UserCreatedEntity> createdByUser = (List<UserCreatedEntity>) dao.listAllByCreator(user);

    for (UserCreatedEntity resource: createdByUser) {
      resource.setCreator(resetUser);
      dao.persist((T) resource);
    }

    List<UserCreatedEntity> modifiedByUser = (List<UserCreatedEntity>) dao.listAllByModifier(user);

    for (UserCreatedEntity resource: modifiedByUser) {
      resource.setLastModifier(resetUser);
      dao.persist((T) resource);
    }
  }
}
