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
/**
 * 
 * @export
 * @interface PanelInterestClass
 */
export interface PanelInterestClass {
    /**
     * Id
     * @type {number}
     * @memberof PanelInterestClass
     */
    readonly id?: number;
    /**
     * Interest class name
     * @type {string}
     * @memberof PanelInterestClass
     */
    name: string;
}

export function PanelInterestClassFromJSON(json: any): PanelInterestClass {
    return PanelInterestClassFromJSONTyped(json, false);
}

export function PanelInterestClassFromJSONTyped(json: any, ignoreDiscriminator: boolean): PanelInterestClass {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': json['name'],
    };
}

export function PanelInterestClassToJSON(value?: PanelInterestClass | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'name': value.name,
    };
}


