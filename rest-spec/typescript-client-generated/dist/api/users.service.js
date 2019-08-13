"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var URI = require("urijs");
var api_1 = require("./api");
var UsersService = /** @class */ (function () {
    function UsersService(basePath, token) {
        this.token = token;
        this.basePath = basePath;
    }
    /**
     * Finds an user by id
     * @summary Find user
     * @param userId user id
    */
    UsersService.prototype.findUser = function (userId) {
        var uri = new URI(this.basePath + "/users/" + encodeURIComponent(String(userId)));
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
    return UsersService;
}());
exports.UsersService = UsersService;
