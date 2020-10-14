/* tslint:disable */
/* eslint-disable */
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
    QueryQuestionComment,
    QueryQuestionCommentFromJSON,
    QueryQuestionCommentToJSON,
} from '../models';

export interface CopyQueryRequest {
    panelId: number;
    queryId: number;
    targetPanelId: number;
    copyData: boolean;
    newName: string;
}

export interface ListQueriesRequest {
    panelId: number;
}

/**
 * 
 */
export class QueriesApi extends runtime.BaseAPI {

    /**
     * Creates copy of an query
     * Create copy of an query
     */
    async copyQueryRaw(requestParameters: CopyQueryRequest): Promise<runtime.ApiResponse<QueryQuestionComment>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling copyQuery.');
        }

        if (requestParameters.queryId === null || requestParameters.queryId === undefined) {
            throw new runtime.RequiredError('queryId','Required parameter requestParameters.queryId was null or undefined when calling copyQuery.');
        }

        if (requestParameters.targetPanelId === null || requestParameters.targetPanelId === undefined) {
            throw new runtime.RequiredError('targetPanelId','Required parameter requestParameters.targetPanelId was null or undefined when calling copyQuery.');
        }

        if (requestParameters.copyData === null || requestParameters.copyData === undefined) {
            throw new runtime.RequiredError('copyData','Required parameter requestParameters.copyData was null or undefined when calling copyQuery.');
        }

        if (requestParameters.newName === null || requestParameters.newName === undefined) {
            throw new runtime.RequiredError('newName','Required parameter requestParameters.newName was null or undefined when calling copyQuery.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.targetPanelId !== undefined) {
            queryParameters['targetPanelId'] = requestParameters.targetPanelId;
        }

        if (requestParameters.copyData !== undefined) {
            queryParameters['copyData'] = requestParameters.copyData;
        }

        if (requestParameters.newName !== undefined) {
            queryParameters['newName'] = requestParameters.newName;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queries/{queryId}/copy`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))).replace(`{${"queryId"}}`, encodeURIComponent(String(requestParameters.queryId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionCommentFromJSON(jsonValue));
    }

    /**
     * Creates copy of an query
     * Create copy of an query
     */
    async copyQuery(requestParameters: CopyQueryRequest): Promise<QueryQuestionComment> {
        const response = await this.copyQueryRaw(requestParameters);
        return await response.value();
    }

    /**
     * Lists queries in a panel
     * Lists queries in a panel.
     */
    async listQueriesRaw(requestParameters: ListQueriesRequest): Promise<runtime.ApiResponse<Array<Query>>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling listQueries.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queries`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
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
