package fi.metatavu.edelphi.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.dao.base.DelfoiBulletinDAO;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.panels.PanelInvitationDAO;
import fi.metatavu.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.metatavu.edelphi.dao.users.UserEmailDAO;
import fi.metatavu.edelphi.domainmodel.base.Delfoi;
import fi.metatavu.edelphi.domainmodel.base.DelfoiBulletin;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitation;
import fi.metatavu.edelphi.domainmodel.panels.PanelInvitationState;
import fi.metatavu.edelphi.domainmodel.panels.PanelState;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocument;
import fi.metatavu.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.domainmodel.users.UserEmail;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.AuthUtils;
import fi.metatavu.edelphi.utils.LocalDocumentComparator;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.languagedetect.TextLanguageDetector;

public class IndexPageController extends PageController {

  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    super.process(pageRequestContext);
    
    PanelDAO panelDAO = new PanelDAO();
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    boolean ignoreBulletinLocale = pageRequestContext.getBoolean("ignoreBulletinLocale");
    
    if (ignoreBulletinLocale) {
    	pageRequestContext.getRequest().setAttribute("ignoreBulletinLocale", true);
    }
    
    pageRequestContext.getRequest().setAttribute("selectedLanguage", locale.getLanguage());
    
    List<Panel> openPanels = panelDAO.listByDelfoiAndAccessLevelAndState(delfoi, PanelAccessLevel.OPEN, PanelState.IN_PROGRESS); 
    Collections.sort(openPanels, new Comparator<Panel>() {
      @Override
      public int compare(Panel o1, Panel o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("openPanels", openPanels);

    User loggedUser = RequestUtils.getUser(pageRequestContext);
    if (loggedUser != null) {
      List<Panel> myPanels = ActionUtils.isSuperUser(pageRequestContext) ? panelDAO.listByDelfoi(delfoi) : panelDAO.listByDelfoiAndUser(delfoi, loggedUser);
      Collections.sort(myPanels, new Comparator<Panel>() {
        @Override
        public int compare(Panel o1, Panel o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      pageRequestContext.getRequest().setAttribute("myPanels", myPanels);

      // Pending panel invitations
      
      if (loggedUser.getDefaultEmail() != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
        List<PanelInvitation> myPanelInvitations = new ArrayList<PanelInvitation>();
        List<UserEmail> emails = userEmailDAO.listByUser(loggedUser);
        for (UserEmail email : emails) {
          myPanelInvitations.addAll(panelInvitationDAO.listByEmailAndState(email.getAddress(), PanelInvitationState.PENDING));
        }
        pageRequestContext.getRequest().setAttribute("myPanelInvitations", myPanelInvitations);
      }
    }

    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });
    
    if (!ignoreBulletinLocale) {
	  	TextLanguageDetector langdetect = null;
			try {
				langdetect = TextLanguageDetector.getInstance();
			} catch (IOException e) {
				e.printStackTrace();
			}
      
      if (langdetect != null) {
    		Iterator<DelfoiBulletin> bulletinIterator = bulletins.iterator();
      	
    		while (bulletinIterator.hasNext()) {
        	DelfoiBulletin bulletin = bulletinIterator.next();
        	String text = bulletin.getSummary().toString();
        	String lang = langdetect.getLanguage(text);
        	
      		if (lang == null || !lang.equals(locale.getLanguage())) {
      			bulletinIterator.remove();
      		}
        }
      }
    }
        
	  Long authSourceId = AuthUtils.getAuthSource("Keycloak").getId();
	  String pageContents = getIndexPageContents(delfoi, locale);
	
	  pageRequestContext.getRequest().setAttribute("pageContents", pageContents);
	  pageRequestContext.getRequest().setAttribute("authSourceId", authSourceId);
	  pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    
    // Action access information
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/index.jsp");
  }
  
  private String getIndexPageContents(Delfoi delfoi, Locale locale) {
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();
    LocalDocument indexPageDocument = MaterialUtils.findIndexPageDocument(delfoi, locale);

    if (indexPageDocument != null) {
      return StringUtils.trimToNull(localDocumentPageDAO.listByDocument(indexPageDocument).stream()
        .sorted(new LocalDocumentComparator())
        .map(LocalDocumentPage::getContent)
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining()));
    }

    return null;
  }
}
