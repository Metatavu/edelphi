import * as React from "react";
import * as ReactDOM from "react-dom";
import { createStore } from 'redux';
import { AppAction } from "../../actions";
import { reducer } from "../../reducers";
import { Provider } from "react-redux";
import AccessTokenRefresh from "../../components/access-token-refresh";
import PanelAdminQueryEditor from "../../components/panel-admin/panel-admin-query-editor";

import strings from "../../localization/strings";
import { StoreState } from "../../types";
import getLanguage from "../../localization/language";
import DomUtils from "../../utils/dom-utils";

declare const JSDATA: any;

window.addEventListener('load', () => {
  const locale: string = getLanguage();
  strings.setLanguage(locale);
  const panelAdminQueryEditor = document.getElementById("panel-admin-query-editor");
  const initialStoreState: StoreState = {
    locale: locale,
    queryValidationMessage: null
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initialStoreState);

  if (panelAdminQueryEditor) {
    const panelId = parseInt(JSDATA['securityContextId']);
    const queryId: number | null = DomUtils.getIntAttribute(panelAdminQueryEditor, "data-query-id");
    const openCopyDialog = document.URL.endsWith("&copy=true");

    if (panelId && queryId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh>
            <PanelAdminQueryEditor
              panelId={ panelId }
              queryId={ queryId }
              openCopyDialog={ openCopyDialog }
            />
          </AccessTokenRefresh>
        </Provider>;

      ReactDOM.render(component, panelAdminQueryEditor);
    }
  }

});
