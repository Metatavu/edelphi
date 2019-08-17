import Api from "edelphi-client";

import "./screens/query";
import "./screens/panel-admin/query-editor";
import "./screens/main";

const location = window.location;
Api.configure(`${location.protocol}//${location.hostname}:${location.port}/api/v1`);