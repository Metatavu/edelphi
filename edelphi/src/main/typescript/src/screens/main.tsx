import * as React from "react";
import * as ReactDOM from "react-dom";
import { createStore } from 'redux';
import App from "../components/app";
import { StoreState } from "src/types";
import "semantic-ui-less/semantic.less";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import strings from "../localization/strings";
import getLanguage from "../localization/language";
import { Provider } from "react-redux";
import AccessTokenRefresh from "../components/access-token-refresh";
import MqttConnector from "../components/mqtt-connector";

/**
 * Entry point for completely ReactJS based views
 */
window.addEventListener('load', () => {
  const locale: string = getLanguage();

  strings.setLanguage(locale);
  const initalStoreState: StoreState = {
    locale: locale
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);

  const root = document.getElementById("root");

  if (root) {
    ReactDOM.render(
      <Provider store={store}>
        <AccessTokenRefresh />
        <MqttConnector>
          <App/>
        </MqttConnector>
      </Provider>, root);
  }

});