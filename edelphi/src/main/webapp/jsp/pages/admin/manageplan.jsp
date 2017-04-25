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
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/manageplanblockcontroller.js"></script>
	</head>
	<body class="environment_admin planmanagement">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.managePlan"/></c:set>
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/manageplan.page?planId=${plan.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

	  <div class="GUI_pageWrapper">
	   
	    <div class="GUI_pageContainer">
        <div class="blockTitle"><h2><fmt:message key="admin.managePlan.title" /></h2></div>
        <div id="panelAdminPlanEditorBlock">

          <form>
            <jsp:include page="/jsp/fragments/formfield_select.jsp">
              <jsp:param name="labelLocale" value="admin.managePlan.visible"/>
              <jsp:param name="name" value="visible"/>
              <jsp:param name="options" value="VISIBLE,HIDDEN"/>
              <jsp:param name="option.VISIBLE" value="${visibleLocale}"/>
              <jsp:param name="option.HIDDEN" value="${hiddenLocale}"/>
              <jsp:param name="value" value="${plan.visible ? 'VISIBLE' : 'HIDDEN'}"/>
            </jsp:include>
                  
            <ed:include page="/jsp/fragments/formfield_select.jsp">
              <ed:param name="labelLocale" value="admin.managePlan.subscriptionLevel"/>
              <ed:param name="name" value="subscriptionLevel" />
              <ed:param name="options" value="${fn:join(subscriptionLevels,',')}" />
              <ed:param name="value" value="${plan.subscriptionLevel}" />
              <c:forEach var="subscriptionLevel" items="${subscriptionLevels}">
                <ed:param name="option.${subscriptionLevel}" value="${subscriptionLevelTitles[subscriptionLevel]}"/>
              </c:forEach>
            </ed:include>
            
            <jsp:include page="/jsp/fragments/formfield_text.jsp">
              <jsp:param name="labelLocale" value="admin.managePlan.price" />
              <jsp:param name="name" value="price" />
              <jsp:param name="value" value="${plan.price}" />
              <jsp:param name="classes" value="required" />
            </jsp:include>
            
            <jsp:include page="/jsp/fragments/formfield_text.jsp">
              <jsp:param name="labelLocale" value="admin.managePlan.days" />
              <jsp:param name="name" value="days" />
              <jsp:param name="value" value="${plan.days}" />
              <jsp:param name="classes" value="required" />
            </jsp:include>
            
            <c:forEach var="locale" items="${locales}">
              <jsp:include page="/jsp/fragments/formfield_text.jsp">
                <jsp:param name="labelText" value="${nameLocales[locale]}" />
                <jsp:param name="name" value="name-${locale}" />
                <jsp:param name="value" value="${nameValues[locale]}" />
                <jsp:param name="classes" value="required" />
              </jsp:include>
            </c:forEach>
            
            <c:forEach var="locale" items="${locales}">
              <jsp:include page="/jsp/fragments/formfield_memo.jsp">
                <jsp:param name="labelText" value="${descriptionLocales[locale]}" />
                <jsp:param name="name" value="description-${locale}" />
                <jsp:param name="value" value="${descriptionValues[locale]}" />
                <jsp:param name="classes" value="required" />
              </jsp:include>
            </c:forEach>
            
            <jsp:include page="/jsp/fragments/formfield_submit.jsp">
              <jsp:param name="labelLocale" value="admin.managePlan.save" />
              <jsp:param name="classes" value="formvalid" />
              <jsp:param name="name" value="save" />
            </jsp:include>
            
            <input type="hidden" name="planId" value="${plan.id}"/>
            
          </form>
  	    </div>
	    </div>
	  </div>
	</body>
</html>