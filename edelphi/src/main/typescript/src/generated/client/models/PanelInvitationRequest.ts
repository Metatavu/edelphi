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
 * @interface PanelInvitationRequest
 */
export interface PanelInvitationRequest {
    /**
     * List of emails the invitation will be sent to
     * @type {Array<string>}
     * @memberof PanelInvitationRequest
     */
    emails: Array<string>;
    /**
     * Inviation email content
     * @type {string}
     * @memberof PanelInvitationRequest
     */
    invitationMessage?: string;
    /**
     * Specify target query for invitation link. If this is left blank, link will lead to panel index page
     * @type {number}
     * @memberof PanelInvitationRequest
     */
    targetQueryId?: number;
    /**
     * If skip invitation is true, users will be added directly without invitation
     * @type {boolean}
     * @memberof PanelInvitationRequest
     */
    skipInvitation: boolean;
    /**
     * Initial password for users. This field is used only when skipInvitation is true
     * @type {string}
     * @memberof PanelInvitationRequest
     */
    password?: string;
}

export function PanelInvitationRequestFromJSON(json: any): PanelInvitationRequest {
    return PanelInvitationRequestFromJSONTyped(json, false);
}

export function PanelInvitationRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): PanelInvitationRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'emails': json['emails'],
        'invitationMessage': !exists(json, 'invitationMessage') ? undefined : json['invitationMessage'],
        'targetQueryId': !exists(json, 'targetQueryId') ? undefined : json['targetQueryId'],
        'skipInvitation': json['skipInvitation'],
        'password': !exists(json, 'password') ? undefined : json['password'],
    };
}

export function PanelInvitationRequestToJSON(value?: PanelInvitationRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'emails': value.emails,
        'invitationMessage': value.invitationMessage,
        'targetQueryId': value.targetQueryId,
        'skipInvitation': value.skipInvitation,
        'password': value.password,
    };
}


