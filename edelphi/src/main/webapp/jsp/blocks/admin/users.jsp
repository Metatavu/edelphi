<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block settingsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.dashboard.usersTitle" name="titleLocale"/>
  </jsp:include>
  
  <c:if test="${actions['MANAGE_USERS']}">
    <div class="panelAdminGenericRow">
      <div class="panelAdminGenericTitle"><a href="/admin/searchusers.page"><fmt:message key="admin.dashboard.userSearchAction" /></a></div>
      <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.userSearchDescription" /></div>
    </div>
  </c:if>
  
</div>