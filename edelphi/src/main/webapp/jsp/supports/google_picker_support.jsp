<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${googlePickerSupportIncluded != true}">
    <script type="text/javascript" src="https://apis.google.com/js/api.js"></script>
    <script>
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
          client_id: "${googlePickerClientId}",
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
            .setDeveloperKey("${googlePickerApiKey}")
            .addView(view)
            .setCallback(pickerCallback)
            .setAppId("${googlePickerAppId}")
            .build();
          picker.setVisible(true);
        });
      }

      function pickerCallback(data) {
        if (data.action === google.picker.Action.PICKED) {
          const panelId = "${panelId}";
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
        } else if (data.action == google.picker.Action.CANCEL) {
          const url = new URL(window.location.href);
          url.searchParams.delete("importFromGoogle");
          window.location.href = url.toString();
        }
      }
    </script>

    
    <c:set scope="request" var="googlePickerSupportIncluded" value="true"/>
  </c:when>
</c:choose>