<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<head>
  <jsp:include page="/jsp/supports/google_picker_support.jsp"></jsp:include>

  <script type="text/javascript" src="https://apis.google.com/js/api.js"></script>
  <script>
    const CLIENT_ID = '748827317096-n6u0r7l3v2ggf08vbehefjifklgnb5au.apps.googleusercontent.com';
    const SCOPE = 'https://www.googleapis.com/auth/drive.file';

    let oauthToken = null;
    const params = new URLSearchParams(window.location.search);

    if (params.has("importFromGoogle")) {
      onApiLoad();
    }

    function onApiLoad() {
      gapi.load('client:auth2', initAuth);
    }

    function initAuth() {
      gapi.auth2.init({
        client_id: CLIENT_ID,
        scope: SCOPE
      }).then(() => {
        gapi.auth2.getAuthInstance().signIn().then(user => {
          oauthToken = user.getAuthResponse().access_token;
          loadPicker();
        });
      });
    }

    function loadPicker() {
      gapi.load('picker', () => {
        const view = new google.picker.View(google.picker.ViewId.DOCS);
        const picker = new google.picker.PickerBuilder()
          .setOAuthToken(oauthToken)
          .setDeveloperKey(__GOOGLE_PICKER_API_KEY)
          .addView(view)
          .setCallback(pickerCallback)
          .setAppId(748827317096)
          .build();
        picker.setVisible(true);
      });
    }

    function pickerCallback(data) {
      if (data.action === google.picker.Action.PICKED) {
        const panelId = document.getElementsByName("panelId")[0].value;
        const fileId = data.docs[0].id;
        console.log(fileId);
        document.getElementsByName("selectedgdoc")[0].value = fileId;
        startLoadingOperation("panelAdmin.block.importMaterialsGDocs.importingMaterials");
        JSONUtils.sendForm(document.forms["importGDocs"], {
          onComplete: function (transport) {
            endLoadingOperation();
          },
          onSuccess: function () {
            window.location.href = CONTEXTPATH + '/panel/admin/managematerials.page?panelId=' + panelId;
          }
        });
      }
    }
  </script>
</head>

<form name="importGDocs" action="${pageContext.request.contextPath}/resources/importgdocs.json">
  <input type="hidden" name="selectedgdoc"/>
  <input type="hidden" name="parentFolderId" value="${panel.rootFolder.id}"/>
  <input type="hidden" name="panelId" value="${panel.id}"/>
</form>

