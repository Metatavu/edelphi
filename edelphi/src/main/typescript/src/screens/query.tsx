import * as React from "react";
import * as ReactDOM from "react-dom";
import AccessTokenRefresh from "../components/access-token-refresh";
import MqttConnector from "../components/mqtt-connector";
import { createStore } from 'redux';
import { StoreState, CommandEvent } from "../types";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import { Provider } from "react-redux";
import strings from "../localization/strings";
import "semantic-ui-less/semantic.less";
import QueryPageLive2d from "../components/panel/query-page-live-2d";
import QueryComments from "../components/panel/query-comments";
import QueryNavigation from "../components/panel/query-navigation";
import getLanguage from "../localization/language";
import { QueryState } from "../generated/client/models";
import LegacyUtils from "../utils/legacy-utils";
import DomUtils from "../utils/dom-utils";

declare const JSDATA: any;
let initialQueryValidationMessage: string | undefined = undefined;
const locale: string = getLanguage();
strings.setLanguage(locale);

LegacyUtils.addCommandListener((event: CommandEvent) => {
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
  
  const initalStoreState: StoreState = {
    locale: locale,
    queryValidationMessage: initialQueryValidationMessage || null
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);

  if (queryComments) {
    const panelId: number | null = DomUtils.getIntAttribute(queryComments, "data-panel-id");
    const queryId: number | null = DomUtils.getIntAttribute(queryComments, "data-query-id");
    const pageId: number | null = DomUtils.getIntAttribute(queryComments, "data-page-id");
    const queryReplyId: number | null = DomUtils.getIntAttribute(queryComments, "data-query-reply-id");
    const commentable: boolean = DomUtils.getBoolAttribute(queryComments, "data-commentable");
    const viewDiscussion: boolean = DomUtils.getBoolAttribute(queryComments, "data-view-discussion");
    const canManageComments: boolean = JSDATA['canManageComments'] == 'true';

    if (panelId && queryId && pageId && queryReplyId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh>
            <MqttConnector>
              <QueryComments
                panelId={ panelId }
                canManageComments={ canManageComments }
                viewDiscussion={ viewDiscussion }
                commentable={ commentable }
                pageId={ pageId }
                queryId={ queryId }
                queryReplyId={ queryReplyId }
              />
            </MqttConnector>
          </AccessTokenRefresh>
        </Provider>;

      ReactDOM.render(component, queryComments);
    }
  }

  if (queryPageLive2D) {
    const panelId: number | null = DomUtils.getIntAttribute(queryPageLive2D, "data-panel-id");
    const queryId: number | null = DomUtils.getIntAttribute(queryPageLive2D, "data-query-id");
    const pageId: number | null = DomUtils.getIntAttribute(queryPageLive2D, "data-page-id");
    const queryReplyId: number | null = DomUtils.getIntAttribute(queryPageLive2D, "data-query-reply-id");

    if (panelId && queryId && pageId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh>
            <MqttConnector>
              <QueryPageLive2d
                pageId={ pageId }
                panelId={ panelId }
                queryId={ queryId }
                queryReplyId={ queryReplyId }
              />
            </MqttConnector>
          </AccessTokenRefresh>
        </Provider>;

      ReactDOM.render(component, queryPageLive2D);
    }
  }

  if (queryNavigation) {
    const panelId: number | null = DomUtils.getIntAttribute(queryNavigation, "data-panel-id");
    const queryId: number | null = DomUtils.getIntAttribute(queryNavigation, "data-query-id");
    const pageId: number | null = DomUtils.getIntAttribute(queryNavigation, "data-page-id");
    const queryState = (DomUtils.getAttribute(queryNavigation, "data-query-state") as QueryState) || QueryState.ACTIVE;

    if (panelId && queryId && pageId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh>
            <MqttConnector>
              <QueryNavigation
                queryState={ queryState }
                pageId={ pageId }
                panelId={ panelId }
                queryId={ queryId }
              />
            </MqttConnector>
          </AccessTokenRefresh>
        </Provider>;

      ReactDOM.render(component, queryNavigation);
    }
  }

});
