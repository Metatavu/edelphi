"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var QueryQuestionCommentCategoriesService = /** @class */ (function () {
    function QueryQuestionCommentCategoriesService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Creates query question category
     * @summary Create query question category
     * @param body Payload
     * @param panelId panel id
    */
    QueryQuestionCommentCategoriesService.prototype.createQueryQuestionCommentCategory = function (body, panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionCommentCategories");
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
     * Deletes query question category
     * @summary Delete query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    QueryQuestionCommentCategoriesService.prototype.deleteQueryQuestionCommentCategory = function (panelId, categoryId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionCommentCategories/" + encodeURIComponent(String(categoryId)));
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
     * Finds query question category by id
     * @summary Find query question category
     * @param panelId panel id
     * @param categoryId query question category id
    */
    QueryQuestionCommentCategoriesService.prototype.findQueryQuestionCommentCategory = function (panelId, categoryId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionCommentCategories/" + encodeURIComponent(String(categoryId)));
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
     * Lists query question categories
     * @summary Lists query question categories
     * @param panelId panel id
     * @param pageId Filter by query page id
    */
    QueryQuestionCommentCategoriesService.prototype.listQueryQuestionCommentCategories = function (panelId, pageId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionCommentCategories");
        if (pageId !== undefined && pageId !== null) {
            uri.addQuery('pageId', pageId);
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
     * Updates query question category
     * @summary Update query question category
     * @param body Payload
     * @param panelId panel id
     * @param categoryId query question category id
    */
    QueryQuestionCommentCategoriesService.prototype.updateQueryQuestionCommentCategory = function (body, panelId, categoryId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/queryQuestionCommentCategories/" + encodeURIComponent(String(categoryId)));
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
    return QueryQuestionCommentCategoriesService;
}());
exports.QueryQuestionCommentCategoriesService = QueryQuestionCommentCategoriesService;
