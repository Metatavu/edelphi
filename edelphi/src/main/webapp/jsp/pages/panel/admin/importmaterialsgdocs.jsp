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
    <script type="text/javascript" src="https://apis.google.com/js/api.js"></script>
    <script>
      const CLIENT_ID = '748827317096-n6u0r7l3v2ggf08vbehefjifklgnb5au.apps.googleusercontent.com';
      const SCOPE = 'https://www.googleapis.com/auth/drive.file';

      let oauthToken = null;

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
