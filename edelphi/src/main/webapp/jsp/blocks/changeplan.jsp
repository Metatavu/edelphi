<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="changeplan.block.currentPlanTitle" name="titleLocale" />
  </jsp:include>

  <div id="currentPlanGenericBlockContent" class="blockContent">
    <div class="currentPlanRow">
      <label><fmt:message key="changeplan.block.currentSubscriptionLevelTitle"/></label>
      <span>
        <fmt:message key="changeplan.block.currentText">
          <fmt:param><fmt:message key="generic.subscriptionLevels.${loggedUser.subscriptionLevel}"/></fmt:param>
        </fmt:message>
      </span>
    </div>
    
    <c:if test="${loggedUser.subscriptionLevel ne 'BASIC' and loggedUser.subscriptionEnds ne null}">
      <div class="currentPlanRow">
        <label><fmt:message key="changeplan.block.currentSubscriptionEndsTitle"/></label>
        <span>
          <fmt:message key="changeplan.block.currentEnds">
            <fmt:param value="${loggedUser.subscriptionEnds}"/>
          </fmt:message>
        </span>
      </div>
    </c:if>
  </div>
  
  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="changeplan.block.changeTitle" name="titleLocale" />
  </jsp:include>

  <div id="changePlanGenericBlockContent" class="blockContent">
    <form>
      <c:forEach var="plan" items="${plans}" varStatus="vs">
        <div class="formFieldContainer formSelectContainer planFieldContainer">
          <span class="planPrice"><fmt:formatNumber value="${plan.price}" currencyCode="${plan.currency}" type="currency" /></span>
        
          <c:choose>
            <c:when test="${planDaysRemaining gt 0 and planLevels[plan.id] eq 'LOWER'}">
              <input type="radio" id="plan-${plan.id}" name="planId" disabled="disabled" title="<fmt:message key="changeplan.block.lowerLevelHint"/>"/>
            </c:when>
            <c:when test="${vs.last}">
              <input type="radio" id="plan-${plan.id}" name="planId" value="${plan.id}" checked="checked"/>
            </c:when>
            <c:otherwise>
              <input type="radio" id="plan-${plan.id}" name="planId" value="${plan.id}"/>
            </c:otherwise>
          </c:choose>
          
          <label class="formFieldLabel" for="plan-${plan.id}">${planNames[plan.id]}</label>
          <p>${planDescriptions[plan.id]}</p>
          
          <c:choose>
            <c:when test="${planDaysRemaining gt 0 and planLevels[plan.id] eq 'LOWER'}">
              <span class="planHint"><fmt:message key="changeplan.block.lowerLevelHint"/></span>  
            </c:when>
            <c:when test="${planDaysRemaining gt 0 and planLevels[plan.id] eq 'EQUAL'}">
              <span class="planHint">
                <fmt:message key="changeplan.block.sameLevelHint">
                  <fmt:param value="${planDaysRemaining}"/>
                </fmt:message>
              </span>  
            </c:when>
            <c:when test="${planDaysRemaining gt 0 and planCompensiotions[plan.id] ne null and planLevels[plan.id] eq 'HIGHER'}">
              <span class="planHint">
                <fmt:message key="changeplan.block.higherLevelHint">
                  <fmt:param value="${planCompensiotions[plan.id]}"/>
                </fmt:message>
              </span>  
            </c:when>
          </c:choose>
          
        </div>
      </c:forEach>
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="changeplan.block.changeButton" />
        <jsp:param name="classes" value="formvalid" />
        <jsp:param name="name" value="change" />
      </jsp:include>
    </form>
  </div>

</div>