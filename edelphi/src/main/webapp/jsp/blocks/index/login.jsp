<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexLoginBlockContent" class="block">
  
  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param name="titleLocale" value="index.block.loginBlockTitle"/>
    <jsp:param name="helpText" value=""/>
  </jsp:include>
  
  <p>
    <fmt:message key="index.block.loginText"/>
    <a href="/dologin.page?authSource=${authSourceId}" class="login-link">
      <fmt:message key="index.block.loginHere"/>
    </a>
  </p>
  
</div>

