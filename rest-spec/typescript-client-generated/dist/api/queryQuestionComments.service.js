"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var QueryQuestionCommentsService = /** @class */ (function () {
    function QueryQuestionCommentsService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Creates query question comment
     * @summary Create query question comment
     * @param body Payload
     * @param panelId panel id
    */
    QueryQuestionCommentsService.prototype.createQueryQuestionComment = function (body, panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionComments");
        var options = {
            method: "post",
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
    /**
     * Deletes query question comment
     * @summary Delete query question comment
     * @param panelId panel id
     * @param commentId query question comment id
    */
    QueryQuestionCommentsService.prototype.deleteQueryQuestionComment = function (panelId, commentId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionComments/" + encodeURIComponent(String(commentId)));
        var options = {
            method: "delete",
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
     * Finds query question comment by id
     * @summary Find query question comment
     * @param panelId panel id
     * @param commentId query question comment id
    */
    QueryQuestionCommentsService.prototype.findQueryQuestionComment = function (panelId, commentId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionComments/" + encodeURIComponent(String(commentId)));
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
     * Lists query question comments
     * @summary Lists query question comments
     * @param panelId panel id
     * @param queryId Filter by query id
     * @param pageId Filter by query page id
     * @param userId Filter by user id
     * @param stampId Filter by stamp id. Defaults to current stamp
     * @param parentId parent comment id. With zero only root comments are returned
     * @param categoryId category id. If zero is specified only non categorized comments are returned
    */
    QueryQuestionCommentsService.prototype.listQueryQuestionComments = function (panelId, queryId, pageId, userId, stampId, parentId, categoryId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionComments");
        if (queryId !== undefined && queryId !== null) {
            uri.addQuery('queryId', queryId);
        }
        if (pageId !== undefined && pageId !== null) {
            uri.addQuery('pageId', pageId);
        }
        if (userId !== undefined && userId !== null) {
            uri.addQuery('userId', userId);
        }
        if (stampId !== undefined && stampId !== null) {
            uri.addQuery('stampId', stampId);
        }
        if (parentId !== undefined && parentId !== null) {
            uri.addQuery('parentId', parentId);
        }
        if (categoryId !== undefined && categoryId !== null) {
            uri.addQuery('categoryId', categoryId);
        }
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
     * Updates query question comment
     * @summary Update query question comment
     * @param body Payload
     * @param panelId panel id
     * @param commentId query question comment id
    */
    QueryQuestionCommentsService.prototype.updateQueryQuestionComment = function (body, panelId, commentId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionComments/" + encodeURIComponent(String(commentId)));
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
    return QueryQuestionCommentsService;
}());
exports.QueryQuestionCommentsService = QueryQuestionCommentsService;
