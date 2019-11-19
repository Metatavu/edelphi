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
 * @interface QueryPageLive2dAllOf
 */
export interface QueryPageLive2dAllOf {
    /**
     * 
     * @type {QueryPageLive2DAnswersVisibleOption}
     * @memberof QueryPageLive2dAllOf
     */
    answersVisible?: QueryPageLive2DAnswersVisibleOption;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2dAllOf
     */
    axisX?: QueryPageLive2DAxis;
    /**
     * 
     * @type {QueryPageLive2DAxis}
     * @memberof QueryPageLive2dAllOf
     */
    axisY?: QueryPageLive2DAxis;
}

export function QueryPageLive2dAllOfFromJSON(json: any): QueryPageLive2dAllOf {
    return QueryPageLive2dAllOfFromJSONTyped(json, false);
}

export function QueryPageLive2dAllOfFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageLive2dAllOf {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'answersVisible': !exists(json, 'answersVisible') ? undefined : QueryPageLive2DAnswersVisibleOptionFromJSON(json['answersVisible']),
        'axisX': !exists(json, 'axisX') ? undefined : QueryPageLive2DAxisFromJSON(json['axisX']),
        'axisY': !exists(json, 'axisY') ? undefined : QueryPageLive2DAxisFromJSON(json['axisY']),
    };
}

export function QueryPageLive2dAllOfToJSON(value?: QueryPageLive2dAllOf | null): any {
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


