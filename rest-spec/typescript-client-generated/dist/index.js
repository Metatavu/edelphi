"use strict";
function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
var api_1 = require("./api/api");
__export(require("./model/models"));
exports.default = api_1.default;
