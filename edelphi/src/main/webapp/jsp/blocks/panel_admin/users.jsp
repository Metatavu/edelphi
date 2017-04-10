<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.dashboard.panelUsersTitle" name="titleLocale"/>
  </jsp:include>

  <div id="panelAdminDashboardUsersBlockContent" class="blockContent">
  
    <ed:feature featurePage="panel/admin/inviteusers.page">
      <div class="panelAdminUserRow">
        <div class="panelAdminGenericTitle"><a href="inviteusers.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.usersInviteAction" /></a></div>
        <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.usersInviteDescription" /></div>
      </div>
    </ed:feature>
    
    <ed:feature featurePage="panel/admin/managepanelusers.page">
      <div class="panelAdminUserRow">
        <div class="panelAdminGenericTitle"><a href="managepanelusers.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.usersManageUsersAction" /></a></div>
        <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.usersManageUsersDescription" /></div>
      </div>
    </ed:feature>
    
    <ed:feature featurePage="panel/admin/managepanelusergroups.page">
      <div class="panelAdminUserRow">
        <div class="panelAdminGenericTitle"><a href="managepanelusergroups.page?panelId=${panel.id}"><fmt:message key="panel.admin.dashboard.usersManageUsergroupsAction" /></a></div>
        <div class="panelAdminGenericDescription"><fmt:message key="panel.admin.dashboard.usersManageUsergroupsDescription" /></div>
      </div>
    </ed:feature>
    
  </div>

</div>