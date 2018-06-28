<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="adminUserSubscriptionLevelEditorBlock" class="block">

  <div id="adminUserSubscriptionLevelEditorBlockContent" class="blockContent">
  
    <div class="blockTitle"><h2><fmt:message key="admin.manageUserSubscriptionLevels.editTitle" /></h2></div>

    <div id="adminUserSubscriptionLevelEditorFormContainer">
    
      <form id="adminUserSubscriptionLevelEditorForm">
        <input type="hidden" name="user-id" value="${user.id}"/>
        
        <p>
          ${user.fullName} &lt;${user.defaultEmailAsString} &gt;
        </p>
        
        <div class="formFieldContainer formSelectContainer">
          <label class="formFieldLabel" for="plan-select">
            <fmt:message key="admin.manageUserSubscriptionLevels.plan"/>
          </label>
          
          <select id="plan-select" name="plan">
            <option value="CURRENT" data-plan-id="${user.plan != null ? plan.id : ''}" data-started="${user.subscriptionStarted != null ? user.subscriptionStarted.time : ''}" data-ends="${user.subscriptionEnds != null ? user.subscriptionEnds.time : ''}">
              <fmt:message key="admin.manageUserSubscriptionLevels.currentPlan"/>
            </option>
            <option  value="BASIC"><fmt:message key="generic.subscriptionLevels.BASIC"/></option>
            <c:forEach var="plan" items="${plans}">
              <option value="${plan.id}" data-type="PLAN" data-days="${plan.days}">${planNames[plan.id]}</option>
            </c:forEach>
          </select>
        </div>
        
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="labelLocale" value="admin.manageUserSubscriptionLevels.subscriptionStarted" />
          <jsp:param name="name" value="subscription-started" />
          <jsp:param name="value" value="${user.subscriptionStarted != null ? user.subscriptionStarted.time : ''}" />
        </jsp:include>
        
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="labelLocale" value="admin.manageUserSubscriptionLevels.subscriptionEnds" />
          <jsp:param name="name" value="subscription-ends" />
          <jsp:param name="value" value="${user.subscriptionEnds != null ? user.subscriptionEnds.time : ''}" />
        </jsp:include>
        
        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="labelLocale" value="admin.manageUserSubscriptionLevels.save" />
          <jsp:param name="classes" value="formvalid" />
          <jsp:param name="name" value="save" />
        </jsp:include>
      
      </form>
    </div>
  </div>
</div>