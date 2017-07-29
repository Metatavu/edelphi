<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexRegisterBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="index.block.registerBlockTitle" name="titleLocale"/>
  </jsp:include>
  
  <p>
    <fmt:message key="index.block.registerText"/>
    <a href="${registerUrl}" class="register-link">
      <fmt:message key="index.block.registerHere"/>
    </a>
  </p>

</div>