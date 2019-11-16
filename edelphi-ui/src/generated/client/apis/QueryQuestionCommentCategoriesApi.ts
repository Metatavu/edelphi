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
    QueryQuestionCommentCategory,
    QueryQuestionCommentCategoryFromJSON,
    QueryQuestionCommentCategoryToJSON,
} from '../models';

export interface CreateQueryQuestionCommentCategoryRequest {
    query_question_comment_category: QueryQuestionCommentCategory;
    panel_id: number;
}

export interface DeleteQueryQuestionCommentCategoryRequest {
    panel_id: number;
    category_id: number;
}

export interface FindQueryQuestionCommentCategoryRequest {
    panel_id: number;
    category_id: number;
}

export interface ListQueryQuestionCommentCategoriesRequest {
    panel_id: number;
    page_id?: number;
    query_id?: number;
}

export interface UpdateQueryQuestionCommentCategoryRequest {
    query_question_comment_category: QueryQuestionCommentCategory;
    panel_id: number;
    category_id: number;
}

/**
 * no description
 */
export class QueryQuestionCommentCategoriesApi extends runtime.BaseAPI {

    /**
     * Creates query question category
     * Create query question category
     */
    async createQueryQuestionCommentCategoryRaw(requestParameters: CreateQueryQuestionCommentCategoryRequest): Promise<runtime.ApiResponse<QueryQuestionCommentCategory>> {
        if (requestParameters.query_question_comment_category === null || requestParameters.query_question_comment_category === undefined) {
            throw new runtime.RequiredError('query_question_comment_category','Required parameter requestParameters.query_question_comment_category was null or undefined when calling createQueryQuestionCommentCategory.');
        }

        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling createQueryQuestionCommentCategory.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionCommentCategories`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: QueryQuestionCommentCategoryToJSON(requestParameters.query_question_comment_category),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionCommentCategoryFromJSON(jsonValue));
    }

    /**
     * Creates query question category
     * Create query question category
     */
    async createQueryQuestionCommentCategory(requestParameters: CreateQueryQuestionCommentCategoryRequest): Promise<QueryQuestionCommentCategory> {
        const response = await this.createQueryQuestionCommentCategoryRaw(requestParameters);
        return await response.value();
    }

    /**
     * Deletes query question category
     * Delete query question category
     */
    async deleteQueryQuestionCommentCategoryRaw(requestParameters: DeleteQueryQuestionCommentCategoryRequest): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling deleteQueryQuestionCommentCategory.');
        }

        if (requestParameters.category_id === null || requestParameters.category_id === undefined) {
            throw new runtime.RequiredError('category_id','Required parameter requestParameters.category_id was null or undefined when calling deleteQueryQuestionCommentCategory.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionCommentCategories/{categoryId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"categoryId"}}`, encodeURIComponent(String(requestParameters.category_id))),
            method: 'DELETE',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.VoidApiResponse(response);
    }

    /**
     * Deletes query question category
     * Delete query question category
     */
    async deleteQueryQuestionCommentCategory(requestParameters: DeleteQueryQuestionCommentCategoryRequest): Promise<void> {
        await this.deleteQueryQuestionCommentCategoryRaw(requestParameters);
    }

    /**
     * Finds query question category by id
     * Find query question category
     */
    async findQueryQuestionCommentCategoryRaw(requestParameters: FindQueryQuestionCommentCategoryRequest): Promise<runtime.ApiResponse<QueryQuestionCommentCategory>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling findQueryQuestionCommentCategory.');
        }

        if (requestParameters.category_id === null || requestParameters.category_id === undefined) {
            throw new runtime.RequiredError('category_id','Required parameter requestParameters.category_id was null or undefined when calling findQueryQuestionCommentCategory.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionCommentCategories/{categoryId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"categoryId"}}`, encodeURIComponent(String(requestParameters.category_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionCommentCategoryFromJSON(jsonValue));
    }

    /**
     * Finds query question category by id
     * Find query question category
     */
    async findQueryQuestionCommentCategory(requestParameters: FindQueryQuestionCommentCategoryRequest): Promise<QueryQuestionCommentCategory> {
        const response = await this.findQueryQuestionCommentCategoryRaw(requestParameters);
        return await response.value();
    }

    /**
     * Lists query question categories
     * Lists query question categories
     */
    async listQueryQuestionCommentCategoriesRaw(requestParameters: ListQueryQuestionCommentCategoriesRequest): Promise<runtime.ApiResponse<Array<QueryQuestionCommentCategory>>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling listQueryQuestionCommentCategories.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.page_id !== undefined) {
            queryParameters['pageId'] = requestParameters.page_id;
        }

        if (requestParameters.query_id !== undefined) {
            queryParameters['queryId'] = requestParameters.query_id;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionCommentCategories`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(QueryQuestionCommentCategoryFromJSON));
    }

    /**
     * Lists query question categories
     * Lists query question categories
     */
    async listQueryQuestionCommentCategories(requestParameters: ListQueryQuestionCommentCategoriesRequest): Promise<Array<QueryQuestionCommentCategory>> {
        const response = await this.listQueryQuestionCommentCategoriesRaw(requestParameters);
        return await response.value();
    }

    /**
     * Updates query question category
     * Update query question category
     */
    async updateQueryQuestionCommentCategoryRaw(requestParameters: UpdateQueryQuestionCommentCategoryRequest): Promise<runtime.ApiResponse<QueryQuestionCommentCategory>> {
        if (requestParameters.query_question_comment_category === null || requestParameters.query_question_comment_category === undefined) {
            throw new runtime.RequiredError('query_question_comment_category','Required parameter requestParameters.query_question_comment_category was null or undefined when calling updateQueryQuestionCommentCategory.');
        }

        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling updateQueryQuestionCommentCategory.');
        }

        if (requestParameters.category_id === null || requestParameters.category_id === undefined) {
            throw new runtime.RequiredError('category_id','Required parameter requestParameters.category_id was null or undefined when calling updateQueryQuestionCommentCategory.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryQuestionCommentCategories/{categoryId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"categoryId"}}`, encodeURIComponent(String(requestParameters.category_id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: QueryQuestionCommentCategoryToJSON(requestParameters.query_question_comment_category),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryQuestionCommentCategoryFromJSON(jsonValue));
    }

    /**
     * Updates query question category
     * Update query question category
     */
    async updateQueryQuestionCommentCategory(requestParameters: UpdateQueryQuestionCommentCategoryRequest): Promise<QueryQuestionCommentCategory> {
        const response = await this.updateQueryQuestionCommentCategoryRaw(requestParameters);
        return await response.value();
    }

}
