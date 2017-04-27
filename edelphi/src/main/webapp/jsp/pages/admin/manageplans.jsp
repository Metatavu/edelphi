<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>

<!DOCTYPE html>
<html>
	<head>
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	  <title><fmt:message key="admin.managePlan.pageTitle"/></title>
	  <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
	</head>
	<body class="environment_admin planmanagement">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.managePlans"/></c:set>
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/managementplan.page?lang=${dashboardLang}" name="breadcrumbPageUrl"/>
    </jsp:include>
	  
	  <div class="GUI_pageWrapper">
	    <div class="GUI_pageContainer">
        <jsp:include page="/jsp/blocks/admin/manageplans.jsp"/>
	    </div>
	  </div>
	</body>
</html>