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
    QueryQuestionAnswer,
    QueryQuestionAnswerFromJSON,
    QueryQuestionAnswerFromJSONTyped,
    QueryQuestionAnswerToJSON,
    QueryQuestionAnswerLive2dAllOf,
    QueryQuestionAnswerLive2dAllOfFromJSON,
    QueryQuestionAnswerLive2dAllOfFromJSONTyped,
    QueryQuestionAnswerLive2dAllOfToJSON,
    QueryQuestionLive2dAnswerData,
    QueryQuestionLive2dAnswerDataFromJSON,
    QueryQuestionLive2dAnswerDataFromJSONTyped,
    QueryQuestionLive2dAnswerDataToJSON,
} from './';

/**
 * 
 * @export
 * @interface QueryQuestionAnswerLive2d
 */
export interface QueryQuestionAnswerLive2d {
    /**
     * Id of the query answer. Id is a composed from queryPageId and queryReplyId by joining them with minus sign (e.g. 123-456)
     * @type {string}
     * @memberof QueryQuestionAnswerLive2d
     */
    readonly id?: string;
    /**
     * 
     * @type {number}
     * @memberof QueryQuestionAnswerLive2d
     */
    queryPageId: number;
    /**
     * 
     * @type {number}
     * @memberof QueryQuestionAnswerLive2d
     */
    queryReplyId: number;
    /**
     * 
     * @type {QueryQuestionLive2dAnswerData}
     * @memberof QueryQuestionAnswerLive2d
     */
    data: QueryQuestionLive2dAnswerData;
}

export function QueryQuestionAnswerLive2dFromJSON(json: any): QueryQuestionAnswerLive2d {
    return QueryQuestionAnswerLive2dFromJSONTyped(json, false);
}

export function QueryQuestionAnswerLive2dFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryQuestionAnswerLive2d {
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

export function QueryQuestionAnswerLive2dToJSON(value?: QueryQuestionAnswerLive2d | null): any {
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


