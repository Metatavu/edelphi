<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexNewsBlockContent" class="block">

  <!--
  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="index.block.newsBlockTitle" name="titleLocale"/>
  </jsp:include>
	-->
	<div id="newsBlockContent" class="blockContent">
    <p>
      <c:if test="${not ignoreBulletinLocale}">
	      <fmt:message key="index.block.bulletinSelectedLanguage">
	       <fmt:param>
	         <c:if test="${selectedLanguage == 'en'}">
	           English
           </c:if>
           <c:if test="${selectedLanguage == 'fi'}">
             suomi
           </c:if>
	       </fmt:param> 
	      </fmt:message>  
	      <a href="/?ignoreBulletinLocale=true"><fmt:message key="index.block.bulletinSelectAllLanguages"/></a>
      </c:if>
      <c:if test="${ignoreBulletinLocale}">
        <fmt:message key="index.block.bulletinAllLanguagesSelected"/>
        <a href="/"><fmt:message key="index.block.bulletinUseCurrentLocale"/></a>
      </c:if>
	  </p>
	  
    <c:forEach var="bulletin" items="${bulletins}" varStatus="vs">
      <div class="newsEntryContainer">
        <h2 class="newsEntryTitle">${bulletin.title}</h2>
        <div class="genericMeta"><fmt:formatDate value="${bulletin.created}" dateStyle="long"/></div>
        <div class="newsEntryContent">${bulletin.summary}</div>
        <div class="newsEntryReadMore"><a href="${pageContext.request.contextPath}/viewbulletin.page?bulletinId=${bulletin.id}"><fmt:message key="index.block.bulletinReadMore"/></a></div>
      </div>
      
      <c:if test="${not vs.last}">
        <div class="newsEntrySpacer"></div>
      </c:if>
    </c:forEach>

	</div>

</div>