import * as React from "react";
import * as ReactDOM from "react-dom";
import AccessTokenRefresh from "../components/access-token-refresh";
import QueryCommentEditor from "../components/query-comment-editor";
import QueryCommentList from "../components/query-comment-list";
import MqttConnector from "../components/mqtt-connector";
import { createStore } from 'redux';
import { StoreState } from "../types";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import { Provider } from "react-redux";
import Api from "edelphi-client";
import strings from "../localization/strings";

Api.configure("http://dev.edelphi.org:8080/api/v1");
declare function getLocale() : any;

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
  const locale: string = getLocale().getLanguage();
  strings.setLanguage(locale);

  const queryComments = document.getElementById("query-comments");
  const initalStoreState: StoreState = { 
    locale: locale
  };
  
  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);
  
  if (queryComments) {
    const panelId: number | null = getIntAttribute(queryComments, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryComments, "data-query-id");
    const pageId: number | null = getIntAttribute(queryComments, "data-page-id");
    const queryReplyId: number | null = getIntAttribute(queryComments, "data-query-reply-id");
    const commentable: boolean = getBoolAttribute(queryComments, "data-commentable");
    const viewDiscussion: boolean  = getBoolAttribute(queryComments, "data-view-discussion");

    if (panelId && queryId && pageId) {
      const component = 
        <Provider store={store}>
          <AccessTokenRefresh/>
          <MqttConnector>
            { commentable ? <QueryCommentEditor queryReplyId={queryReplyId}/> : null }
            { viewDiscussion ? <QueryCommentList panelId={panelId} queryId={queryId} pageId={pageId}/> : null }
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryComments);
    }
  }

});