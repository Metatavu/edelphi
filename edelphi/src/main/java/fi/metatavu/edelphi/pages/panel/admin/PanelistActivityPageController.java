package fi.metatavu.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fi.metatavu.edelphi.smvcj.PageNotFoundException;
import fi.metatavu.edelphi.smvcj.controllers.PageRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.dao.panels.PanelUserDAO;
import fi.metatavu.edelphi.dao.querydata.QueryReplyDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.panels.Panel;
import fi.metatavu.edelphi.domainmodel.panels.PanelUser;
import fi.metatavu.edelphi.domainmodel.querydata.QueryReply;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.pages.panel.PanelPageController;
import fi.metatavu.edelphi.utils.ActionUtils;
import fi.metatavu.edelphi.utils.RequestUtils;
import fi.metatavu.edelphi.utils.ResourceUtils;

public class PanelistActivityPageController extends PanelPageController {

  public PanelistActivityPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    QueryDAO queryDAO = new QueryDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();

    // Panel
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    pageRequestContext.getRequest().setAttribute("panel", panel);
    setJsDataVariable(pageRequestContext, "panelId", panel.getId().toString());
    
    // Queries of the panel

    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    // Selected query, ensuring that it belongs to the current panel 
    
    Long queryId = pageRequestContext.getLong("queryId");
    if (queryId != null) {
      Query query = queryDAO.findById(queryId);
      Panel queryPanel = ResourceUtils.getResourcePanel(query);
      if (queryPanel == null || !queryPanel.getId().equals(panel.getId())) {
        throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
      }
      pageRequestContext.getRequest().setAttribute("query", query);
      
      // Panelists
      
      List<PanelUser> panelUsers = panelUserDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
      Collections.sort(panelUsers, new Comparator<PanelUser>() {
        public int compare(PanelUser o1, PanelUser o2) {
          String s1 = o1.getUser().getFullName() == null ? "" : o1.getUser().getFullName().toLowerCase();
          String s2 = o2.getUser().getFullName() == null ? "" : o2.getUser().getFullName().toLowerCase();
          return s1.compareTo(s2);
        }
      });
      
      // Replied and unreplied panelists as PanelistBeans
      
      List<PanelistBean> repliedPanelists = new ArrayList<PanelistBean>();
      List<PanelistBean> unrepliedPanelists = new ArrayList<PanelistBean>();
      List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStampAndArchived(query, panel.getCurrentStamp(), Boolean.FALSE);
      for (PanelUser panelUser : panelUsers) {
        Long userId = panelUser.getUser().getId();

        PanelistBean panelistBean = new PanelistBean();
        panelistBean.setName(panelUser.getUser().getFullName(true, false));
        panelistBean.setEmail(panelUser.getUser().getDefaultEmailAsObfuscatedString());
        panelistBean.setLastLogin(panelUser.getUser().getLastLogin());
        
        for (QueryReply queryReply : queryReplies) {
          Long replyUserId = queryReply.getUser().getId();
          if (userId.equals(replyUserId)) {
            queryReplies.remove(queryReply);
            panelistBean.setReplyDate(queryReply.getLastModified());
            break;
          }
        }
        
        if (panelistBean.getReplyDate() != null) {
          repliedPanelists.add(panelistBean);
        }
        else {
          unrepliedPanelists.add(panelistBean);
        }
      }

      pageRequestContext.getRequest().setAttribute("repliedPanelists", repliedPanelists);
      pageRequestContext.getRequest().setAttribute("unrepliedPanelists", unrepliedPanelists);
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/panelistactivity.jsp");
  }
  
  public class PanelistBean {
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public Date getLastLogin() {
      return lastLogin;
    }
    public void setLastLogin(Date lastLogin) {
      this.lastLogin = lastLogin;
    }
    public Date getReplyDate() {
      return replyDate;
    }
    public void setReplyDate(Date replyDate) {
      this.replyDate = replyDate;    }
    private String name;
    private String email;
    private Date lastLogin;
    private Date replyDate;
  }

}