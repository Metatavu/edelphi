package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;
import one.util.streamex.StreamEx;

public class InvitationListPageController extends PanelPageController {

  public InvitationListPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public Feature getFeature() {
    return Feature.MANAGE_PANEL_INVITATIONS;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    // Statistic variables to help creating the user interface
    
    int addedCount = 0;
    int acceptedCount = 0;
    int registeredCount = 0;
    int failedCount = 0;
    int queuedCount = 0;
    int declinedCount = 0;
    int pendingCount = 0;

    List<UserBean> userBeans = new ArrayList<>();
    List<String> emails = new ArrayList<>();
    
    // Convert panel users to user beans
    
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndRoleAndStamp(panel, panel.getDefaultPanelUserRole(), panel.getCurrentStamp());
    for (PanelUser panelUser : panelUsers) {
      String type = UserBean.ADDED;
      switch (panelUser.getJoinType()) {
        case ADDED:
          addedCount++;
          break;
        case INVITED:
          acceptedCount++;
          type = UserBean.ACCEPTED;
          break;
        case REGISTERED:
          registeredCount++;
          type = UserBean.REGISTERED;
          break;
      }
      Long userId = panelUser.getUser().getId();
      String firstName = panelUser.getUser().getFirstName();
      String lastName = panelUser.getUser().getLastName();
      String fullName = panelUser.getUser().getFullName(true,  false);
      UserEmail userEmail = panelUser.getUser().getDefaultEmail();
      String obfuscatedEmail = userEmail == null ? null : userEmail.getObfuscatedAddress();
      String email = userEmail == null ? null : userEmail.getAddress();
      
      if (email != null) {
        emails.add(email);
      }
      
      userBeans.add(new UserBean(type, userId, firstName, lastName, fullName, obfuscatedEmail));
    }

    // Only invitations not in previous lists (by email) and not in state accepted are accepted
    
    List<PanelInvitation> invitations = StreamEx.of(panelInvitationDAO.listByPanel(panel))
      .filter(invitation -> invitation.getState() != PanelInvitationState.ACCEPTED)
      .filter(invitation -> !emails.contains(invitation.getEmail()))
      .distinct(PanelInvitation::getEmail)
      .sortedBy(PanelInvitation::getEmail)
      .collect(Collectors.toList());
    
    // Convert invitations to user beans
    
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    for (PanelInvitation invitation : invitations) {
      String type = UserBean.ACCEPTED;
      switch (invitation.getState()) {
        case ACCEPTED:
          acceptedCount++;
          break;
        case DECLINED:
          declinedCount++;
          type = UserBean.DECLINED;
          break;
        case PENDING:
          pendingCount++;
          type = UserBean.PENDING;
          break;
        case SEND_FAIL:
          failedCount++;
          type = UserBean.FAILED;
          break;
        case IN_QUEUE:
        case BEING_SENT:
          queuedCount++;
          type = UserBean.QUEUED;
          break;
      }
      UserEmail userEmail = userEmailDAO.findByAddress(invitation.getEmail());
      Long userId = userEmail == null ? null : userEmail.getUser().getId();
      String firstName = userEmail == null ? null : userEmail.getUser().getFirstName();
      String lastName = userEmail == null ? null : userEmail.getUser().getLastName();
      String fullName = userEmail == null ? null : userEmail.getUser().getFullName(true,  false);
      String email = userEmail == null ? invitation.getEmail() : invitation.getObfuscatedEmail();
      userBeans.add(new UserBean(type, userId, firstName, lastName, fullName, email));
    }

    Collections.sort(userBeans, new Comparator<UserBean>() {
      public int compare(UserBean o1, UserBean o2) {
        String s1 = o1.getFullName() == null ? o1.getEmail() : o1.getFullName();
        s1 = s1 == null ? "" : s1.toLowerCase();
        String s2 = o2.getFullName() == null ? o2.getEmail() : o2.getFullName();
        s2 = s2 == null ? "" : s2.toLowerCase();
        return s1.compareTo(s2);
      }
    });
    
    // Add request attributes
    
    pageRequestContext.getRequest().setAttribute("addedCount", addedCount);
    pageRequestContext.getRequest().setAttribute("acceptedCount", acceptedCount);
    pageRequestContext.getRequest().setAttribute("registeredCount", registeredCount);
    pageRequestContext.getRequest().setAttribute("declinedCount", declinedCount);
    pageRequestContext.getRequest().setAttribute("pendingCount", pendingCount);
    pageRequestContext.getRequest().setAttribute("queuedCount", queuedCount);
    pageRequestContext.getRequest().setAttribute("failedCount", failedCount);
    pageRequestContext.getRequest().setAttribute("userBeans", userBeans);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/inviteusers_listusers.jsp");
  }

  public class UserBean {
    public UserBean(String type, Long userId, String firstName, String lastName, String fullName, String email) {
      this.setType(type);
      this.setUserId(userId);
      this.setFirstName(firstName);
      this.setLastName(lastName);
      this.setFullName(fullName);
      this.setEmail(email);
    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }
    public Long getUserId() {
      return userId;
    }
    public void setUserId(Long userId) {
      this.userId = userId;
    }
    public String getFullName() {
      return fullName;
    }
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public String getFirstName() {
      return firstName;
    }
    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }
    public String getLastName() {
      return lastName;
    }
    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
    private String type;
    private Long userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    public static final String ADDED = "ADDED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REGISTERED = "REGISTERED";
    public static final String DECLINED = "DECLINED";
    public static final String PENDING = "PENDING";
    public static final String QUEUED = "QUEUED";
    public static final String FAILED = "FAILED";
  }
}
