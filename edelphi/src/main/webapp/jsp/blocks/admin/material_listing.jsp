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
    <jsp:param value="admin.dashboard.materialsTitle" name="titleLocale"/>
  </jsp:include>
  
  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">

    <jsp:param name="items" value="CREATE,CREATEFOLDER,GDOCSIMPORT"/>
    <jsp:param name="item.CREATEFOLDER.tooltipLocale" value="admin.dashboard.materialCreateFolder"/>
    <jsp:param name="item.CREATEFOLDER.href" value="#parentFolderId:${param.parentFolderId}"/>

    <jsp:param name="item.CREATE.tooltipLocale" value="admin.dashboard.materialCreateLocalDocument"/>
    <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/admin/createlocaldocument.page?cat=materials&lang=${dashboardLang}"/>

    <jsp:param name="item.GDOCSIMPORT.tooltipLocale" value="admin.dashboard.materialImportGoogleDocuments"/>
    <jsp:param name="item.GDOCSIMPORT.href" value="?panelId=${panel.id}&importFromGoogle=importFromGoogle"/>
  </jsp:include>
  
  <div class="blockContent materialsBlockList sortableMaterialList">
    <input type="hidden" name="materialsBlockListParentFolderId" value="${param.parentFolderId}"/>

    <c:forEach var="materialItem" items="${materials}" >
      <c:set var="material" value="${materialItem}" scope="request"></c:set>
      <c:set var="subMaterials" value="${materialTrees}" scope="request"></c:set>
      
      <jsp:include page="/jsp/fragments/material_materialtyperow.jsp">
        <jsp:param value="materials" name="dashboardCategory"/>
        <jsp:param value="MANAGE_DELFOI_MATERIALS" name="editAction"/>
        <jsp:param value="${param.parentFolderId}" name="parentFolderId"/>
        <jsp:param value="/material.page?documentId=${material.id}" name="resourcePath"/>
      </jsp:include>
    </c:forEach>
  </div>
  
  <c:if test="${param.showAllLink eq 'true'}">
    <div class="indexAdminDashboardMaterialsShowAllLink">
      <a href="${pageContext.request.contextPath}/admin/managematerials.page?cat=materials&lang=${dashboardLang}">
        <fmt:message key="admin.dashboard.materialsShowAll">
          <fmt:param>${materialCount}</fmt:param>
        </fmt:message>
      </a>
    </div>
  </c:if>
  
</div>