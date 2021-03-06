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
    PanelExpertiseClass,
    PanelExpertiseClassFromJSON,
    PanelExpertiseClassToJSON,
    PanelExpertiseGroup,
    PanelExpertiseGroupFromJSON,
    PanelExpertiseGroupToJSON,
    PanelInterestClass,
    PanelInterestClassFromJSON,
    PanelInterestClassToJSON,
} from '../models';

export interface ListExpertiseClassesRequest {
    panelId: number;
}

export interface ListExpertiseGroupsRequest {
    panelId: number;
}

export interface ListInterestClassesRequest {
    panelId: number;
}

/**
 * 
 */
export class PanelExpertiseApi extends runtime.BaseAPI {

    /**
     * List defined expertise classes from a panel
     * List panel expertise classes
     */
    async listExpertiseClassesRaw(requestParameters: ListExpertiseClassesRequest): Promise<runtime.ApiResponse<Array<PanelExpertiseClass>>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling listExpertiseClasses.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/expertiseClasses`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(PanelExpertiseClassFromJSON));
    }

    /**
     * List defined expertise classes from a panel
     * List panel expertise classes
     */
    async listExpertiseClasses(requestParameters: ListExpertiseClassesRequest): Promise<Array<PanelExpertiseClass>> {
        const response = await this.listExpertiseClassesRaw(requestParameters);
        return await response.value();
    }

    /**
     * List defined expertise groups from a panel
     * List panel expertise groups
     */
    async listExpertiseGroupsRaw(requestParameters: ListExpertiseGroupsRequest): Promise<runtime.ApiResponse<Array<PanelExpertiseGroup>>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling listExpertiseGroups.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/expertiseGroups`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(PanelExpertiseGroupFromJSON));
    }

    /**
     * List defined expertise groups from a panel
     * List panel expertise groups
     */
    async listExpertiseGroups(requestParameters: ListExpertiseGroupsRequest): Promise<Array<PanelExpertiseGroup>> {
        const response = await this.listExpertiseGroupsRaw(requestParameters);
        return await response.value();
    }

    /**
     * List defined interest classes from a panel
     * List panel interest classes
     */
    async listInterestClassesRaw(requestParameters: ListInterestClassesRequest): Promise<runtime.ApiResponse<Array<PanelInterestClass>>> {
        if (requestParameters.panelId === null || requestParameters.panelId === undefined) {
            throw new runtime.RequiredError('panelId','Required parameter requestParameters.panelId was null or undefined when calling listInterestClasses.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/interestClasses`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panelId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(PanelInterestClassFromJSON));
    }

    /**
     * List defined interest classes from a panel
     * List panel interest classes
     */
    async listInterestClasses(requestParameters: ListInterestClassesRequest): Promise<Array<PanelInterestClass>> {
        const response = await this.listInterestClassesRaw(requestParameters);
        return await response.value();
    }

}
