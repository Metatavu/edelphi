<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="profile.block.profileBlockTitle" name="titleLocale" />
  </jsp:include>

  <div id="profileGenericBlockContent" class="blockContent">
    <c:if test="${loggedUserHasPicture}">
      <c:set var="userPictureStyle" value="background-image:url('${pageContext.request.contextPath}/user/picture.binary?userId=${user.id}');" />
    </c:if>

    <div class="profilePictureWrapper">
      <div class="profilePictureTitle">
        <fmt:message key="profile.block.profilePictureBlockTitle" />
      </div>
      <div id="profilePicture" class="profilePicture" style="${userPictureStyle}">
        <div class="changeProfilePictureButton">
          <fmt:message key="profile.block.profilePicture.changeProfilePictureButton" />
        </div>
        <div class="changeProfilePictureModalOverlay" style="display: none"></div>
        <div class="changeProfilePictureModalContainer" style="display: none">
          <iframe id="_uploadFrame" style="display: none" name="_uploadFrame"> </iframe>

          <div class="changeProfilePictureCloseModalButton"></div>
          <div class="changeProfilePictureModalContent">
            <jsp:include page="/jsp/fragments/block_title.jsp">
              <jsp:param value="profile.block.profilePicture.modalTitle" name="titleLocale" />
            </jsp:include>

            <form action="${pageContext.request.contextPath}/profile/updatepicture.json" target="_uploadFrame" method="post" enctype="multipart/form-data">
              <jsp:include page="/jsp/fragments/formfield_file.jsp">
                <jsp:param name="name" value="imageData" />
                <jsp:param name="classes" value="required" />
                <jsp:param name="labelLocale" value="profile.block.profilePicture.pictureFileCaption" />
              </jsp:include>

              <jsp:include page="/jsp/fragments/formfield_submit.jsp">
                <jsp:param name="name" value="updateProfilePictureButton" />
                <jsp:param name="classes" value="formvalid" />
                <jsp:param name="labelLocale" value="profile.block.profilePicture.updatePictureButton" />
              </jsp:include>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div id="profileSettingsForm">
      <form name="profileSettings">
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="firstName" />
          <jsp:param name="classes" value="required" />
          <jsp:param name="labelLocale" value="profile.block.profileFirstNameLabel" />
          <jsp:param name="value" value="${user.firstName}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="lastName" />
          <jsp:param name="classes" value="required" />
          <jsp:param name="labelLocale" value="profile.block.profileLastNameLabel" />
          <jsp:param name="value" value="${user.lastName}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="nickname" />
          <jsp:param name="labelLocale" value="profile.block.profileNicknameLabel" />
          <jsp:param name="value" value="${user.nickname}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="email" />
          <jsp:param name="classes" value="required email" />
          <jsp:param name="labelLocale" value="profile.block.profileEmailLabel" />
          <jsp:param name="value" value="${user.defaultEmail.address}" />
        </jsp:include>

        <input type="hidden" name="emailId" value="${user.defaultEmail.id}" /> <input id="profileUserIdElement" type="hidden" name="userId" value="${user.id}" />

        <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
          <jsp:param name="name" value="commentMail" />
          <jsp:param name="labelLocale" value="profile.block.profileCommentMailLabel" />
          <jsp:param name="checked" value="${userCommentMail}" />
          <jsp:param name="value=" value="1" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="updateProfileButton" />
          <jsp:param name="classes" value="formvalid" />
          <jsp:param name="labelLocale" value="profile.block.updateProfileButtonLabel" />
        </jsp:include>
      </form>
    </div>
  </div>
  
  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="profile.block.profileLoginSettingsTitle" name="titleLocale" />
  </jsp:include>
  
  <div id="profileLoginSettingsBlockContent" class="blockContent">
    <p class="profileLoginSettingsText">
      <span><fmt:message key="profile.block.profileLoginSettingsText"/></span>
      <a class="profileLoginSettingsLink" href="${accountUrl}"><fmt:message key="profile.block.profileLoginSettingsLink"/></a>
    </p>
  </div>
  
  <!-- Subscription level -->

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="profile.block.profileSubscriptionLevelBlockTitle" name="titleLocale" />
  </jsp:include>
  
  <div id="profileSubscriptionLevelBlockContent" class="blockContent">
    <p class="profileSubscriptionLevelText">
      <fmt:message key="profile.block.profileSubscriptionLevelText">
        <fmt:param><fmt:message key="generic.subscriptionLevels.${subscriptionLevelSettings.level}"/></fmt:param>
      </fmt:message>
    </p>
     
    <c:if test="${subscriptionEnds ne null}">
      <p class="profileSubscriptionEnds">
        <fmt:message key="profile.block.profileSubscriptionEnds">
          <fmt:param value="${subscriptionEnds}"/>
        </fmt:message>
      </p>
    </c:if>
    <c:if test="${paymentServicesDisabled ne true}">
        <p>
          <span class="profileChangeSubscriptionText"><fmt:message key="profile.block.profileChangeSubscriptionText"/></span>
          <a class="profileChangeSubscriptionLink" href="/changeplan.page"><fmt:message key="profile.block.profileChangeSubscriptionLink"/></a>
        </p>
    </c:if>
  </div>

  <!-- Invitations -->

  <c:if test="${!empty(myInvitations)}">
    <jsp:include page="/jsp/fragments/block_title.jsp">
      <jsp:param value="profile.block.invitationsBlockTitle" name="titleLocale" />
    </jsp:include>
    
    <div id="profileInvitationBlockContent" class="blockContent">
      <c:forEach var="invitation" items="${myInvitations}">
        <div class="profileInvitationRowWrapper">
          <div>
            <div class="profileInvitationPanelName">
              <c:choose>
                <c:when test="${!empty(invitation.query)}">
                  ${invitation.query.name}
                </c:when>
                <c:otherwise>
                  ${invitation.panel.name}
                </c:otherwise>
              </c:choose>
            </div>
            <div class="profileInvitationDate">
              <fmt:formatDate pattern="d.M.yyyy" value="${invitation.created}"/>
            </div>
            <div class="profileInvitationSender">
              <span class="profileInvitationSenderTitle">Lähettäjä:</span>
              ${invitation.creator.defaultEmailAsString}
            </div>
          </div>
          <div class="contextualLinks">
            <div class="blockContextualLink delete">
              <c:set var="tooltip">
                <fmt:message key="profile.block.invitationDeleteTooltip"/>
              </c:set>
              <a href="#invitationId:${invitation.id}" title="${tooltip}">
                <span class="blockContextualLinkTooltip">
                  <span class="blockContextualLinkTooltipText">${tooltip}</span>
                  <span class="blockContextualLinkTooltipArrow"></span>
                </span>
              </a>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:if>


</div>