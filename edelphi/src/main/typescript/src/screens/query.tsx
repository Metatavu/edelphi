import * as React from "react";
import * as ReactDOM from "react-dom";
import AccessTokenRefresh from "../components/access-token-refresh";
import MqttConnector from "../components/mqtt-connector";
import { createStore } from 'redux';
import { StoreState, CommandEvent, PageChangeEvent } from "../types";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import { Provider } from "react-redux";
import strings from "../localization/strings";
import "semantic-ui-less/semantic.less";
import QueryPageLive2d from "../components/panel/query-page-live-2d";
import QueryComments from "../components/panel/query-comments";
import QueryNavigation from "../components/panel/query-navigation";
import getLanguage from "../localization/language";

declare const JSDATA: any;
let initialQueryValidationMessage: string | undefined = undefined;
const locale: string = getLanguage();
strings.setLanguage(locale);

const getAttribute = (element: Element, attributeName: string): string | null => {
  if (!element) {
    return null;
  }

  const attribute = element.attributes.getNamedItem(attributeName);
  if (!attribute) {
    return null;
  }

  return attribute.value;
}

const getIntAttribute = (element: Element, attributeName: string): number | null => {
  const value = getAttribute(element, attributeName);
  return value ? parseInt(value) : null;
}

const getBoolAttribute = (element: Element, attributeName: string): boolean => {
  const value = getAttribute(element, attributeName);
  return value === "true";
}

document.addEventListener("react-command", (event: CommandEvent) => {
  if (event.detail.command == "disable-query-next") {
    initialQueryValidationMessage = event.detail.data.reason || strings.panel.query.noAnswer;
  } else if (event.detail.command == "enable-query-next") {
    initialQueryValidationMessage = undefined;
  }
});

window.addEventListener('load', () => {
  const queryComments = document.getElementById("query-comments");
  const queryPageLive2D = document.getElementById("query-page-live2d");
  const queryNavigation = document.getElementById("query-navigation");
  
  let pageChangeListener = (event: PageChangeEvent) => { }; 
  const setPageChangeListener = (listener: (event: PageChangeEvent) => void) => {
    pageChangeListener = listener;
  }

  const initalStoreState: StoreState = {
    locale: locale,
    queryValidationMessage: initialQueryValidationMessage || null
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);

  if (queryComments) {
    const panelId: number | null = getIntAttribute(queryComments, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryComments, "data-query-id");
    const pageId: number | null = getIntAttribute(queryComments, "data-page-id");
    const queryReplyId: number | null = getIntAttribute(queryComments, "data-query-reply-id");
    const commentable: boolean = getBoolAttribute(queryComments, "data-commentable");
    const viewDiscussion: boolean = getBoolAttribute(queryComments, "data-view-discussion");
    const canManageComments: boolean = JSDATA['canManageComments'] == 'true';

    if (panelId && queryId && pageId && queryReplyId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh />
          <MqttConnector>
            <QueryComments setPageChangeListener={ setPageChangeListener } panelId={ panelId } canManageComments={ canManageComments }  viewDiscussion={ viewDiscussion } commentable={ commentable } pageId={ pageId } queryId={ queryId } queryReplyId={ queryReplyId }/>
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryComments);
    }
  }

  if (queryPageLive2D) {
    const panelId: number | null = getIntAttribute(queryPageLive2D, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryPageLive2D, "data-query-id");
    const pageId: number | null = getIntAttribute(queryPageLive2D, "data-page-id");
    const queryReplyId: number | null = getIntAttribute(queryPageLive2D, "data-query-reply-id");

    if (panelId && queryId && pageId && queryReplyId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh />
          <MqttConnector>
            <QueryPageLive2d pageId={pageId} panelId={panelId} queryId={queryId} queryReplyId={queryReplyId} />
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryPageLive2D);
    }
  }

  if (queryNavigation) {
    const panelId: number | null = getIntAttribute(queryNavigation, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryNavigation, "data-query-id");
    const pageId: number | null = getIntAttribute(queryNavigation, "data-page-id");

    if (panelId && queryId && pageId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh />
          <MqttConnector>
            <QueryNavigation pageId={pageId} panelId={panelId} queryId={queryId} onPageChange={ pageChangeListener }/>
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryNavigation);
    }
  }

});