<div class="block materialsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.dashboard.panelMaterialsTitle" name="titleLocale"/>
  </jsp:include>

  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
    <jsp:param name="items" value="CREATE,GDOCSIMPORT"/>
    <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.dashboard.materialCreateLocalDocument"/>
    <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createlocaldocument.page?panelId=${panel.id}"/>
    <jsp:param name="item.GDOCSIMPORT.tooltipLocale" value="panel.admin.dashboard.materialImportGoogleDocuments"/>
    <jsp:param name="item.GDOCSIMPORT.href" value="?panelId=${panel.id}&importFromGoogle=importFromGoogle"/>
  </jsp:include>

  <div class="blockContent materialsBlockList">

    <c:choose>
      <c:when test="${panelActions['MANAGE_PANEL_MATERIALS']}">
        <c:set var="editAccess" value="true"/>
      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false"/>
      </c:otherwise>
    </c:choose>

    <c:forEach var="material" items="${materials}">
      <c:choose>
        <c:when test="${material.type eq 'LOCAL_DOCUMENT'}">
          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
            <jsp:param name="resourceId" value="${material.id}" />
            <jsp:param name="resourceName" value="${material.name}" />
            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
            <jsp:param name="resourceCreated" value="${material.created.time}" />
            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
            <jsp:param name="resourceVisible" value="${material.visible}" />
            <jsp:param name="mayEdit" value="${editAccess}" />
            <jsp:param name="mayDelete" value="${editAccess}" />
            <jsp:param name="mayHide" value="${editAccess}" />
            <jsp:param name="editLocale" value="block.materialListing.editLocalDocument" />
            <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalDocument" />
            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
            <jsp:param name="editLink" value="${pageContext.request.contextPath}/panel/admin/editlocaldocument.page?panelId=${panel.id}&localDocumentId=${material.id}" />
            <jsp:param name="selected" value="${param.localDocumentId eq material.id}" />
          </jsp:include>
        </c:when>

        <c:when test="${material.type eq 'GOOGLE_DOCUMENT'}">
          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
            <jsp:param name="resourceId" value="${material.id}" />
            <jsp:param name="resourceName" value="${material.name}" />
            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
            <jsp:param name="resourceCreated" value="${material.created.time}" />
            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
            <jsp:param name="resourceVisible" value="${material.visible}" />
            <jsp:param name="mayDelete" value="${editAccess}" />
            <jsp:param name="mayHide" value="${editAccess}" />
            <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleDocument" />
            <jsp:param name="hideLocale" value="block.materialListing.hideGoogleDocument" />
            <jsp:param name="showLocale" value="block.materialListing.showGoogleDocument"/>
          </jsp:include>
        </c:when>

        <c:when test="${material.type eq 'LOCAL_IMAGE'}">
          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
            <jsp:param name="resourceId" value="${material.id}" />
            <jsp:param name="resourceName" value="${material.name}" />
            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
            <jsp:param name="resourceCreated" value="${material.created.time}" />
            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
            <jsp:param name="resourceVisible" value="${material.visible}" />
            <jsp:param name="mayEdit" value="${editAccess}" />
            <jsp:param name="mayDelete" value="${editAccess}" />
            <jsp:param name="mayHide" value="${editAccess}" />
            <jsp:param name="editLocale" value="block.materialListing.editLocalImage" />
            <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalImage" />
            <jsp:param name="hideLocale" value="block.materialListing.hideLocalImage" />
            <jsp:param name="showLocale" value="block.materialListing.showLocalImage"/>
            <jsp:param name="editLink" value="${pageContext.request.contextPath}/panel/admin/editlocalimage.page?panelId=${panel.id}&resourceId=${material.id}" />
          </jsp:include>
        </c:when>

        <c:when test="${material.type eq 'LINKED_IMAGE'}">
          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
            <jsp:param name="resourceId" value="${material.id}" />
            <jsp:param name="resourceName" value="${material.name}" />
            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
            <jsp:param name="resourceCreated" value="${material.created.time}" />
            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
            <jsp:param name="resourceVisible" value="${material.visible}" />
            <jsp:param name="mayDelete" value="${editAccess}" />
            <jsp:param name="mayHide" value="${editAccess}" />
            <jsp:param name="deleteLocale" value="block.materialListing.deleteLinkedImage" />
            <jsp:param name="hideLocale" value="block.materialListing.hideLinkedImage" />
            <jsp:param name="showLocale" value="block.materialListing.showLinkedImage"/>
          </jsp:include>
        </c:when>

        <c:when test="${material.type eq 'GOOGLE_IMAGE'}">
          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
            <jsp:param name="resourceId" value="${material.id}" />
            <jsp:param name="resourceName" value="${material.name}" />
            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
            <jsp:param name="resourceCreated" value="${material.created.time}" />
            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
            <jsp:param name="resourceVisible" value="${material.visible}" />
            <jsp:param name="mayDelete" value="${editAccess}" />
            <jsp:param name="mayHide" value="${editAccess}" />
            <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleImage" />
            <jsp:param name="hideLocale" value="block.materialListing.hideGoogleImage" />
            <jsp:param name="showLocale" value="block.materialListing.showGoogleImage"/>
          </jsp:include>
        </c:when>
      </c:choose>
    </c:forEach>
  </div>

  <c:if test="${param.showAllLink eq 'true'}">
    <div class="adminDashboardMaterialsShowAllLink">
      <a href="${pageContext.request.contextPath}/panel/admin/managematerials.page?panelId=${panel.id}">
        <fmt:message key="panel.admin.dashboard.materialsShowAll">
          <fmt:param>${materialCount}</fmt:param>
        </fmt:message>
      </a>
    </div>
  </c:if>

</div>
