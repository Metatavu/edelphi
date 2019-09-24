<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryBlock" id="panelQueryBlock" style="max-width: calc(100vw - 20px)">
  <form action="${pageContext.request.contextPath}/queries/saveanswers.json">
    <c:set var="titleText" scope="page">
      <fmt:message key="panel.block.query.title">
        <fmt:param>${queryPage.querySection.query.name}</fmt:param>
      </fmt:message>
    </c:set>
    
    <jsp:include page="/jsp/fragments/block_title.jsp">
      <jsp:param name="titleText" value="${titleText}"/>
    </jsp:include>
    
    <div id="panelQueryBlockContent" class="blockContent">
      <h2 class="queryPageTitle">${queryPage.title}</h2>

      <c:forEach var="requiredQueryFragment" items="${requiredQueryFragments}">
        <ed:include page="/jsp/fragments/query_${requiredQueryFragment.name}.jsp">
          <c:forEach items="${requiredQueryFragment.attributes}" var="attribute">
            <ed:param name="${attribute.key}" value="${attribute.value}"/>
          </c:forEach>
        </ed:include>
      </c:forEach>
    </div>
    
    <input type="hidden" name="panelPath" value="${panel.fullPath}"/>
    <input type="hidden" name="queryPageId" value="${queryPage.id}"/>
    <input type="hidden" name="queryReplyId" value="${queryReply.id}"/>
    <input type="hidden" name="queryPageType" value="${queryPage.pageType}"/>
    <input type="hidden" name="queryNextPageNumber" value="${queryNextPageNumber}"/>
    <input type="hidden" name="queryPreviousPageNumber" value="${queryPreviousPageNumber}"/>
    
    <div class="clearBoth"></div>

    <div id="query-navigation" 
      data-panel-id="${panel.id}"
      data-query-id="${queryPage.querySection.query.id}"
      data-page-id="${queryPage.id}"
      data-query-state="${queryPage.querySection.query.state}"></div>
    
  </form>
</div>