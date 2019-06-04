<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.viewPanel.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message>
    </title>
    <script crossorigin src="https://unpkg.com/react@16/umd/react.development.js"></script>
    <script crossorigin src="https://unpkg.com/react-dom@16/umd/react-dom.development.js"></script>
    <link rel='stylesheet prefetch' href='//cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/components/icon.min.css'></link>

    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/flotr_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/mathutils.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel/queryblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/ping.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/dist/bundle.min.js"></script>
  </head>
  <body class="panel index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
      <jsp:param value="${queryPage.querySection.query.name}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}${queryPage.querySection.query.fullPath}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <div class="pagePanel">
          <jsp:include page="/jsp/blocks/panel/query.jsp"></jsp:include>
        </div>
        
        <div class="clearBoth"></div>
      
      </div>
    
    </div>
  </body>
</html>