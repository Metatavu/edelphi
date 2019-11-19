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
    QueryPage,
    QueryPageFromJSON,
    QueryPageFromJSONTyped,
    QueryPageToJSON,
    QueryPageCommentOptions,
    QueryPageCommentOptionsFromJSON,
    QueryPageCommentOptionsFromJSONTyped,
    QueryPageCommentOptionsToJSON,
    QueryPageLive2DAnswersVisibleOption,
    QueryPageLive2DAnswersVisibleOptionFromJSON,
    QueryPageLive2DAnswersVisibleOptionFromJSONTyped,
    QueryPageLive2DAnswersVisibleOptionToJSON,
    QueryPageLive2DAxis,
    QueryPageLive2DAxisFromJSON,
    QueryPageLive2DAxisFromJSONTyped,
    QueryPageLive2DAxisToJSON,
    QueryPageLive2dAllOf,
    QueryPageLive2dAllOfFromJSON,
    QueryPageLive2dAllOfFromJSONTyped,
    QueryPageLive2dAllOfToJSON,
    QueryPageType,
    QueryPageTypeFromJSON,
    QueryPageTypeFromJSONTyped,
    QueryPageTypeToJSON,
} from './';

/**
 * 
 * @export
 * @interface QueryPageLive2d
 */
export interface QueryPageLive2d {
    /**
     * 
     * @type {number}
     * @memberof QueryPageLive2d
     */
    readonly id?: number;
    /**
     * 
     * @type {number}
     * @memberof QueryPageLive2d
     */
    pageNumber: number;
    /**
     * 
     * @type {string}
     * @memberof QueryPageLive2d
     */
    title: string;
    /**
     * 
     * @type {QueryPageType}
     * @memberof QueryPageLive2d
     */
    type: QueryPageType;
    /**
     * 
     * @type {QueryPageCommentOptions}
     * @memberof QueryPageLive2d
     */
    commentOptions: QueryPageCommentOptions;
    /**
     * 
     * @type {QueryPageLive2DAnswersVisibleOption}
     * @memberof QueryPageLive2d
     */
    answersVisible?: QueryPageLive2DAnswersVisibleOption;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2d
     */
    axisX?: QueryPageLive2DAxis;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2d
     */
    axisY?: QueryPageLive2DAxis;
}

export function QueryPageLive2dFromJSON(json: any): QueryPageLive2d {
    return QueryPageLive2dFromJSONTyped(json, false);
}

export function QueryPageLive2dFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageLive2d {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'pageNumber': json['pageNumber'],
        'title': json['title'],
        'type': QueryPageTypeFromJSON(json['type']),
        'commentOptions': QueryPageCommentOptionsFromJSON(json['commentOptions']),
        'answersVisible': !exists(json, 'answersVisible') ? undefined : QueryPageLive2DAnswersVisibleOptionFromJSON(json['answersVisible']),
        'axisX': !exists(json, 'axisX') ? undefined : QueryPageLive2DAxisFromJSON(json['axisX']),
        'axisY': !exists(json, 'axisY') ? undefined : QueryPageLive2DAxisFromJSON(json['axisY']),
    };
}

export function QueryPageLive2dToJSON(value?: QueryPageLive2d | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'pageNumber': value.pageNumber,
        'title': value.title,
        'type': QueryPageTypeToJSON(value.type),
        'commentOptions': QueryPageCommentOptionsToJSON(value.commentOptions),
        'answersVisible': QueryPageLive2DAnswersVisibleOptionToJSON(value.answersVisible),
        'axisX': QueryPageLive2DAxisToJSON(value.axisX),
        'axisY': QueryPageLive2DAxisToJSON(value.axisY),
    };
}

