"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var ReportsService = /** @class */ (function () {
    function ReportsService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Creates a request to generate a report
     * @summary Creates a report request
     * @param body Payload
    */
    ReportsService.prototype.createReportRequest = function (body) {
        var uri = new URI(this.basePath + "/reportRequests");
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
    return ReportsService;
}());
exports.ReportsService = ReportsService;
