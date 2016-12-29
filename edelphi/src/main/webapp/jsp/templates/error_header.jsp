<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

	<div class="GUI_header_outer_wrapper">
	  <div class="GUI_header_inner_wrapper">
		  <div class="GUI_header">
		  
		    <!--  <div class="GUI_header_logo"></div> -->
		    <div class="GUI_header_text">
		      <a href="${pageContext.request.contextPath}/"></a>
		      <span class="GUI_header_text_small"><fmt:message key="generic.header.logoText"/></span>
        </div>
		
        <div class="headerLocaleContainer">
          <c:choose>
            <c:when test="${pageContext.request.locale.language eq 'fi'}">
              <a class="headerLocaleLink localeSelected" href="#" onclick="setLocale('fi_FI');">Suomeksi</a><a class="headerLocaleLink" href="#" onclick="setLocale('en_US');">In English</a>
            </c:when>
            <c:otherwise>
              <a class="headerLocaleLink" href="#" onclick="setLocale('fi_FI');">Suomeksi</a><a class="headerLocaleLink localeSelected" href="#" onclick="setLocale('en_US');">In English</a>
            </c:otherwise>
          </c:choose>
        </div>
		
		  </div>
		  
		  <div class="GUI_header_shadow_wrapper">
	      <div class="GUI_header_shadow">
	     
	      </div>
	    </div>
	    
	    <!-- 
	    <div class="GUI_breadcrumb">
	  
      </div>
      -->
      
    </div>
      
	</div>



