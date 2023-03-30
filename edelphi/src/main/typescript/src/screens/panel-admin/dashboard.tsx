import * as React from "react";
import * as ReactDOM from "react-dom";
import { createStore } from 'redux';
import { AppAction } from "../../actions";
import { reducer } from "../../reducers";
import { Provider } from "react-redux";
import AccessTokenRefresh from "../../components/access-token-refresh";
import PanelAdminDashboard from "../../components/panel-admin/panel-admin-dashboard";

import strings from "../../localization/strings";
import { StoreState } from "../../types";
import getLanguage from "../../localization/language";

declare const JSDATA: any;

window.addEventListener('load', () => {
  const locale: string = getLanguage();
  strings.setLanguage(locale);
  const panelAdminDashboard = document.getElementById("panel-admin-dashboard");
  
  const initialStoreState: StoreState = {
    locale: locale,
    queryValidationMessage: null
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initialStoreState);

  if (panelAdminDashboard) {
    const panelId = parseInt(JSDATA['securityContextId']);

    if (panelId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh>
            <PanelAdminDashboard
              panelId={ panelId }
            />
          </AccessTokenRefresh>
        </Provider>;

      ReactDOM.render(component, panelAdminDashboard);
    }
  }

});
