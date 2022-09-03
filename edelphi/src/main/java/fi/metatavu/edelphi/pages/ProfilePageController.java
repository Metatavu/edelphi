package fi.metatavu.edelphi.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.dao.users.UserIdentificationDAO;
import fi.metatavu.edelphi.dao.users.UserSettingDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.domainmodel.users.UserIdentification;
import fi.metatavu.edelphi.domainmodel.users.UserSetting;
import fi.metatavu.edelphi.domainmodel.users.UserSettingKey;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.*;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils.SubscriptionLevelSettings;

public class ProfilePageController extends PageController {

  public ProfilePageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    if (loggedUser != null) {
      List<Panel> myPanels = panelDAO.listByDelfoiAndUser(delfoi, loggedUser);
      Collections.sort(myPanels, new PanelComparator());
      pageRequestContext.getRequest().setAttribute("myPanels", myPanels);

      // Invitations (pending to left side panel listing, all in profile view)

      if (loggedUser.getDefaultEmail() != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
        List<PanelInvitation> pendingInvitations = new ArrayList<>();
        List<PanelInvitation> allInvitations = new ArrayList<>();
        List<UserEmail> emails = userEmailDAO.listByUser(loggedUser);
        
        for (UserEmail email : emails) {
          List<PanelInvitation> invitations = panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.PENDING); 
          pendingInvitations.addAll(invitations);
          allInvitations.addAll(invitations);
          allInvitations.addAll(panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.ACCEPTED));
        }
        
        Collections.sort(pendingInvitations, new PanelInvitationComparator());
        Collections.sort(allInvitations, new PanelInvitationComparator());
        pageRequestContext.getRequest().setAttribute("myPanelInvitations", pendingInvitations); // panel listing
        pageRequestContext.getRequest().setAttribute("myInvitations", allInvitations); // profile view
      }

      pageRequestContext.getRequest().setAttribute("user", loggedUser);
      
      // Logon types
      
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(loggedUser);
      pageRequestContext.getRequest().setAttribute("userIdentifications", userIdentifications);
      
      // User settings (essentially just comment reply mails for now)
      
      UserSettingDAO userSettingDAO = new UserSettingDAO();
      UserSetting userSetting = userSettingDAO.findByUserAndKey(loggedUser, UserSettingKey.MAIL_COMMENT_REPLY);
      pageRequestContext.getRequest().setAttribute("userCommentMail", userSetting != null && "1".equals(userSetting.getValue()));
      
      // Ensures returning to the profile page if an external authentication source is added to the profile
      AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
      
      SubscriptionLevelSettings subscriptionLevelSettings = SubscriptionLevelUtils.getSubscriptionLevelSettings(loggedUser.getSubscriptionLevel());
      pageRequestContext.getRequest().setAttribute("subscriptionLevelSettings", subscriptionLevelSettings);
      pageRequestContext.getRequest().setAttribute("subscriptionStarted", loggedUser.getSubscriptionStarted());
      pageRequestContext.getRequest().setAttribute("subscriptionEnds", loggedUser.getSubscriptionEnds());
      pageRequestContext.getRequest().setAttribute("activePanelCount", SubscriptionLevelUtils.countManagedActivePanels(loggedUser));
      pageRequestContext.getRequest().setAttribute("accountUrl", AuthUtils.getKeycloakAccountUrl());
      pageRequestContext.getRequest().setAttribute("paymentServicesDisabled", SystemUtils.PAYMENT_SERVICES_DISABLED);
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    // Open panels
    
    List<Panel> openPanels = panelDAO.listByDelfoiAndAccessLevelInAndState(delfoi, Arrays.asList(PanelAccessLevel.OPEN, PanelAccessLevel.ANONYMOUS), PanelState.IN_PROGRESS); 
    Collections.sort(openPanels, new PanelComparator());
    pageRequestContext.getRequest().setAttribute("openPanels", openPanels);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/profile.jsp");
  }
  
  private class PanelComparator implements Comparator<Panel> {

    @Override
    public int compare(Panel o1, Panel o2) {
      return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
    
  }
  
  private class PanelInvitationComparator implements Comparator<PanelInvitation> {

    @Override
    public int compare(PanelInvitation o1, PanelInvitation o2) {
      String s1 = o1.getQuery() == null ? o1.getPanel().getName() : o1.getQuery().getName(); 
      String s2 = o2.getQuery() == null ? o2.getPanel().getName() : o2.getQuery().getName(); 
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
    
  }

}
