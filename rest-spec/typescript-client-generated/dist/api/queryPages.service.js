"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var QueryPagesService = /** @class */ (function () {
    function QueryPagesService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Finds query page by id
     * @summary Find query page.
     * @param panelId panel id
     * @param queryPageId query page id
    */
    QueryPagesService.prototype.findQueryPage = function (panelId, queryPageId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryPages/" + encodeURIComponent(String(queryPageId)));
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
    /**
     * Updates query page
     * @summary Update query page
     * @param body Payload
     * @param panelId panel id
     * @param queryPageId query page id
    */
    QueryPagesService.prototype.updateQueryPage = function (body, panelId, queryPageId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryPages/" + encodeURIComponent(String(queryPageId)));
        var options = {
            method: "put",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + this.token
            },
            body: JSON.stringify(body)
        };
        return fetch(uri.toString(), options).then(function (response) {
            return api_1.ApiUtils.handleResponse(response);
        });
    };
    return QueryPagesService;
}());
exports.QueryPagesService = QueryPagesService;
