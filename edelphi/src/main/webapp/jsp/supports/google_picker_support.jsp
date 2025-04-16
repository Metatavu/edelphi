<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${googlePickerSupportIncluded != true}">
    <script type="text/javascript">
      const __GOOGLE_PICKER_API_KEY = "${googlePickerApiKey}";
    </script>
    
    <c:set scope="request" var="googlePickerSupportIncluded" value="true"/>
  </c:when>
</c:choose>