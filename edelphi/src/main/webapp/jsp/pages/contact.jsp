<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="contact.pageTitle" /> </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>

    <script type="text/javascript">
      function decodeEmail() {
        var email = atob('${email}');
        document.getElementById('email').innerHTML = email;
        document.getElementById('email').setAttribute("href", "mailto:" + email);
      }
    </script>
  </head>
  <body class="environment index" onload="decodeEmail()">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="contact" name="activeTrail"/>
    </jsp:include>
    
    <div class="GUI_pageWrapper">

      <div class="GUI_pageContainer">    
    
	      <div class="block">

            <jsp:include page="/jsp/fragments/block_title.jsp">
              <jsp:param value="contact.block.blockTitle" name="titleLocale"/>
            </jsp:include>

            <div class="blockContent" style="margin-top: 24px; margin-bottom: 24px">
              <p>
                <fmt:message key="contact.block.text1"/>
              </p>
              <p>
                <fmt:message key="contact.block.text2"/>&nbsp;<a id="email"></a><fmt:message key="contact.block.text3"/>
              </p>
            </div>

          </div>
	    
	    </div>
	    
    </div>
    
    <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>
    
  </body>

</html>