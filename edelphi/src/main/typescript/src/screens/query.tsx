import * as React from "react";
import * as ReactDOM from "react-dom";
import AccessTokenRefresh from "../components/access-token-refresh";
import QueryCommentEditor from "../components/query-comment-editor";
import QueryCommentList from "../components/query-comment-list";
import { createStore } from 'redux';
import { StoreState } from "../types";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import { Provider } from "react-redux";

const getAttribute = (element: Element, attributeName: string): string | null => {
  if (!element) {
    return null;
  }
  
  const attribute = element.attributes.getNamedItem(attributeName);
  if (!attribute) {
    return null;
  }

  return attribute.value; 
}

const getIntAttribute = (element: Element, attributeName: string): number | null => {
  const value = getAttribute(element, attributeName);
  return value ? parseInt(value) : null; 
}

const getBoolAttribute = (element: Element, attributeName: string): boolean => {
  const value = getAttribute(element, attributeName);
  return value === "true"; 
}

window.addEventListener('load', () => {
  const queryComments = document.getElementById("query-comments");
  const initalStoreState: StoreState = { };
  
  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);
  
  if (queryComments) {
    const queryId: number | null = getIntAttribute(queryComments, "data-query-id");
    const queryReplyId: number | null = getIntAttribute(queryComments, "data-query-reply-id");
    const commentable: boolean = getBoolAttribute(queryComments, "data-commentable");
    const viewDiscussion: boolean  = getBoolAttribute(queryComments, "data-view-discussion");

    if (queryId) {
      const component = 
        <Provider store={store}>
          <AccessTokenRefresh/>
          { commentable ? <QueryCommentEditor queryReplyId={queryReplyId}/> : null }
          { viewDiscussion ? <QueryCommentList queryId={queryId}/> : null }
        </Provider>;

      ReactDOM.render(component, queryComments);
    }
  }

});