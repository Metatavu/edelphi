<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block settingsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.dashboard.miscMaterialsTitle" name="titleLocale"/>
  </jsp:include>
  
  <div class="panelAdminGenericRow">
    <div class="panelAdminGenericTitle"><a href="/admin/editindexdocument.page"><fmt:message key="admin.dashboard.editIndexPageDocumentAction" /></a></div>
    <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.editIndexPageDocumentDescription" /></div>
  </div>
  
</div>