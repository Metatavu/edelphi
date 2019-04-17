"use strict";
function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(require("./queryPages.service"));
var queryPages_service_1 = require("./queryPages.service");
__export(require("./queryQuestionAnswers.service"));
var queryQuestionAnswers_service_1 = require("./queryQuestionAnswers.service");
__export(require("./queryQuestionComments.service"));
var queryQuestionComments_service_1 = require("./queryQuestionComments.service");
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
    Api.prototype.getQueryPagesService = function (token) {
        return new queryPages_service_1.QueryPagesService(this.apiUrl, token);
    };
    Api.prototype.getQueryQuestionAnswersService = function (token) {
        return new queryQuestionAnswers_service_1.QueryQuestionAnswersService(this.apiUrl, token);
    };
    Api.prototype.getQueryQuestionCommentsService = function (token) {
        return new queryQuestionComments_service_1.QueryQuestionCommentsService(this.apiUrl, token);
    };
    return Api;
}());
