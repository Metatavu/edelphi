package fi.metatavu.edelphi;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiActionDAO;
import fi.metatavu.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.metatavu.edelphi.dao.base.DelfoiDAO;
import fi.metatavu.edelphi.dao.users.DelfoiUserRoleDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiAction;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.users.DelfoiUserRole;
import fi.metatavu.edelphi.domainmodel.users.UserRole;
import fi.metatavu.edelphi.utils.SystemUtils;

@Singleton
@Startup
@DependsOn (value = "ActionsInitializer")
public class TestDefaultActionsInitializer {
  
  private static final Logger logger = Logger.getLogger(TestDefaultActionsInitializer.class.getName());

  @PersistenceContext
  private EntityManager entityManager;

  @PostConstruct
  public void init() {
    GenericDAO.setEntityManager(entityManager);
    try {
      if (SystemUtils.isTestEnvironment()) {
        initializeDefaultPermissions();
      }
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }

  private void initializeDefaultPermissions() {
    createDelfoiActionDefaults(DelfoiActionName.CREATE_PANEL, true, true, true, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_BULLETINS, true, true, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_USER_PROFILE, true, true, true, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_DELFOI_MATERIALS, true, true, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, true, false, false, false);
    createDelfoiActionDefaults(DelfoiActionName.MANAGE_DELFOI, true, true, false, false);
  }
  
  private void createDelfoiActionDefaults(DelfoiActionName delfoiActionName, boolean administrators, boolean managers, boolean users, boolean guents) {
    Long administratorsRoleId = 2l;
    Long managersId = 3l;
    Long usersId = 4l;
    Long guestsId = 5l;
    
    if (administrators) {
      createDelfoiAction(delfoiActionName, administratorsRoleId);
    }

    if (managers) {
      createDelfoiAction(delfoiActionName, managersId);
    }

    if (users) {
      createDelfoiAction(delfoiActionName, usersId);
    }
    
    if (guents) {
      createDelfoiAction(delfoiActionName, guestsId);
    }
  }
  
  private void createDelfoiAction(DelfoiActionName delfoiActionName, Long roleId) {
    DelfoiUserRoleDAO delfoiUserRoleDAO = new DelfoiUserRoleDAO();
    
    DelfoiUserRole userRole = delfoiUserRoleDAO.findById(roleId);
    if (userRole == null) {
      logger.severe(String.format("Could not find user role %d", roleId));
    } else {
      createDelfoiAction(delfoiActionName, userRole);
    }
  }
  
  private void createDelfoiAction(DelfoiActionName delfoiActionName, UserRole userRole) {
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    DelfoiDAO delfoiDAO = new DelfoiDAO();
    Delfoi delfoi = delfoiDAO.findById(1l);
    DelfoiAction delfoiAction = delfoiActionDAO.findByActionName(delfoiActionName.name());
    
    delfoiUserRoleActionDAO.create(delfoi, delfoiAction, userRole);
    
  }
  
}
