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
 * @interface PanelExpertiseClass
 */
export interface PanelExpertiseClass {
    /**
     * Id
     * @type {number}
     * @memberof PanelExpertiseClass
     */
    readonly id?: number;
    /**
     * Expertise class name
     * @type {string}
     * @memberof PanelExpertiseClass
     */
    name: string;
}

export function PanelExpertiseClassFromJSON(json: any): PanelExpertiseClass {
    return PanelExpertiseClassFromJSONTyped(json, false);
}

export function PanelExpertiseClassFromJSONTyped(json: any, ignoreDiscriminator: boolean): PanelExpertiseClass {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': json['name'],
    };
}

export function PanelExpertiseClassToJSON(value?: PanelExpertiseClass | null): any {
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


