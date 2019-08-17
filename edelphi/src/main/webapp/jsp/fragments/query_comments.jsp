<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="query-comments" style="margin-top: 20px"
  data-panel-id="${param['panelId']}"
  data-query-id="${param['queryId']}"
  data-page-id="${param['pageId']}" 
  data-query-reply-id="${param['queryReplyId']}" 
  data-commentable="${param['queryPageCommentable']}" 
  data-view-discussion="${param['queryViewDiscussion']}"></div>