"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var PanelsService = /** @class */ (function () {
    function PanelsService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Finds a panel by id
     * @summary Find a panel.
     * @param panelId panel id
    */
    PanelsService.prototype.findPanel = function (panelId) {
        var uri = new URI(this.basePath + "/panels/" + encodeURIComponent(String(panelId)));
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
    return PanelsService;
}());
exports.PanelsService = PanelsService;
