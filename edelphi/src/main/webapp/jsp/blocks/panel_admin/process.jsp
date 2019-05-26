<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.dashboard.panelProcessTitle" name="titleLocale"/>
  </jsp:include>
  
  <div id="panelAdminDashboardProcessBlockContent" class="blockContent">
  
    <div id="panelAdminDashboardProcessLeftBlock">
      <ed:feature featurePage="panel/admin/panelistactivity.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="panelistactivity.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessPanelistactivityAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessPanelistactivityDescription" /></div>
        </div>
      </ed:feature>
      <!-- 
      <ed:feature featurePage="panel/admin/messagecenter.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="messagecenter.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessMessageCenterAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessMessageDescription" /></div>
        </div>
      </ed:feature>
      -->
      <ed:feature featurePage="panel/admin/queryresults.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="queryresults.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessQueryResultsAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessQueryResultsDescription" /></div>
        </div>
      </ed:feature>

      <ed:feature featurePage="panel/admin/comparereports.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="comparereports.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessCompareReportsAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessCompareReportsDescription" /></div>
        </div>
      </ed:feature>

      <ed:feature featurePage="panel/admin/commentview.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="commentview.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessCommentViewAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessCommentViewDescription" /></div>
        </div>
      </ed:feature>
    </div>
    
    <div id="panelAdminDashboardProcessRightBlock">
    
      <ed:feature featurePage="panel/admin/managepanelstamps.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="managepanelstamps.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessPanelStampsAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessPanelStampsDescription" /></div>
        </div>      
      </ed:feature>

      <ed:feature featurePage="panel/admin/panelexperts.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="panelexperts.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessQueryExpertsAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessQueryExpertsDescription" /></div>
        </div>
      </ed:feature>
      
      <ed:feature featurePage="panel/admin/sendemail.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="sendemail.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessEmailAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessEmailDescription" /></div>
        </div>
      </ed:feature>

      <ed:feature featurePage="panel/admin/managepanelbulletins.page">
        <div class="panelAdminProcessRow">
          <div class="panelAdminGenericTitle"><a href="managepanelbulletins.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.panelProcessBulletinsAction" /></a></div>
          <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.panelProcessBulletinsDescription" /></div>
        </div>
      </ed:feature>

    </div>
    
    <div class="clearBoth"></div>
  
  </div>
  
  

</div>