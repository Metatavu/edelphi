<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block settingsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.dashboard.settingsTitle" name="titleLocale"/>
  </jsp:include>
  
  <c:if test="${actions['MANAGE_SUBSCRIPTION_LEVELS']}">
    <div class="panelAdminGenericRow">
      <div class="panelAdminGenericTitle"><a href="/admin/managesubscriptionlevels.page"><fmt:message key="admin.dashboard.subscriptionLevelsAction" /></a></div>
      <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.subscriptionLevelsDescription" /></div>
    </div>
  </c:if>
  
  <c:if test="${actions['MANAGE_PLANS']}">
    <div class="panelAdminGenericRow">
      <div class="panelAdminGenericTitle"><a href="/admin/manageplans.page"><fmt:message key="admin.dashboard.managePlansAction" /></a></div>
      <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.managePlansDescription" /></div>
    </div>
  </c:if>
  
</div>