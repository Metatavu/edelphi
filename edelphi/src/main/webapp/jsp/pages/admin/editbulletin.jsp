<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	  <title><fmt:message key="admin.managePanelBulletins.pageTitle"/></title>
	  <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
      <jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
      <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
      <script type="text/javascript" src="//cdn.metatavu.io/libs/moment/2.17.1/moment-with-locales.js"></script>
      <script type="text/javascript" src="//cdn.metatavu.io/libs/flatpickr/2.6.1/flatpickr.js"></script>
      <script type="text/javascript" src="//cdn.metatavu.io/libs/flatpickr/2.6.1/l10n/fi.js"></script>
      <link rel="stylesheet" href="//cdn.metatavu.io/libs/flatpickr/2.6.1/flatpickr.min.css"></link>
      <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/admin/bulletinlistblockcontroller.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/admin/bulletineditorblockcontroller.js"></script>
	</head>
	<body class="environment_admin index">
	
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.createPanelBulletin"/></c:set>
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/bulletins.page?lang=${dashboardLang}" name="breadcrumbPageUrl"/>
    </jsp:include>
	  
	  <div class="GUI_pageWrapper">
	   
	    <div class="GUI_pageContainer">
	
<%-- 	      <jsp:include page="/jsp/fragments/page_title.jsp"> --%>
<%-- 	        <jsp:param value="admin.managePanelBulletins.pageTitle" name="titleLocale" /> --%>
<%-- 	        <jsp:param name="titleLocaleParam" value="${panel.name}"/> --%>
<%-- 	      </jsp:include> --%>
	
	      <div class="GUI_adminManageBulletinsNarrowColumn">
            <c:set var="bulletins" value="${bulletins}" scope="request"></c:set>
	        <jsp:include page="/jsp/blocks/admin/bulletins_list.jsp"></jsp:include>
	      </div>
	
	      <div class="GUI_adminManageBulletinsWideColumn">
            <c:set var="bulletin" value="${bulletin}" scope="request"></c:set>
	        <jsp:include page="/jsp/blocks/admin/bulletineditor.jsp"></jsp:include>
          </div>
	    </div>
	
	  </div>
	</body>
</html>