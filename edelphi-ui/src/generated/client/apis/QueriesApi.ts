// tslint:disable
// eslint-disable
/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * The version of the OpenAPI document: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import {
    ErrorResponse,
    ErrorResponseFromJSON,
    ErrorResponseToJSON,
    Query,
    QueryFromJSON,
    QueryToJSON,
} from '../models';

export interface ListQueriesRequest {
    panel_id: number;
}

/**
 * no description
 */
export class QueriesApi extends runtime.BaseAPI {

    /**
     * Lists queries in a panel
     * Lists queries in a panel.
     */
    async listQueriesRaw(requestParameters: ListQueriesRequest): Promise<runtime.ApiResponse<Array<Query>>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling listQueries.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queries`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(QueryFromJSON));
    }

    /**
     * Lists queries in a panel
     * Lists queries in a panel.
     */
    async listQueries(requestParameters: ListQueriesRequest): Promise<Array<Query>> {
        const response = await this.listQueriesRaw(requestParameters);
        return await response.value();
    }

}
