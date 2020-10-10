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
    QueryPageLive2DAnswersVisibleOption,
    QueryPageLive2DAnswersVisibleOptionFromJSON,
    QueryPageLive2DAnswersVisibleOptionFromJSONTyped,
    QueryPageLive2DAnswersVisibleOptionToJSON,
    QueryPageLive2DAxis,
    QueryPageLive2DAxisFromJSON,
    QueryPageLive2DAxisFromJSONTyped,
    QueryPageLive2DAxisToJSON,
} from './';

/**
 * 
 * @export
 * @interface QueryPageLive2DOptions
 */
export interface QueryPageLive2DOptions {
    /**
     * 
     * @type {QueryPageLive2DAnswersVisibleOption}
     * @memberof QueryPageLive2DOptions
     */
    answersVisible?: QueryPageLive2DAnswersVisibleOption;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2DOptions
     */
    axisX?: QueryPageLive2DAxis;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2DOptions
     */
    axisY?: QueryPageLive2DAxis;
}

export function QueryPageLive2DOptionsFromJSON(json: any): QueryPageLive2DOptions {
    return QueryPageLive2DOptionsFromJSONTyped(json, false);
}

export function QueryPageLive2DOptionsFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageLive2DOptions {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'answersVisible': !exists(json, 'answersVisible') ? undefined : QueryPageLive2DAnswersVisibleOptionFromJSON(json['answersVisible']),
        'axisX': !exists(json, 'axisX') ? undefined : QueryPageLive2DAxisFromJSON(json['axisX']),
        'axisY': !exists(json, 'axisY') ? undefined : QueryPageLive2DAxisFromJSON(json['axisY']),
    };
}

export function QueryPageLive2DOptionsToJSON(value?: QueryPageLive2DOptions | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'answersVisible': QueryPageLive2DAnswersVisibleOptionToJSON(value.answersVisible),
        'axisX': QueryPageLive2DAxisToJSON(value.axisX),
        'axisY': QueryPageLive2DAxisToJSON(value.axisY),
    };
}

