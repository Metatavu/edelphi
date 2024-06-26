package fi.metatavu.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.metatavu.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.panels.PanelDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelStamp;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.query.QueryPageHandler;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.QueryDataUtils;
import fi.metatavu.edelphi.utils.QueryPageBean;
import fi.metatavu.edelphi.utils.RequestUtils;

public class ViewQueryPageController extends PanelPageController {

  public ViewQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public Feature getFeature() {
    return Feature.BASIC_USAGE;
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelDAO panelDAO = new PanelDAO();
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();

    Long panelId = pageRequestContext.getLong("panelId");
    Long queryId = pageRequestContext.getLong("queryId");
    Integer pageNumber = pageRequestContext.getInteger("page");
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();

    Panel panel = panelDAO.findById(panelId);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Query query = queryDAO.findById(queryId);
    if (query == null) {
      
      // Query does not exist
      
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    else if (query.getVisible() != true) {
      
      // Query is hidden; redirect the user to an information page
      
      pageRequestContext.getRequest().setAttribute("panel",  panel);
      pageRequestContext.getRequest().setAttribute("statusCode", EdelfoiStatusCode.OK);
      pageRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.queryHidden", new String[] { query.getName() }));
      pageRequestContext.setIncludeJSP("/jsp/pages/error.jsp");
    }
    else if (query.getState() == QueryState.CLOSED) {

      // Query is closed; redirect the user to an information page
      
      pageRequestContext.getRequest().setAttribute("panel",  panel);
      pageRequestContext.getRequest().setAttribute("statusCode", EdelfoiStatusCode.OK);
      pageRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.queryClosed", new String[] { query.getName() }));
      pageRequestContext.setIncludeJSP("/jsp/pages/error.jsp");
    }
    else {
      
      // Query is accessible

      QueryPage queryPage = null;

      if (pageNumber == null) {
        pageNumber = 0;
        Long totalPageCount = queryPageDAO.countByQuery(query);

        do {
          queryPage = queryPageDAO.findByQueryAndPageNumber(query, pageNumber);
          if ((queryPage != null) && (queryPage.getVisible() == true) && (queryPage.getQuerySection() != null) && (queryPage.getQuerySection().getVisible() == true))
            break;
          pageNumber++;
        } while (pageNumber < totalPageCount);

      } else {
        queryPage = queryPageDAO.findByQueryAndPageNumber(query, pageNumber);
      }

      if ((queryPage == null) || (queryPage.getVisible() != true)) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.EMPTY_QUERY, messages.getText(locale, "exception.1020.emptyQuery"));
      }

      if ((queryPage.getQuerySection() == null) || (queryPage.getQuerySection().getVisible() != true)) {
        throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
      }

      if (query.getState() == QueryState.EDIT) {
        if (!ActionUtils.hasPanelAccess(pageRequestContext, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString())) {
          throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
        } else {
          pageRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.block.query.editStateWarning"));
        }
      }

      int uiPageNumber = 1;
      List<QueryPageBean> naviPageBeans = new ArrayList<QueryPageBean>();
      List<QueryPage> naviPages = queryPageDAO.listByQuery(query);
      Collections.sort(naviPages, new Comparator<QueryPage>() {
        @Override
        public int compare(QueryPage o1, QueryPage o2) {
          return o1.getPageNumber().compareTo(o2.getPageNumber());
        }
      });
      for (QueryPage naviPage : naviPages) {
        if (naviPage.getVisible()) {
          naviPageBeans.add(new QueryPageBean(naviPage.getTitle(), naviPage.getPageNumber(), uiPageNumber++));
        }
      }

      User loggedUser = null;
      if (pageRequestContext.isLoggedIn())
        loggedUser = userDAO.findById(pageRequestContext.getLoggedUserId());

      // TODO support stamps in read-only mode; for now, ensure the latest stamp is always in use
      PanelStamp stamp = RequestUtils.getActiveStamp(pageRequestContext);
      if (stamp != null && !stamp.getId().equals(panel.getCurrentStamp().getId())) {
        RequestUtils.setActiveStamp(pageRequestContext, panel.getCurrentStamp().getId());
      }

      QueryReply queryReply = null;

      synchronized (this) {
        if (query.getState() == QueryState.ACTIVE) {
          queryReply = QueryDataUtils.findQueryReply(pageRequestContext, loggedUser, query);
          if (queryReply == null) {
            QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
            queryReply = queryReplyDAO.create(loggedUser, query, panel.getCurrentStamp(), loggedUser);
          }
          QueryDataUtils.storeQueryReplyId(pageRequestContext.getRequest().getSession(), queryReply);
        }
      }

      Integer previousPageNumber = pageNumber - 1;
      Integer nextPageNumber = pageNumber + 1;
      Integer currentVisiblePageNumber = pageNumber + 1;
      Integer maxPageNumber = queryPageDAO.findMaxPageNumber(query);

      while (nextPageNumber <= maxPageNumber) {
        QueryPage page = queryPageDAO.findByQueryAndPageNumber(query, nextPageNumber);

        if ((page != null) && (page.getVisible() == true) && (page.getQuerySection().getVisible() == true) && (page.getQuerySection().getArchived() == false))
          break;

        nextPageNumber++;
      }

      if (nextPageNumber > maxPageNumber)
        nextPageNumber = null;

      if (previousPageNumber >= 0) {
        while (previousPageNumber >= 0) {
          QueryPage page = queryPageDAO.findByQueryAndPageNumber(query, previousPageNumber);

          if ((page != null) && (page.getVisible() == true) && (page.getQuerySection().getVisible() == true) && (page.getQuerySection().getArchived() == false))
            break;

          previousPageNumber--;
        }

        if (previousPageNumber < 0)
          previousPageNumber = null;

        // Calculate the visible page number by going through previous pages and decreasing if page or section is invisible 
        Integer visiblePageNumIter = pageNumber - 1;
        while (visiblePageNumIter >= 0) {
          QueryPage visiPage = queryPageDAO.findByQueryAndPageNumber(query, visiblePageNumIter);

          if ((visiPage == null) || (!visiPage.getVisible()) || (!visiPage.getQuerySection().getVisible()))
            currentVisiblePageNumber--;

          visiblePageNumIter--;
        }

      } else {
        previousPageNumber = null;
      }

      QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
      queryPageHandler.renderPage(pageRequestContext, queryPage, queryReply);

      boolean hasAnswers = queryQuestionAnswerDAO.countByQueryPageAndQueryReply(queryPage, queryReply) > 0;

      ActionUtils.includeRoleAccessList(pageRequestContext);

      if (ActionUtils.hasPanelAccess(pageRequestContext, DelfoiActionName.MANAGE_QUERY_COMMENTS.toString()))
        setJsDataVariable(pageRequestContext, "canManageComments", "true");

      pageRequestContext.getRequest().setAttribute("queryPage", queryPage);
      pageRequestContext.getRequest().setAttribute("queryPages", naviPageBeans);
      pageRequestContext.getRequest().setAttribute("queryReply", queryReply);
      pageRequestContext.getRequest().setAttribute("queryNextPageNumber", nextPageNumber);
      pageRequestContext.getRequest().setAttribute("queryPreviousPageNumber", previousPageNumber);
      pageRequestContext.getRequest().setAttribute("currentVisiblePageNumber", currentVisiblePageNumber);
      pageRequestContext.getRequest().setAttribute("queryPageCount", queryPageDAO.countByQueryAndVisible(query, Boolean.TRUE));
      pageRequestContext.getRequest().setAttribute("panel", panel);
      pageRequestContext.getRequest().setAttribute("allowEditReply", query.getAllowEditReply());
      pageRequestContext.getRequest().setAttribute("hasAnswers", hasAnswers);

      pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewquery.jsp");
    }
  }
}
