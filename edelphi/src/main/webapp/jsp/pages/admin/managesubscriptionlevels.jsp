<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	  <title><fmt:message key="admin.manageSubscriptionLevels.pageTitle"/></title>
	  <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/managesubscriptionlevelsblockcontroller.js"></script>
	</head>
	<body class="environment_admin subscriptionlevels">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.manageSubscriptionLevels"/></c:set>
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/managesubscriptionlevels.page?lang=${dashboardLang}" name="breadcrumbPageUrl"/>
    </jsp:include>
	  
	  <div class="GUI_pageWrapper">
	   
	    <div class="GUI_pageContainer">
        <div class="blockTitle"><h2><fmt:message key="admin.manageSubscriptionLevels.title" /></h2></div>
        <div id="panelAdminSubscriptionLevelsEditorBlock">
          <form>
            <div class="formFieldContainer formSubscriptionLevelsContainer">
              <table>
                <thead>
                  <tr>
                    <th></th>
                    <c:forEach var="subscriptionLevel" items="${subscriptionLevels}">
                      <th><fmt:message key="generic.subscriptionLevels.${subscriptionLevel}"/></th>
                    </c:forEach>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="feature" items="${features}">
                    <tr>
                      <td><fmt:message key="generic.features.${feature}"/></td>
                      <c:forEach var="subscriptionLevel" items="${subscriptionLevels}">
                        <td>
                          <input type="checkbox" name="${subscriptionLevel}.${feature}" ${subscriptionLevelFeatureMap[subscriptionLevel][feature] ? 'checked="checked"' : ''} value="true"/>
                        </td>
                      </c:forEach>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            
            <jsp:include page="/jsp/fragments/formfield_submit.jsp">
              <jsp:param name="labelLocale" value="admin.manageSubscriptionLevels.save" />
              <jsp:param name="classes" value="formvalid" />
              <jsp:param name="name" value="save" />
            </jsp:include>
          </form>
  	    </div>
	    </div>
	  </div>
	</body>
</html>