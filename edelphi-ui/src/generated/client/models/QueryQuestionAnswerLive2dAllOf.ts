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

import { exists, mapValues } from '../runtime';
import {
    QueryQuestionLive2dAnswerData,
    QueryQuestionLive2dAnswerDataFromJSON,
    QueryQuestionLive2dAnswerDataFromJSONTyped,
    QueryQuestionLive2dAnswerDataToJSON,
} from './';

/**
 * 
 * @export
 * @interface QueryQuestionAnswerLive2dAllOf
 */
export interface QueryQuestionAnswerLive2dAllOf {
    /**
     * 
     * @type {QueryQuestionLive2dAnswerData}
     * @memberof QueryQuestionAnswerLive2dAllOf
     */
    data: QueryQuestionLive2dAnswerData;
}

export function QueryQuestionAnswerLive2dAllOfFromJSON(json: any): QueryQuestionAnswerLive2dAllOf {
    return QueryQuestionAnswerLive2dAllOfFromJSONTyped(json, false);
}

export function QueryQuestionAnswerLive2dAllOfFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryQuestionAnswerLive2dAllOf {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'data': QueryQuestionLive2dAnswerDataFromJSON(json['data']),
    };
}

export function QueryQuestionAnswerLive2dAllOfToJSON(value?: QueryQuestionAnswerLive2dAllOf | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'data': QueryQuestionLive2dAnswerDataToJSON(value.data),
    };
}


