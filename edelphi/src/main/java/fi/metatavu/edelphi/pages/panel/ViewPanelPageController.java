package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelBulletinDAO;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.metatavu.edelphi.domainmodel.panels.PanelBulletin;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.smvcj.controllers.RequestContext;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.MaterialBean;
import fi.metatavu.edelphi.utils.MaterialUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.TimeUtils;

public class ViewPanelPageController extends PanelPageController {

  public ViewPanelPageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void authorize(RequestContext requestContext) {
    Panel panel = RequestUtils.getPanel(requestContext);
    if (panel != null && panel.getAccessLevel().equals(PanelAccessLevel.OPEN)) {
      Long userId = requestContext.getLoggedUserId();
      if (userId != null) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findById(userId);
        if (user != null) {
          PanelUserDAO panelUserDAO = new PanelUserDAO();
          PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
          if (panelUser == null && !ActionUtils.isSuperUser(requestContext)) {
            panelUserDAO.create(panel, user, panel.getDefaultPanelUserRole(), PanelUserJoinType.REGISTERED, panel.getCurrentStamp(), user);
            authorizePanel(requestContext, panel, DelfoiActionName.ACCESS_PANEL.toString());
            return;
          }
        }
      }
    }
    super.authorize(requestContext);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    UserDAO userDAO = new UserDAO();
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    User loggedUser = pageRequestContext.getLoggedUserId() == null ? null : userDAO.findById(pageRequestContext.getLoggedUserId());
    List<Query> queries = queryDAO.listByFolderAndVisibleAndArchived(panel.getRootFolder(), Boolean.TRUE, Boolean.FALSE);
    Collections.sort(queries, new PanelComparator());
    
    List<QueryBean> unfinishedQueries = new ArrayList<>();
    List<QueryBean> finishedQueries = new ArrayList<>();
    List<QueryBean> notStartedQueries = new ArrayList<>();
    List<QueryBean> closedQueries = new ArrayList<>();
        
    String monthsText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.months");
    String monthText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.month");
    String weeksText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.weeks");
    String weekText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.week");
    String daysText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.days");
    String dayText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.day");
    String hoursText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.hours");
    String hourText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.hour");
    String minutesText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.minutes");
    String minuteText = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), "generic.timeRemaining.minute");
    
    for (Query query : queries) {
      Long timeToAnswer = null;
      String timeToAnswerText = null;
      if (query.getCloses() != null) {
        timeToAnswer = query.getCloses().getTime() - System.currentTimeMillis();
        timeToAnswerText = TimeUtils.printTimeRemaining(timeToAnswer, monthsText, monthText, weeksText, weekText, daysText, dayText, hoursText, hourText, minutesText, minuteText);
      }
        
      QueryBean queryBean = new QueryBean(query.getId(), query.getName(), query.getFullPath(), query.getDescription(), timeToAnswerText, query.getCreated(), query.getVisible(), query.getState());

      if ((query.getState() != QueryState.CLOSED) && ((timeToAnswer == null)||(timeToAnswer > 0))) {
        boolean listQuery = true;
        
        if (query.getState() == QueryState.EDIT) {
          listQuery = ActionUtils.hasPanelAccess(pageRequestContext, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
        }
        
        if (listQuery) {
          QueryReply queryReply = loggedUser == null ? null : QueryDataUtils.findQueryReply(pageRequestContext, loggedUser, query);
          if (queryReply != null) {
            if (Boolean.TRUE.equals(queryReply.getComplete())) {
              finishedQueries.add(queryBean);
            }
            else {
              unfinishedQueries.add(queryBean);
            }
          }
          else {
            notStartedQueries.add(queryBean);
          }
        }
      }
      else {
        closedQueries.add(queryBean);
      }
    }

    List<PanelBulletin> bulletins = panelBulletinDAO.listByPanelAndArchived(panel, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<PanelBulletin>() {
      @Override
      public int compare(PanelBulletin o1, PanelBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    pageRequestContext.getRequest().setAttribute("unfinishedQueries", unfinishedQueries);
    pageRequestContext.getRequest().setAttribute("finishedQueries", finishedQueries);
    pageRequestContext.getRequest().setAttribute("notStartedQueries", notStartedQueries);
    pageRequestContext.getRequest().setAttribute("closedQueries", closedQueries);

    try {
      List<MaterialBean> materials = MaterialUtils.listPanelMaterials(panel, false);
      Collections.sort(materials, new Comparator<MaterialBean>() {
        @Override
        public int compare(MaterialBean o1, MaterialBean o2) {
          return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
      });
      pageRequestContext.getRequest().setAttribute("materials", materials);
    }
    catch (Exception e) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewpanel.jsp");
  }
  
  private class PanelComparator implements Comparator<Query> {
    
    @Override
    public int compare(Query o1, Query o2) {
      return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
    
  }

  public class QueryBean {
    
    public QueryBean(Long id, String name, String fullPath, String description, String timeToAnswer, Date created, Boolean visible, QueryState state) {
      this.id = id;
      this.name = name;
      this.fullPath = fullPath;
      this.description = description;
      this.timeToAnswer = timeToAnswer;
      this.created = created;
      this.visible = visible;
      this.state = state;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getName() {
      return name;
    }
    
    public String getFullPath() {
      return fullPath;
    }
    
    public String getDescription() {
      return description;
    }
    
    public Date getCreated() {
      return created;
    }
    
    public String getTimeToAnswer() {
      return timeToAnswer;
    }
    
    public Boolean getVisible() {
      return visible;
    }
    
    public QueryState getState() {
      return state;
    }
    
    private Long id;
    private String name;
    private String fullPath;
    private String description;
    private Date created;
    private String timeToAnswer;
    private Boolean visible;
    private QueryState state;
  }
  
}
