<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${dataGridSupportIncluded != true}">
    <script type="text/javascript" src="//cdn.metatavu.io/assets/edelphi/_scripts/pdgrid/pdgrid-uncompressed.js"></script>
    <c:set scope="request" var="dataGridSupportIncluded" value="true"/>
  </c:when>
</c:choose>