<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">
  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.block.materialImportGDocs.title" name="titleLocale"/>
  </jsp:include>
  
  <div id="adminMaterialImportGDocsBlockContent" class="blockContent">

    <form name="importGDocs" action="${pageContext.request.contextPath}/resources/importgdocs.json">
  
      <c:forEach var="entry" items="${googleDocuments}">
        <div class="adminMaterialImportGDocsEntry">
          <div class="adminMaterialImportGDocsEntryCheckContainer">
            <input class="adminMaterialImportGDocsEntryCheck" type="checkbox" name="selectedgdoc" value="${entry.resourceId}"/>
          </div>
          <div class="adminMaterialImportGDocsEntryIconContainer">
            <img src="${entry.iconUrl}" class="adminMaterialImportGDocsEntryIcon"/>
          </div>
          <div class="adminMaterialImportGDocsEntryTitleContainer">
            <div class="adminMaterialImportGDocsEntryTitle">${entry.title}</div>
          </div>
        </div>
      </c:forEach>  
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="admin.block.materialImportGDocs.import"/>
        <jsp:param name="classes" value="formvalid"/>
        <jsp:param name="name" value="save"/>
      </jsp:include>
    
      <input type="hidden" name="parentFolderId" value="${param.parentFolderId}"/>
    </form>
     
  </div>  

</div>