package fi.metatavu.edelphi.pages.panel;

import java.util.UUID;

import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.smvcj.AccessDeniedException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.utils.RequestUtils;

/**
 * Page controller for handling requests into /panel/anonymouslogin.page -URL
 * 
 * @author Antti Leppä
 */
public class AnonymousLoginPageController extends PanelPageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext requestContext) {
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    QueryDAO queryDAO = new QueryDAO();

    Long queryId = requestContext.getLong("queryId");
    
    RequestUtils.logoutUser(requestContext, null);
    
    Panel panel = RequestUtils.getPanel(requestContext);
    if (panel == null) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
    
    if (panel.getState() != PanelState.IN_PROGRESS) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
    
    if (panel.getAccessLevel() != PanelAccessLevel.ANONYMOUS) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
    
    Query query = queryDAO.findById(queryId);
    if (query == null) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
    
    if (query.getState() != QueryState.ACTIVE) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
    
    String baseUrl = RequestUtils.getBaseUrl(requestContext.getRequest());
    User user = RequestUtils.getUser(requestContext);
    String email = String.format("anon-%s@edelphi.org", System.currentTimeMillis());
    String invitationHash = UUID.randomUUID().toString();
    panelInvitationDAO.create(panel, query, email, invitationHash, panel.getDefaultPanelUserRole(), PanelInvitationState.IN_QUEUE, user);
    String joinUrl = String.format("%s/joinpanel.page?panelId=%d&queryId=%d&hash=%s&join=1&anonymous=1", baseUrl, panel.getId(), query.getId(), invitationHash);
    
    requestContext.setRedirectURL(joinUrl);
  }

}
