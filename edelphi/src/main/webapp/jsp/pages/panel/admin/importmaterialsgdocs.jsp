<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.manageMaterials.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/google_picker_support.jsp"></jsp:include>

  </head>

  <body class="panel_admin index">
    <c:set var="pageBreadcrumbTitle">
      <fmt:message key="breadcrumb.panelAdmin.importMaterialsGDocs"/>
    </c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/importmaterialsgdocs.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">
      <div class="GUI_pageContainer">

        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param name="titleLocale" value="panel.admin.manageMaterials.pageTitle" />
          <jsp:param name="titleLocaleParam" value="${panel.name}" />
        </jsp:include>

        <div class="GUI_adminManageMaterialsNarrowColumn">
          <jsp:include page="/jsp/blocks/panel_admin/material_listing.jsp"></jsp:include>
        </div>

        <div class="GUI_adminManageMaterialsWideColumn">
          <div class="block">
            <jsp:include page="/jsp/fragments/block_title.jsp">
              <jsp:param value="panelAdmin.block.materialImportGDocs.title" name="titleLocale"/>
            </jsp:include>
            <br/>
            <button onclick="onApiLoad()">Pick File</button>
            <form name="importGDocs" action="${pageContext.request.contextPath}/resources/importgdocs.json">
              <input type="hidden" name="selectedgdoc"/>
              <input type="hidden" name="parentFolderId" value="${panel.rootFolder.id}"/>
              <input type="hidden" name="panelId" value="${panel.id}"/>
            </form>
          </div>
        </div>

      </div>
    </div>
  </body>
</html>
