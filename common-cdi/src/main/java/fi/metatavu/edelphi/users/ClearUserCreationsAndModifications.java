package fi.metatavu.edelphi.users;

import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
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
import fi.metatavu.edelphi.domainmodel.users.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    panelUserDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelUserDAO.persist(entity);
    });
    panelUserDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelUserDAO.persist(entity);
    });

    panelBulletinDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelBulletinDAO.persist(entity);
    });
    panelBulletinDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelBulletinDAO.persist(entity);
    });

    delfoiUserDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      delfoiUserDAO.persist(entity);
    });
    delfoiUserDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      delfoiUserDAO.persist(entity);
    });

    formDraftDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      formDraftDAO.persist(entity);
    });
    formDraftDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      formDraftDAO.persist(entity);
    });

    panelDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelDAO.persist(entity);
    });
    panelDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelDAO.persist(entity);
    });

    delfoiBulletinDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      delfoiBulletinDAO.persist(entity);
    });
    delfoiBulletinDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      delfoiBulletinDAO.persist(entity);
    });

    panelInvitationDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelInvitationDAO.persist(entity);
    });
    panelInvitationDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelInvitationDAO.persist(entity);
    });

    panelStampDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelStampDAO.persist(entity);
    });
    panelStampDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelStampDAO.persist(entity);
    });

    panelUserGroupDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      panelUserGroupDAO.persist(entity);
    });
    panelUserGroupDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      panelUserGroupDAO.persist(entity);
    });

    queryQuestionCommentDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryQuestionCommentDAO.persist(entity);
    });
    queryQuestionCommentDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryQuestionCommentDAO.persist(entity);
    });

    queryQuestionCommentCategoryDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryQuestionCommentCategoryDAO.persist(entity);
    });
    queryQuestionCommentCategoryDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryQuestionCommentCategoryDAO.persist(entity);
    });

    queryReplyDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryReplyDAO.persist(entity);
    });
    queryReplyDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryReplyDAO.persist(entity);
    });

    queryPageDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryPageDAO.persist(entity);
    });
    queryPageDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryPageDAO.persist(entity);
    });

    queryPageTemplateDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      queryPageTemplateDAO.persist(entity);
    });
    queryPageTemplateDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      queryPageTemplateDAO.persist(entity);
    });

    querySectionDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      querySectionDAO.persist(entity);
    });
    querySectionDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      querySectionDAO.persist(entity);
    });

    resourceDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      resourceDAO.persist(entity);
    });
    resourceDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      resourceDAO.persist(entity);
    });

    resourceLockDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      resourceLockDAO.persist(entity);
    });
    resourceLockDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      resourceLockDAO.persist(entity);
    });

    userDAO.listAllByCreator(user).forEach(entity -> {
      entity.setCreator(resetUser);
      userDAO.persist(entity);
    });
    userDAO.listAllByModifier(user).forEach(entity -> {
      entity.setLastModifier(resetUser);
      userDAO.persist(entity);
    });

  }
}
