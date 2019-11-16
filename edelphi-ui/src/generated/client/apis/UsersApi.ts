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
    User,
    UserFromJSON,
    UserToJSON,
} from '../models';

export interface FindUserRequest {
    user_id: string;
}

/**
 * no description
 */
export class UsersApi extends runtime.BaseAPI {

    /**
     * Finds an user by id
     * Find user
     */
    async findUserRaw(requestParameters: FindUserRequest): Promise<runtime.ApiResponse<User>> {
        if (requestParameters.user_id === null || requestParameters.user_id === undefined) {
            throw new runtime.RequiredError('user_id','Required parameter requestParameters.user_id was null or undefined when calling findUser.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/users/{userId}`.replace(`{${"userId"}}`, encodeURIComponent(String(requestParameters.user_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => UserFromJSON(jsonValue));
    }

    /**
     * Finds an user by id
     * Find user
     */
    async findUser(requestParameters: FindUserRequest): Promise<User> {
        const response = await this.findUserRaw(requestParameters);
        return await response.value();
    }

}
