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
     * Creates copy of an query
     * @summary Create copy of an query
     * @param panelId panel id
     * @param queryId panel id
     * @param targetPanelId target panel panel id
     * @param copyData whether to copy query data
     * @param newName new name for query copy
    */
    QueriesService.prototype.copyQuery = function (panelId, queryId, targetPanelId, copyData, newName) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queries/" + encodeURIComponent(String(queryId)) + "/copy");
        if (targetPanelId !== undefined && targetPanelId !== null) {
            uri.addQuery('targetPanelId', targetPanelId);
        }
        if (copyData !== undefined && copyData !== null) {
            uri.addQuery('copyData', copyData);
        }
        if (newName !== undefined && newName !== null) {
            uri.addQuery('newName', newName);
        }
        var options = {
            method: "post",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + this.token
            }
        };
        return fetch(uri.toString(), options).then(function (response) {
            return api_1.ApiUtils.handleResponse(response);
        });
    };
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
