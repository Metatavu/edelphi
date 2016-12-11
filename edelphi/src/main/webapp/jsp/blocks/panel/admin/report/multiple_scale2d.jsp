<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="queryReportPage" value="${queryReportPages[param.pageNumber]}" />

<div class="block">
  <div class="blockContent">

    <!-- Title -->

    <h2>${queryReportPage.pageTitle}</h2>

    <!-- Thesis -->

    <!-- Chart(s) -->
    
    <c:forEach var="multiple2dscalesThesis" items="${multiple2dscalesTheses}" varStatus="vsThesis">
      <jsp:include page="/jsp/fragments/query_report_page_thesis.jsp">
        <jsp:param name="thesis" value="${multiple2dscalesThesis}" />
      </jsp:include>

      <c:choose>
        <c:when test="${!empty reportContext.parameters['show2dAs1d']}">
          <!-- two 1D charts -->
          <ed:queryPageChart reportContext="${reportContext}" output="PNG" width="740" height="450" queryPageId="${queryReportPage.queryPageId}">
            <ed:param name="render2dAxis" value="x" />
            <ed:param name="dynamicSize" value="true" />
            <ed:param name="chartIndex" value="${vsThesis.index}" />
          </ed:queryPageChart>
          <ed:queryPageChart reportContext="${reportContext}" output="PNG" width="740" height="450" queryPageId="${queryReportPage.queryPageId}">
            <ed:param name="render2dAxis" value="y" />
            <ed:param name="dynamicSize" value="true" />
            <ed:param name="chartIndex" value="${vsThesis.index}" />
          </ed:queryPageChart>
        </c:when>
        <c:otherwise>
          <!-- 2D chart -->
          <ed:queryPageChart reportContext="${reportContext}" output="PNG" width="740" height="450" queryPageId="${queryReportPage.queryPageId}">
            <ed:param name="render2dAxis" value="both" />
            <ed:param name="dynamicSize" value="true" />
            <ed:param name="chartIndex" value="${vsThesis.index}" />
          </ed:queryPageChart>
        </c:otherwise>
      </c:choose>
    </c:forEach>

    <!--  TODO statistics -->

    <!-- Comments -->

    <jsp:include page="/jsp/blocks/panel/admin/report/querycomments.jsp">
      <jsp:param name="pageNumber" value="${param.pageNumber}"/>
    </jsp:include>

  </div>

</div>