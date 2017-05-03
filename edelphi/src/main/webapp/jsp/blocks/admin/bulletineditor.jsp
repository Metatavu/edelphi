<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminBulletinEditorBlock" class="block">

  <div id="panelAdminBulletinEditorBlockContent" class="blockContent">
  
    <div class="blockTitle"><h2><fmt:message key="admin.managePanelBulletins.editTitle" /></h2></div>

    <div id="panelAdminBulletinEditorForm">
    
      <form>
    
        <input type="hidden" name="bulletinId" value="${bulletin.id}"/>
    
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="labelLocale" value="admin.managePanelBulletins.titleLabel" />
          <jsp:param name="name" value="title" />
          <jsp:param name="value" value="${bulletin.title}" />
          <jsp:param name="classes" value="required" />
        </jsp:include>
       
        <jsp:include page="/jsp/fragments/formfield_memo.jsp">
          <jsp:param name="labelLocale" value="admin.managePanelBulletins.messageLabel" />
          <jsp:param name="name" value="message" />
          <jsp:param name="value" value="${bulletin.message}" />
        </jsp:include>
    
        <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
          <jsp:param name="name" value="important" />
          <jsp:param name="labelLocale" value="admin.managePanelBulletins.important" />
          <jsp:param name="checked" value="${bulletin.important}" />
          <jsp:param name="value" value="TRUE" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="labelLocale" value="admin.managePanelBulletins.importantEndsLabel" />
          <jsp:param name="name" value="importantEnds" />
          <jsp:param name="value" value="${bulletin.importantEnds.time}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="labelLocale" value="admin.managePanelBulletins.save" />
          <jsp:param name="classes" value="formvalid" />
          <jsp:param name="name" value="save" />
        </jsp:include>
        
        
      
      </form>
    </div>
  </div>
</div>