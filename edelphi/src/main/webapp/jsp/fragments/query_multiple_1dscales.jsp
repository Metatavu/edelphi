<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryMultiple1DScalesQuestionContainer">
  <table data-option-count="${param.optionCount}" data-thesis-count="${param.thesisCount}">
    
    <tr>
      <th></th>
      <th class="label" colspan="${param.optionCount}">${param.label}</th>
    </tr>
    
    <tr>
      <th></th>
      
      <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="vs">
        <c:set var="option" value="option.${vs.index}"></c:set>
        <th>${param[option]}</th>
      </c:forEach>
    </tr>
    
    <c:forEach begin="0" end="${param.thesisCount - 1}" varStatus="vs">
      <c:set var="thesis" value="thesis.${vs.index}"></c:set>
      <c:set var="selected" value="selected.${vs.index}"></c:set>
      <tr>
        <td>${param[thesis]}</td>
        
        <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="svs">
          <td>
            <label class="cover-label" for="m1ds-${vs.index}-${svs.index}"></label>
            <c:choose>
              <c:when test="${svs.index eq param[selected]}">
                <input type="radio" name="multiple1dscales.${vs.index}" value="${svs.index}" checked="checked" id="m1ds-${vs.index}-${svs.index}"/>
              </c:when>
              <c:otherwise>
                <input type="radio" name="multiple1dscales.${vs.index}" value="${svs.index}" id="m1ds-${vs.index}-${svs.index}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>

      </tr>
    </c:forEach>

  </table>
</div>