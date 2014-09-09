<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${jsonUtilsSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/jsonutils/jsonutils.js"></script>
    <c:set scope="request" var="jsonUtilsSupportIncluded" value="true"/>
  </c:when>
</c:choose>