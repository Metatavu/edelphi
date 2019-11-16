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
    ReportDelivery,
    ReportDeliveryFromJSON,
    ReportDeliveryFromJSONTyped,
    ReportDeliveryToJSON,
    ReportFormat,
    ReportFormatFromJSON,
    ReportFormatFromJSONTyped,
    ReportFormatToJSON,
    ReportRequestOptions,
    ReportRequestOptionsFromJSON,
    ReportRequestOptionsFromJSONTyped,
    ReportRequestOptionsToJSON,
    ReportType,
    ReportTypeFromJSON,
    ReportTypeFromJSONTyped,
    ReportTypeToJSON,
} from './';

/**
 * 
 * @export
 * @interface ReportRequest
 */
export interface ReportRequest {
    /**
     * 
     * @type {number}
     * @memberof ReportRequest
     */
    panel_id: number;
    /**
     * 
     * @type {number}
     * @memberof ReportRequest
     */
    query_id: number;
    /**
     * Panel stamp id. Defaults to current stamp
     * @type {number}
     * @memberof ReportRequest
     */
    stamp_id?: number;
    /**
     * 
     * @type {ReportType}
     * @memberof ReportRequest
     */
    type: ReportType;
    /**
     * 
     * @type {ReportFormat}
     * @memberof ReportRequest
     */
    format: ReportFormat;
    /**
     * 
     * @type {ReportDelivery}
     * @memberof ReportRequest
     */
    delivery?: ReportDelivery;
    /**
     * 
     * @type {ReportRequestOptions}
     * @memberof ReportRequest
     */
    options: ReportRequestOptions;
}

export function ReportRequestFromJSON(json: any): ReportRequest {
    return ReportRequestFromJSONTyped(json, false);
}

export function ReportRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReportRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'panel_id': json['panelId'],
        'query_id': json['queryId'],
        'stamp_id': !exists(json, 'stampId') ? undefined : json['stampId'],
        'type': ReportTypeFromJSON(json['type']),
        'format': ReportFormatFromJSON(json['format']),
        'delivery': !exists(json, 'delivery') ? undefined : ReportDeliveryFromJSON(json['delivery']),
        'options': ReportRequestOptionsFromJSON(json['options']),
    };
}

export function ReportRequestToJSON(value?: ReportRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'panelId': value.panel_id,
        'queryId': value.query_id,
        'stampId': value.stamp_id,
        'type': ReportTypeToJSON(value.type),
        'format': ReportFormatToJSON(value.format),
        'delivery': ReportDeliveryToJSON(value.delivery),
        'options': ReportRequestOptionsToJSON(value.options),
    };
}


