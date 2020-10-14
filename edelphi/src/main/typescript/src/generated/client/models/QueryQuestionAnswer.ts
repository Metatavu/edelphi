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
 * @interface QueryQuestionAnswer
 */
export interface QueryQuestionAnswer {
    /**
     * Id of the query answer. Id is a composed from queryPageId and queryReplyId by joining them with minus sign (e.g. 123-456)
     * @type {string}
     * @memberof QueryQuestionAnswer
     */
    readonly id?: string;
    /**
     * 
     * @type {number}
     * @memberof QueryQuestionAnswer
     */
    queryPageId: number;
    /**
     * 
     * @type {number}
     * @memberof QueryQuestionAnswer
     */
    queryReplyId: number;
    /**
     * 
     * @type {QueryQuestionLive2dAnswerData}
     * @memberof QueryQuestionAnswer
     */
    data: QueryQuestionLive2dAnswerData;
}

export function QueryQuestionAnswerFromJSON(json: any): QueryQuestionAnswer {
    return QueryQuestionAnswerFromJSONTyped(json, false);
}

export function QueryQuestionAnswerFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryQuestionAnswer {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'queryPageId': json['queryPageId'],
        'queryReplyId': json['queryReplyId'],
        'data': QueryQuestionLive2dAnswerDataFromJSON(json['data']),
    };
}

export function QueryQuestionAnswerToJSON(value?: QueryQuestionAnswer | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'queryPageId': value.queryPageId,
        'queryReplyId': value.queryReplyId,
        'data': QueryQuestionLive2dAnswerDataToJSON(value.data),
    };
}


