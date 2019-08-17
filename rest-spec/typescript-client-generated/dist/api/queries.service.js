"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var QueriesService = /** @class */ (function () {
    function QueriesService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Lists queries in a panel
     * @summary Lists queries in a panel.
     * @param panelId panel id
    */
    QueriesService.prototype.listQueries = function (panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queries");
        var options = {
            method: "get",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + this.token
            }
        };
        return fetch(uri.toString(), options).then(function (response) {
            return api_1.ApiUtils.handleResponse(response);
        });
    };
    return QueriesService;
}());
exports.QueriesService = QueriesService;
