<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="orderplan.block.summary" name="titleLocale" />
  </jsp:include>

  <div id="orderPlanSummaryBlockContent" class="blockContent">
  
  <div class="summaryRow">
      <label><fmt:message key="orderplan.block.planDescriptionTitle"/></label>
      <span class="planDescription">
        <fmt:message key="orderplan.block.planDescription">
          <fmt:param value="${newPlan.days}"/>
          <fmt:param><fmt:message key="generic.subscriptionLevels.${newPlan.subscriptionLevel}"/></fmt:param>
          <fmt:param value="${subscriptionEnds}"/>
        </fmt:message>
      </span>   
    </div>

    <div class="summaryRow">
      <label><fmt:message key="orderplan.block.planPriceTitle"/></label>
      <span class="planPrice"><fmt:formatNumber value="${newPlan.price}" currencyCode="${newPlan.currency}" type="currency" /></span>   
    </div>
    
    <c:if test="${compensation ne null}">
      <div class="summaryRow">
        <label><fmt:message key="orderplan.block.compensationTitle"/></label>
        <span class="compensation"><fmt:formatNumber value="${compensation}" currencyCode="${oldPlan.currency}" type="currency" /></span>   
      </div>
    </c:if>
    
    <div class="summaryRow">
      <label><fmt:message key="orderplan.block.totalPrice"/></label>
      <span class="total"><fmt:formatNumber value="${totalPrice}" currencyCode="${newPlan.currency}" type="currency" /></span>   
    </div>
  </div>

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="orderplan.block.paymentDetails" name="titleLocale" />
  </jsp:include>

  <div id="orderPlanDetailsBlockContent" class="blockContent">
    <form method="post">
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="company" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsCompanyLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="firstName" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsFirstNameLabel" />
        <jsp:param name="value" value="${loggedUser.firstName}" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="lastName" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsLastNameLabel" />
        <jsp:param name="value" value="${loggedUser.lastName}" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="email" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsEmailLabel" />
        <jsp:param name="value" value="${loggedUser.defaultEmail.address}" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="mobileNumber" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsMobileNumberLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="telephoneNumber" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsTelephoneNumberLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="streetAddress" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsStreetAddressLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="postalCode" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsPostalCodeLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="postalOffice" />
        <jsp:param name="classes" value="required" />
        <jsp:param name="labelLocale" value="orderplan.block.paymentDetailsPostalOfficeLabel" />
        <jsp:param name="value" value="" />
      </jsp:include>
      
      <ed:include page="/jsp/fragments/formfield_select.jsp">
        <ed:param name="labelLocale" value="orderplan.block.paymentDetailsCountryLabel" />
        <ed:param name="name" value="country" />
        <ed:param name="classes" value="required" />
        <ed:param name="options" value="${countryCodes}" />
        <ed:param name="value" value="FI" />
        <c:forEach var="country" items="${countries}">
          <ed:param name="option.${country.name()}" value="${country.getName()}"/>
        </c:forEach>
      </ed:include>
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="orderplan.block.order" />
        <jsp:param name="classes" value="formvalid" />
        <jsp:param name="name" value="order" />
      </jsp:include>
      
      <input type="hidden" name="planId" value="${newPlan.id}"/>
    </form>
  </div>

</div>
