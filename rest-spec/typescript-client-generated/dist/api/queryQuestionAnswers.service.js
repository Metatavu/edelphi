"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var QueryQuestionAnswersService = /** @class */ (function () {
    function QueryQuestionAnswersService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Deletes query question answers
     * @summary Delete query question answers
     * @param panelId panel id
     * @param queryId Delete answers by query
     * @param queryPageId Delete answers by query page
     * @param queryReplyId Delete answers by query reply
    */
    QueryQuestionAnswersService.prototype.deleteQueryQuestionAnswers = function (panelId, queryId, queryPageId, queryReplyId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionAnswers");
        if (queryId !== undefined && queryId !== null) {
            uri.addQuery('queryId', queryId);
        }
        if (queryPageId !== undefined && queryPageId !== null) {
            uri.addQuery('queryPageId', queryPageId);
        }
        if (queryReplyId !== undefined && queryReplyId !== null) {
            uri.addQuery('queryReplyId', queryReplyId);
        }
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
     * Finds query question answer by id
     * @summary Find query question answer.
     * @param panelId panel id
     * @param answerId query question answer id
    */
    QueryQuestionAnswersService.prototype.findQueryQuestionAnswer = function (panelId, answerId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionAnswers/" + encodeURIComponent(String(answerId)));
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
     * Lists query question answers
     * @summary Lists query question answers
     * @param panelId panel id
     * @param queryId Filter by query id
     * @param pageId Filter by query page id
     * @param userId Filter by user id
     * @param stampId Filter by stamp id. Defaults to current stamp
    */
    QueryQuestionAnswersService.prototype.listQueryQuestionAnswers = function (panelId, queryId, pageId, userId, stampId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionAnswers");
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
     * Creates or updates query question answer
     * @summary Creates or updates query question answer
     * @param body Payload
     * @param panelId panel id
     * @param answerId query question answer id
    */
    QueryQuestionAnswersService.prototype.upsertQueryQuestionAnswer = function (body, panelId, answerId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionAnswers/" + encodeURIComponent(String(answerId)));
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
    return QueryQuestionAnswersService;
}());
exports.QueryQuestionAnswersService = QueryQuestionAnswersService;
