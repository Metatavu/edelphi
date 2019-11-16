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

/**
 * 
 * @export
 * @enum {string}
 */
export enum QueryPageLive2DAnswersVisibleOption {
    IMMEDIATELY = 'IMMEDIATELY',
    AFTEROWNANSWER = 'AFTER_OWN_ANSWER'
}

export function QueryPageLive2DAnswersVisibleOptionFromJSON(json: any): QueryPageLive2DAnswersVisibleOption {
    return QueryPageLive2DAnswersVisibleOptionFromJSONTyped(json, false);
}

export function QueryPageLive2DAnswersVisibleOptionFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageLive2DAnswersVisibleOption {
    return json as QueryPageLive2DAnswersVisibleOption;
}

export function QueryPageLive2DAnswersVisibleOptionToJSON(value?: QueryPageLive2DAnswersVisibleOption | null): any {
    return value as any;
}

