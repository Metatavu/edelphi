<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block manageUsersBlock" id="adminManageUsersBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.userSearch.title" name="titleLocale"/>
  </jsp:include>
  
  <div class="blockContent panelAdminUsersEditorBlock">
    <jsp:include page="/jsp/fragments/formfield_text.jsp">
      <jsp:param name="labelLocale" value="admin.userSearch.searchByEmail" />
      <jsp:param name="name" value="email" />
    </jsp:include>
    
    <div id="user-search-results"></div>
  </div>
  
</div>