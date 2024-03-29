<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="blockContextMenu">
  <c:forEach var="item" items="${param.items}">
    <c:set var="tooltipLocale" value="item.${item}.tooltipLocale"/>
    
    <c:set var="tooltip">
      <fmt:message key="${param[tooltipLocale]}"/>
    </c:set>
    
    <c:set var="href" value="item.${item}.href"/>
    <c:set var="id" value="item.${item}.id"/>
    <c:set var="icon" value="item.${item}.icon"/>
    <div class="blockContextMenuItem ${item}">
    <a id="${param[id]}" href="${param[href]}" title="${tooltip}">
      <c:if test="${!empty(param[icon])}">
        <i aria-hidden="true" class="${param[icon]} icon"></i>
      </c:if>
      <span class="blockContextMenuTooltip">
        <span class="blockContextMenuTooltipText">${tooltip}</span>
        <span class="blockContextMenuTooltipArrow"></span>
      </span>
    </a>
  </div>
  </c:forEach> 
</div>
