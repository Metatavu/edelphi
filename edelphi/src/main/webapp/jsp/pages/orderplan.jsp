<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title><fmt:message key="orderplan.title" /></title>
  <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
  <link href="${pageContext.request.contextPath}/_themes/${theme}/css/orderplan.css" rel="stylesheet"/>
  <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/orderplanblockcontroller.js"></script>
</head>

<body class="environment changePlan">

  <jsp:include page="/jsp/templates/index_header.jsp">
    <jsp:param value="index" name="activeTrail" />
  </jsp:include>

  <div class="GUI_pageWrapper">

    <div class="GUI_pageContainer">
      <div id="GUI_indexChangePlanPanel" class="pagePanel">
        <jsp:include page="/jsp/blocks/orderplan.jsp"></jsp:include>
      </div>
    </div>
    
  </div>

  <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>

</body>
</html>