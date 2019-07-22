"use strict";
function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(require("./panels.service"));
var panels_service_1 = require("./panels.service");
__export(require("./queries.service"));
var queries_service_1 = require("./queries.service");
__export(require("./queryPages.service"));
var queryPages_service_1 = require("./queryPages.service");
__export(require("./queryQuestionAnswers.service"));
var queryQuestionAnswers_service_1 = require("./queryQuestionAnswers.service");
__export(require("./queryQuestionCommentCategories.service"));
var queryQuestionCommentCategories_service_1 = require("./queryQuestionCommentCategories.service");
__export(require("./queryQuestionComments.service"));
var queryQuestionComments_service_1 = require("./queryQuestionComments.service");
__export(require("./reports.service"));
var reports_service_1 = require("./reports.service");
var ApiUtils = /** @class */ (function () {
    function ApiUtils() {
    }
    /**
     * Handles response from API
     *
     * @param response response object
     */
    ApiUtils.handleResponse = function (response) {
        switch (response.status) {
            case 202:
            case 204:
                return {};
            default:
                return response.json();
        }
    };
    return ApiUtils;
}());
exports.ApiUtils = ApiUtils;
exports.default = new /** @class */ (function () {
    function Api() {
        this.apiUrl = "http://localhost";
    }
    /**
     * Configures api endpoint
     *
     */
    Api.prototype.configure = function (baseUrl) {
        this.apiUrl = baseUrl;
    };
    Api.prototype.getPanelsService = function (token) {
        return new panels_service_1.PanelsService(this.apiUrl, token);
    };
    Api.prototype.getQueriesService = function (token) {
        return new queries_service_1.QueriesService(this.apiUrl, token);
    };
    Api.prototype.getQueryPagesService = function (token) {
        return new queryPages_service_1.QueryPagesService(this.apiUrl, token);
    };
    Api.prototype.getQueryQuestionAnswersService = function (token) {
        return new queryQuestionAnswers_service_1.QueryQuestionAnswersService(this.apiUrl, token);
    };
    Api.prototype.getQueryQuestionCommentCategoriesService = function (token) {
        return new queryQuestionCommentCategories_service_1.QueryQuestionCommentCategoriesService(this.apiUrl, token);
    };
    Api.prototype.getQueryQuestionCommentsService = function (token) {
        return new queryQuestionComments_service_1.QueryQuestionCommentsService(this.apiUrl, token);
    };
    Api.prototype.getReportsService = function (token) {
        return new reports_service_1.ReportsService(this.apiUrl, token);
    };
    return Api;
}());
