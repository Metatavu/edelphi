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
    QueryQuestionAnswer,
    QueryQuestionAnswerFromJSON,
    QueryQuestionAnswerToJSON,
} from '../models';

export interface DeleteQueryQuestionAnswersRequest {
    panelId: number;
    queryId?: number;
    queryPageId?: number;
    querySectionId?: number;
}

export interface FindQueryQuestionAnswerRequest {
    panelId: number;
    answerId: string;
}

export interface ListQueryQuestionAnswersRequest {
    panelId: number;
    queryId?: number;
    pageId?: number;
    userId?: string;
    stampId?: number;
}

export interface UpsertQueryQuestionAnswerRequest {
    queryQuestionAnswer: QueryQuestionAnswer;
    panelId: number;
    answerId: string;
}

/**
 * 
 */
export class QueryQuestionAnswersApi extends runtime.BaseAPI {

    /**
     * Deletes query question answers
     * Delete query question answers
     */
    async deleteQueryQuestionAnswersRaw(requestParameters: DeleteQueryQuestionAnswersRequest): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling deleteQueryQuestionAnswers.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.queryId !== undefined) {
            queryParameters['queryId'] = requestParameters.queryId;
        }

        if (requestParameters.queryPageId !== undefined) {
            queryParameters['queryPageId'] = requestParameters.queryPageId;
        }

        if (requestParameters.querySectionId !== undefined) {
            queryParameters['querySectionId'] = requestParameters.querySectionId;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
            method: 'DELETE',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.VoidApiResponse(response);
    }

    /**
     * Deletes query question answers
     * Delete query question answers
     */
    async deleteQueryQuestionAnswers(requestParameters: DeleteQueryQuestionAnswersRequest): Promise<void> {
        await this.deleteQueryQuestionAnswersRaw(requestParameters);
    }

    /**
     * Finds query question answer by id
     * Find query question answer.
     */
    async findQueryQuestionAnswerRaw(requestParameters: FindQueryQuestionAnswerRequest): Promise<runtime.ApiResponse<QueryQuestionAnswer>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling findQueryQuestionAnswer.');
        }

        if (requestParameters.answerId === null || requestParameters.answerId === undefined) {
            throw new runtime.RequiredError('answerId','Required parameter requestParameters.answerId was null or undefined when calling findQueryQuestionAnswer.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers/{answerId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))).replace(`{${"answerId"}}`, encodeURIComponent(String(requestParameters.answerId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionAnswerFromJSON(jsonValue));
    }

    /**
     * Finds query question answer by id
     * Find query question answer.
     */
    async findQueryQuestionAnswer(requestParameters: FindQueryQuestionAnswerRequest): Promise<QueryQuestionAnswer> {
        const response = await this.findQueryQuestionAnswerRaw(requestParameters);
        return await response.value();
    }

    /**
     * Lists query question answers
     * Lists query question answers
     */
    async listQueryQuestionAnswersRaw(requestParameters: ListQueryQuestionAnswersRequest): Promise<runtime.ApiResponse<Array<QueryQuestionAnswer>>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling listQueryQuestionAnswers.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.queryId !== undefined) {
            queryParameters['queryId'] = requestParameters.queryId;
        }

        if (requestParameters.pageId !== undefined) {
            queryParameters['pageId'] = requestParameters.pageId;
        }

        if (requestParameters.userId !== undefined) {
            queryParameters['userId'] = requestParameters.userId;
        }

        if (requestParameters.stampId !== undefined) {
            queryParameters['stampId'] = requestParameters.stampId;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(QueryQuestionAnswerFromJSON));
    }

    /**
     * Lists query question answers
     * Lists query question answers
     */
    async listQueryQuestionAnswers(requestParameters: ListQueryQuestionAnswersRequest): Promise<Array<QueryQuestionAnswer>> {
        const response = await this.listQueryQuestionAnswersRaw(requestParameters);
        return await response.value();
    }

    /**
     * Creates or updates query question answer
     * Creates or updates query question answer
     */
    async upsertQueryQuestionAnswerRaw(requestParameters: UpsertQueryQuestionAnswerRequest): Promise<runtime.ApiResponse<QueryQuestionAnswer>> {
        if (requestParameters.queryQuestionAnswer === null || requestParameters.queryQuestionAnswer === undefined) {
            throw new runtime.RequiredError('queryQuestionAnswer','Required parameter requestParameters.queryQuestionAnswer was null or undefined when calling upsertQueryQuestionAnswer.');
        }

        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling upsertQueryQuestionAnswer.');
        }

        if (requestParameters.answerId === null || requestParameters.answerId === undefined) {
            throw new runtime.RequiredError('answerId','Required parameter requestParameters.answerId was null or undefined when calling upsertQueryQuestionAnswer.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers/{answerId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))).replace(`{${"answerId"}}`, encodeURIComponent(String(requestParameters.answerId))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: QueryQuestionAnswerToJSON(requestParameters.queryQuestionAnswer),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionAnswerFromJSON(jsonValue));
    }

    /**
     * Creates or updates query question answer
     * Creates or updates query question answer
     */
    async upsertQueryQuestionAnswer(requestParameters: UpsertQueryQuestionAnswerRequest): Promise<QueryQuestionAnswer> {
        const response = await this.upsertQueryQuestionAnswerRaw(requestParameters);
        return await response.value();
    }

}
