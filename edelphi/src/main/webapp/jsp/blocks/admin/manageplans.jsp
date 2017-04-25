<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block managePlansBlock" id="adminManagePlansBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.managePlans.listTitle" name="titleLocale"/>
  </jsp:include>
  
  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
    <jsp:param name="items" value="CREATE"/>
    <jsp:param name="item.CREATE.tooltipLocale" value="admin.managePlans.createNewPlan"/>
    <jsp:param name="item.CREATE.href" value="/admin/manageplan.page?create=TRUE"/>
  </jsp:include>
  
  <div class="blockContent plansBlockList">
    <!-- admin.managePlans.createNewPlan -->
  
    <c:forEach var="plan" items="${plans}" >
      <div class="panelAdminGenericRow">
        <div class="panelAdminGenericTitle"><a href="/admin/manageplan.page?planId=${plan.id}">${planNames[plan.id]}</a></div>
        <div class="panelAdminGenericDescription">
          <fmt:message key="admin.managePlans.planDescription">
            <fmt:param value="${plan.days}"/>
            <fmt:param><fmt:message key="generic.subscriptionLevels.${plan.subscriptionLevel}"/></fmt:param>
            <fmt:param><fmt:formatNumber value="${plan.price}" currencyCode="${plan.currency}" type="currency" /></fmt:param>
            <fmt:param>
              <c:if test="${plan.visible eq false}"><fmt:message key="admin.managePlans.planDescriptionHidden"/></c:if>
            </fmt:param>
          </fmt:message>
        </div>
      </div>
    </c:forEach>
  </div>
  
</div>