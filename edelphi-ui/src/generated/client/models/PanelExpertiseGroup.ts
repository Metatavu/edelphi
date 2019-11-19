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
/**
 * 
 * @export
 * @interface PanelExpertiseGroup
 */
export interface PanelExpertiseGroup {
    /**
     * Id
     * @type {number}
     * @memberof PanelExpertiseGroup
     */
    readonly id?: number;
    /**
     * 
     * @type {number}
     * @memberof PanelExpertiseGroup
     */
    interestClassId: number;
    /**
     * 
     * @type {number}
     * @memberof PanelExpertiseGroup
     */
    expertiseClassId: number;
}

export function PanelExpertiseGroupFromJSON(json: any): PanelExpertiseGroup {
    return PanelExpertiseGroupFromJSONTyped(json, false);
}

export function PanelExpertiseGroupFromJSONTyped(json: any, ignoreDiscriminator: boolean): PanelExpertiseGroup {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'interestClassId': json['interestClassId'],
        'expertiseClassId': json['expertiseClassId'],
    };
}

export function PanelExpertiseGroupToJSON(value?: PanelExpertiseGroup | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'interestClassId': value.interestClassId,
        'expertiseClassId': value.expertiseClassId,
    };
}

