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
    QueryQuestionAnswer,
    QueryQuestionAnswerFromJSON,
    QueryQuestionAnswerToJSON,
    QueryQuestionAnswerLive2d,
    QueryQuestionAnswerLive2dFromJSON,
    QueryQuestionAnswerLive2dToJSON,
} from '../models';

export interface DeleteQueryQuestionAnswersRequest {
    panel_id: number;
    query_id?: number;
    query_page_id?: number;
    query_section_id?: number;
}

export interface FindQueryQuestionAnswerLive2dRequest {
    panel_id: number;
    answer_id: string;
}

export interface ListQueryQuestionAnswersRequest {
    panel_id: number;
    query_id?: number;
    page_id?: number;
    user_id?: string;
    stamp_id?: number;
}

export interface UpsertQueryQuestionAnswerLive2dRequest {
    query_question_answer_live2d: QueryQuestionAnswerLive2d;
    panel_id: number;
    answer_id: string;
}

/**
 * no description
 */
export class QueryQuestionAnswersApi extends runtime.BaseAPI {

    /**
     * Deletes query question answers
     * Delete query question answers
     */
    async deleteQueryQuestionAnswersRaw(requestParameters: DeleteQueryQuestionAnswersRequest): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling deleteQueryQuestionAnswers.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.query_id !== undefined) {
            queryParameters['queryId'] = requestParameters.query_id;
        }

        if (requestParameters.query_page_id !== undefined) {
            queryParameters['queryPageId'] = requestParameters.query_page_id;
        }

        if (requestParameters.query_section_id !== undefined) {
            queryParameters['querySectionId'] = requestParameters.query_section_id;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
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
    async findQueryQuestionAnswerLive2dRaw(requestParameters: FindQueryQuestionAnswerLive2dRequest): Promise<runtime.ApiResponse<QueryQuestionAnswerLive2d>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling findQueryQuestionAnswerLive2d.');
        }

        if (requestParameters.answer_id === null || requestParameters.answer_id === undefined) {
            throw new runtime.RequiredError('answer_id','Required parameter requestParameters.answer_id was null or undefined when calling findQueryQuestionAnswerLive2d.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/live2dQueryQuestionAnswers/{answerId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"answerId"}}`, encodeURIComponent(String(requestParameters.answer_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionAnswerLive2dFromJSON(jsonValue));
    }

    /**
     * Finds query question answer by id
     * Find query question answer.
     */
    async findQueryQuestionAnswerLive2d(requestParameters: FindQueryQuestionAnswerLive2dRequest): Promise<QueryQuestionAnswerLive2d> {
        const response = await this.findQueryQuestionAnswerLive2dRaw(requestParameters);
        return await response.value();
    }

    /**
     * Lists query question answers
     * Lists query question answers
     */
    async listQueryQuestionAnswersRaw(requestParameters: ListQueryQuestionAnswersRequest): Promise<runtime.ApiResponse<Array<QueryQuestionAnswer>>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling listQueryQuestionAnswers.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.query_id !== undefined) {
            queryParameters['queryId'] = requestParameters.query_id;
        }

        if (requestParameters.page_id !== undefined) {
            queryParameters['pageId'] = requestParameters.page_id;
        }

        if (requestParameters.user_id !== undefined) {
            queryParameters['userId'] = requestParameters.user_id;
        }

        if (requestParameters.stamp_id !== undefined) {
            queryParameters['stampId'] = requestParameters.stamp_id;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionAnswers`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
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
    async upsertQueryQuestionAnswerLive2dRaw(requestParameters: UpsertQueryQuestionAnswerLive2dRequest): Promise<runtime.ApiResponse<QueryQuestionAnswerLive2d>> {
        if (requestParameters.query_question_answer_live2d === null || requestParameters.query_question_answer_live2d === undefined) {
            throw new runtime.RequiredError('query_question_answer_live2d','Required parameter requestParameters.query_question_answer_live2d was null or undefined when calling upsertQueryQuestionAnswerLive2d.');
        }

        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling upsertQueryQuestionAnswerLive2d.');
        }

        if (requestParameters.answer_id === null || requestParameters.answer_id === undefined) {
            throw new runtime.RequiredError('answer_id','Required parameter requestParameters.answer_id was null or undefined when calling upsertQueryQuestionAnswerLive2d.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/live2dQueryQuestionAnswers/{answerId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"answerId"}}`, encodeURIComponent(String(requestParameters.answer_id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: QueryQuestionAnswerLive2dToJSON(requestParameters.query_question_answer_live2d),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionAnswerLive2dFromJSON(jsonValue));
    }

    /**
     * Creates or updates query question answer
     * Creates or updates query question answer
     */
    async upsertQueryQuestionAnswerLive2d(requestParameters: UpsertQueryQuestionAnswerLive2dRequest): Promise<QueryQuestionAnswerLive2d> {
        const response = await this.upsertQueryQuestionAnswerLive2dRaw(requestParameters);
        return await response.value();
    }

}
