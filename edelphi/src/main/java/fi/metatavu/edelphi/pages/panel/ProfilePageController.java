package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.dao.users.UserPasswordDAO;
import fi.metatavu.edelphi.dao.users.UserSettingDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserPassword;
import fi.metatavu.edelphi.domainmodel.users.UserSetting;
import fi.metatavu.edelphi.domainmodel.users.UserSettingKey;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ProfilePageController extends PanelPageController {

  public ProfilePageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    if (loggedUser != null) {
      UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
      pageRequestContext.getRequest().setAttribute("user", loggedUser);

      UserPassword userPassword = userPasswordDAO.findByUser(loggedUser);
      boolean hasPassword = userPassword != null;
      
      // Logon types
      
      AuthUtils.includeAuthSources(pageRequestContext);
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(loggedUser);
      pageRequestContext.getRequest().setAttribute("userIdentifications", userIdentifications);
      
      // Invitations
      
      if (loggedUser.getDefaultEmail() != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
        List<PanelInvitation> myInvitations = new ArrayList<PanelInvitation>();
        List<UserEmail> emails = userEmailDAO.listByUser(loggedUser);
        for (UserEmail email : emails) {
          myInvitations.addAll(panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.PENDING));
          myInvitations.addAll(panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.ACCEPTED));
        }
        Collections.sort(myInvitations, new Comparator<PanelInvitation>() {
          @Override
          public int compare(PanelInvitation o1, PanelInvitation o2) {
            String s1 = o1.getQuery() == null ? o1.getPanel().getName() : o1.getQuery().getName(); 
            String s2 = o2.getQuery() == null ? o2.getPanel().getName() : o2.getQuery().getName(); 
            return s1.toLowerCase().compareTo(s2.toLowerCase());
          }
        });
        pageRequestContext.getRequest().setAttribute("myInvitations", myInvitations);
      }

      // Ensures returning to the profile page if an external authentication source is added to the profile
      AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));

      pageRequestContext.getRequest().setAttribute("userHasPassword", hasPassword);
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    
    // Open panels
    
    List<Panel> openPanels = panelDAO.listByDelfoiAndAccessLevelAndState(delfoi, PanelAccessLevel.OPEN, PanelState.IN_PROGRESS); 
    Collections.sort(openPanels, new Comparator<Panel>() {
      @Override
      public int compare(Panel o1, Panel o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("openPanels", openPanels);

    // User settings (essentially just comment reply mails for now)
    
    UserSettingDAO userSettingDAO = new UserSettingDAO();
    UserSetting userSetting = userSettingDAO.findByUserAndKey(loggedUser, UserSettingKey.MAIL_COMMENT_REPLY);
    pageRequestContext.getRequest().setAttribute("userCommentMail", userSetting != null && "1".equals(userSetting.getValue()));

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/profile.jsp");
  }

}
