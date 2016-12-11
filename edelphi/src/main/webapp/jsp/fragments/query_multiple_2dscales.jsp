<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryMultiple2DScalesQuestionContainer">
  <table>
    
    <tr>
      <th></th>
      <th colspan="${param.optionCount}">${param.labelX}</th>
      <th class="scale-separator"></th>
      <th colspan="${param.optionCount}">${param.labelY}</th>
    </tr>
    
    <tr>
      <th></th>
      
      <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="vs">
        <c:set var="option" value="option.${vs.index}"></c:set>
        <th>${param[option]}</th>
      </c:forEach>
      
      <th class="scale-separator"></th>

      <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="vs">
        <c:set var="option" value="option.${vs.index}"></c:set>
        <th>${param[option]}</th>
      </c:forEach>
    </tr>
    
    <c:forEach begin="0" end="${param.thesisCount - 1}" varStatus="vs">
      <c:set var="thesis" value="thesis.${vs.index}"></c:set>
      <c:set var="selectedX" value="selectedX.${vs.index}"></c:set>
      <c:set var="selectedY" value="selectedY.${vs.index}"></c:set>
      <tr>
        <td>${param[thesis]}</td>
        
        <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="svs">
          <td>
            <c:choose>
              <c:when test="${svs.index eq param[selectedX]}">
                <input type="radio" name="multiple2dscales.${vs.index}.x" value="${svs.index}" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="radio" name="multiple2dscales.${vs.index}.x" value="${svs.index}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>
        
        <td class="scale-separator"></td>

        <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="svs">
          <td>
            <c:choose>
              <c:when test="${svs.index eq param[selectedY]}">
                <input type="radio" name="multiple2dscales.${vs.index}.y" value="${svs.index}" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="radio" name="multiple2dscales.${vs.index}.y" value="${svs.index}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </c:forEach>

      </tr>
  </c:forEach>
    
  </table>
  
</div>