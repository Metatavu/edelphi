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

/**
 * 
 * @export
 * @enum {string}
 */
export enum QueryPageType {
    TEXT = 'TEXT',
    FORM = 'FORM',
    EXPERTISE = 'EXPERTISE',
    THESISSCALE1D = 'THESIS_SCALE_1D',
    THESISSCALE2D = 'THESIS_SCALE_2D',
    THESISORDER = 'THESIS_ORDER',
    THESISTIMESERIE = 'THESIS_TIME_SERIE',
    THESISMULTISELECT = 'THESIS_MULTI_SELECT',
    THESISTIMELINE = 'THESIS_TIMELINE',
    THESISGROUPING = 'THESIS_GROUPING',
    THESISMULTIPLE1DSCALES = 'THESIS_MULTIPLE_1D_SCALES',
    THESISMULTIPLE2DSCALES = 'THESIS_MULTIPLE_2D_SCALES',
    LIVE2D = 'LIVE_2D',
    COLLAGE2D = 'COLLAGE_2D'
}

export function QueryPageTypeFromJSON(json: any): QueryPageType {
    return QueryPageTypeFromJSONTyped(json, false);
}

export function QueryPageTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageType {
    return json as QueryPageType;
}

export function QueryPageTypeToJSON(value?: QueryPageType | null): any {
    return value as any;
}

