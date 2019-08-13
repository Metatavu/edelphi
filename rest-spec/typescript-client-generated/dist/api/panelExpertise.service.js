"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var PanelExpertiseService = /** @class */ (function () {
    function PanelExpertiseService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * List defined expertise classes from a panel
     * @summary List panel expertise classes
     * @param panelId panel id
    */
    PanelExpertiseService.prototype.listExpertiseClasses = function (panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/expertiseClasses");
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
     * List defined expertise groups from a panel
     * @summary List panel expertise groups
     * @param panelId panel id
    */
    PanelExpertiseService.prototype.listExpertiseGroups = function (panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/expertiseGroups");
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
     * List defined interest classes from a panel
     * @summary List panel interest classes
     * @param panelId panel id
    */
    PanelExpertiseService.prototype.listInterestClasses = function (panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)) + "/interestClasses");
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
    return PanelExpertiseService;
}());
exports.PanelExpertiseService = PanelExpertiseService;
