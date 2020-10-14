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
export enum ReportFormat {
    PDF = 'PDF',
    CSV = 'CSV',
    PNG = 'PNG',
    GOOGLESHEET = 'GOOGLE_SHEET',
    GOOGLEDOCUMENT = 'GOOGLE_DOCUMENT'
}

export function ReportFormatFromJSON(json: any): ReportFormat {
    return ReportFormatFromJSONTyped(json, false);
}

export function ReportFormatFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReportFormat {
    return json as ReportFormat;
}

export function ReportFormatToJSON(value?: ReportFormat | null): any {
    return value as any;
}

